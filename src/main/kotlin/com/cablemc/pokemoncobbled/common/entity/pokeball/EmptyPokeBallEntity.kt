package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.api.scheduling.taskBuilder
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.isServerSide
import com.cablemc.pokemoncobbled.common.util.math.toRotations
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.core.Rotations
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth.PI
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class EmptyPokeBallEntity(
    pokeBall: PokeBall,
    entityType: EntityType<out EmptyPokeBallEntity>,
    level: Level
) : PokeBallEntity(pokeBall, entityType, level) {
    enum class CaptureState {
        NOT,
        HIT,
        FALL,
        SHAKE
    }

    companion object {
        private val CAPTURE_STATE = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, EntityDataSerializers.BYTE)
        private val HIT_TARGET_POSITION = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, EntityDataSerializers.ROTATIONS)
        private val HIT_VELOCITY = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, EntityDataSerializers.ROTATIONS)
        private val SHAKE = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, EntityDataSerializers.BOOLEAN)

        const val TICKS_BETWEEN_SHAKES = 40
        const val TICKS_BEFORE_SHAKE = 10
    }

    var capturingPokemon: PokemonEntity? = null
    val captureState = addEntityProperty(CAPTURE_STATE, CaptureState.NOT.ordinal.toByte())
    val hitTargetPosition = addEntityProperty(HIT_TARGET_POSITION, Rotations(0F, 0F, 0F))
    val hitVelocity = addEntityProperty(HIT_VELOCITY, Rotations(0F, 0F, 0F))
    val shakeEmitter = addEntityProperty(SHAKE, false)

    override val delegate = if (level.isClientSide) {
        com.cablemc.pokemoncobbled.client.entity.EmptyPokeBallClientDelegate()
    } else {
        EmptyPokeBallServerDelegate()
    }

    init {
        delegate.initialize(this)
        captureState.subscribe { state ->
            when (CaptureState.values()[state.toInt()]) {
                CaptureState.NOT -> isNoGravity = false
                CaptureState.HIT -> {}
                CaptureState.FALL -> isNoGravity = false
                CaptureState.SHAKE -> isNoGravity = false
            }
        }
    }

    private var isAttemptingCatch = false

    constructor(entityType: EntityType<out EmptyPokeBallEntity>, level: Level) : this(PokeBalls.POKE_BALL, entityType, level)
    constructor(pokeBall: PokeBall, level: Level) : this(pokeBall, EntityRegistry.EMPTY_POKEBALL.get(), level)

    override fun onHitBlock(hitResult: BlockHitResult) {
        if (captureState.get() == CaptureState.NOT.ordinal.toByte()) {
            if (level.isServerSide()) {
                super.onHitBlock(hitResult)
                kill()
                spawnAtLocation(defaultItem)
            }
        } else {
            isNoGravity = false
            deltaMovement = Vec3.ZERO
            isOnGround = true
        }
    }

    override fun onHitEntity(hitResult: EntityHitResult) {
        if (captureState.get() == CaptureState.NOT.ordinal.toByte()) {
            if (hitResult.entity is PokemonEntity && level.isServerSide()) {
                val pokemon = hitResult.entity as PokemonEntity
                if (pokemon.isBeingCaptured || !pokemon.pokemon.isWild()) {
                    kill()
                    return
                }
                capturingPokemon = pokemon
                hitVelocity.set(deltaMovement.normalize().toRotations())
                hitTargetPosition.set(hitResult.location.toRotations())
                attemptCatch(pokemon)
                return
            }
        }
        super.onHitEntity(hitResult)
    }

    override fun tick() {
        delegate.tick(this)
        super.tick()
        if (level.isServerSide()) {
            if (owner == null || !owner!!.isAlive || (captureState.get() != CaptureState.NOT.ordinal.toByte() && capturingPokemon?.isAlive != true)) {
                kill()
                return
            }

            if (isOnGround && captureState.get() == CaptureState.FALL.ordinal.toByte()) {
                captureState.set(CaptureState.SHAKE.ordinal.toByte())

                val captureResult = PokemonCobbledMod.captureCalculator.processCapture(owner as ServerPlayer, capturingPokemon!!.pokemon, pokeBall)

                var rollsRemaining = captureResult.numberOfShakes
                if (rollsRemaining == 4) {
                    rollsRemaining--
                }

                taskBuilder()
                    .iterations(captureResult.numberOfShakes + 1)
                    .delay(TICKS_BEFORE_SHAKE)
                    .interval(TICKS_BETWEEN_SHAKES)
                    .execute {
                        if (capturingPokemon?.isAlive != true) {
                            kill()
                        }

                        if (!isAlive) {
                            it.expire()
                            return@execute
                        }

                        if (rollsRemaining <= 0) {
                            if (captureResult.isSuccessfulCapture) {
                                // Do a capture
                                capturingPokemon!!.kill()
                                val party = PokemonStoreManager.getParty(this.owner!!.uuid)
                                party.add(capturingPokemon!!.pokemon)
                            } else {
                                breakFree()
                            }
                            return@execute
                        }

                        rollsRemaining--
                        shakeEmitter.set(!shakeEmitter.get())
                    }
                    .build()
            }
        }

    }

    private fun breakFree() {
        capturingPokemon!!.setPos(position())
        capturingPokemon!!.isInvisible = false
        capturingPokemon!!.isBeingCaptured = false
        kill()
    }

    private fun attemptCatch(pokemonEntity: PokemonEntity) {
        // TODO: Validate pokemon is not owned by a player
        if (!isAttemptingCatch) {
            isAttemptingCatch = true
            pokemonEntity.isBeingCaptured = true
            val displace = deltaMovement
            captureState.set(CaptureState.HIT.ordinal.toByte())
            val mul = if (random.nextBoolean()) 1 else -1
            deltaMovement = displace.multiply(-1.0, 0.0, -1.0).normalize().yRot(mul * PI/3).multiply(0.1, 0.0, 0.1).add(0.0, 1.0 / 3, 0.0)
            after(seconds = 0.7F) {
                deltaMovement = Vec3.ZERO
                isNoGravity = true
            }

            after(seconds = 2.2F) {
                pokemonEntity.isInvisible = true
                pokemonEntity.setPos(position())
                // TODO can we turn off collision or something
                captureState.set(CaptureState.FALL.ordinal.toByte())
            }
        }
    }
}