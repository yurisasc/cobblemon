package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.CobbledEntities
import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.CobbledSounds
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.net.serializers.Vec3DataSerializer
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.api.scheduling.taskBuilder
import com.cablemc.pokemoncobbled.common.entity.EntityProperty
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.asResource
import com.cablemc.pokemoncobbled.common.util.isServerSide
import com.cablemc.pokemoncobbled.common.util.playSoundServer
import com.cablemc.pokemoncobbled.common.util.sendParticlesServer
import dev.architectury.extensions.network.EntitySpawnExtension
import dev.architectury.networking.NetworkManager
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.Packet
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth.PI
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class EmptyPokeBallEntity(
    var pokeBall: PokeBall,
    level: Level,
    entityType: EntityType<out EmptyPokeBallEntity> = CobbledEntities.EMPTY_POKEBALL_TYPE
) : ThrowableItemProjectile(entityType, level), EntitySpawnExtension {
    enum class CaptureState {
        NOT,
        HIT,
        FALL,
        SHAKE
    }

    companion object {
        private val CAPTURE_STATE = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, EntityDataSerializers.BYTE)
        private val HIT_TARGET_POSITION = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, Vec3DataSerializer)
        private val HIT_VELOCITY = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, Vec3DataSerializer)
        private val SHAKE = SynchedEntityData.defineId(EmptyPokeBallEntity::class.java, EntityDataSerializers.BOOLEAN)

        const val SECONDS_BETWEEN_SHAKES = 2F
        const val SECONDS_BEFORE_SHAKE = 0.5F
    }

    val DIMENSIONS = EntityDimensions(0.4F, 0.4F, true)
    val entityProperties = mutableListOf<EntityProperty<*>>()

    var capturingPokemon: PokemonEntity? = null
    val captureState = addEntityProperty(CAPTURE_STATE, CaptureState.NOT.ordinal.toByte())
    val hitTargetPosition = addEntityProperty(HIT_TARGET_POSITION, Vec3.ZERO)
    val hitVelocity = addEntityProperty(HIT_VELOCITY, Vec3.ZERO)
    val shakeEmitter = addEntityProperty(SHAKE, false)

    val delegate = if (level.isClientSide) {
        com.cablemc.pokemoncobbled.common.client.entity.EmptyPokeBallClientDelegate()
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

    constructor(level: Level) : this(pokeBall = PokeBalls.POKE_BALL, level = level)

    override fun onHitBlock(hitResult: BlockHitResult) {
        if (captureState.get() == CaptureState.NOT.ordinal.toByte()) {
            if (level.isServerSide()) {
                super.onHitBlock(hitResult)
                discard()
                val player = this.owner as ServerPlayer
                if (!player.isCreative) spawnAtLocation(defaultItem)
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
                if (pokemon.isBusy || !pokemon.pokemon.isWild()) {
                    discard()
                    val player = this.owner as ServerPlayer
                    if (!player.isCreative) spawnAtLocation(defaultItem)
                    return
                }
                capturingPokemon = pokemon
                hitVelocity.set(deltaMovement.normalize())
                hitTargetPosition.set(hitResult.location)
                attemptCatch(pokemon)
                return
            }
        }
        super.onHitEntity(hitResult)
    }

    override fun tick() {
        delegate.tick(this)
        entityProperties.forEach { it.checkForUpdate() }
        super.tick()
        if (level.isServerSide()) {
            if (owner == null || !owner!!.isAlive || (captureState.get() != CaptureState.NOT.ordinal.toByte() && capturingPokemon?.isAlive != true)) {
                breakFree()
                discard()
                return
            }

            if (isOnGround && captureState.get() == CaptureState.FALL.ordinal.toByte()) {
                capturingPokemon?.setPos(position())
                captureState.set(CaptureState.SHAKE.ordinal.toByte())

                val captureResult = PokemonCobbled.captureCalculator.processCapture(owner as ServerPlayer, capturingPokemon!!.pokemon, pokeBall)

                var rollsRemaining = captureResult.numberOfShakes
                if (rollsRemaining == 4) {
                    rollsRemaining--
                }

                taskBuilder()
                    .iterations(captureResult.numberOfShakes + 1)
                    .delay(SECONDS_BEFORE_SHAKE)
                    .interval(SECONDS_BETWEEN_SHAKES)
                    .execute {
                        if (capturingPokemon?.isAlive != true) {
                            discard()
                        }

                        if (!isAlive) {
                            it.expire()
                            return@execute
                        }

                        if (rollsRemaining <= 0) {
                            if (captureResult.isSuccessfulCapture) {
                                // Do a capture
                                level.sendParticlesServer(ParticleTypes.CRIT, position(), 10, Vec3(0.1, -0.5, 0.1), 0.2)
                                level.playSoundServer(position(), CobbledSounds.CAPTURE_SUCCEEDED.get(), volume = 0.3F, pitch = 1F)
                                val pokemon = capturingPokemon ?: return@execute
                                val player = this.owner as? ServerPlayer ?: return@execute

                                after(seconds = 1F) {
                                    pokemon.discard()
                                    discard()
                                    val party = PokemonCobbled.storage.getParty(player.uuid)
                                    pokemon.pokemon.caughtBall = pokeBall
                                    party.add(pokemon.pokemon)
                                }

                                return@execute
                            } else {
                                breakFree()
                            }
                            return@execute
                        }

                        rollsRemaining--
                        level.playSoundServer(position(), CobbledSounds.POKEBALL_SHAKE.get())
                        shakeEmitter.set(!shakeEmitter.get())
                    }
                    .build()
            }
        }
    }

    private fun breakFree() {
        val pokemon = capturingPokemon ?: return
        pokemon.setPos(position())
        pokemon.beamModeEmitter.set(1)
        pokemon.isInvisible = false

        after(seconds = 0.25F) {
            pokemon.busyLocks.remove(this)
            level.sendParticlesServer(ParticleTypes.CLOUD, position(), 20, Vec3(0.0, 0.2, 0.0), 0.05)
            level.playSoundServer(position(), SoundEvents.GLASS_BREAK)
            discard()
        }
    }

    override fun getDefaultItem(): Item = CobbledItems.ballMap[pokeBall]?.get() as Item

    override fun getAddEntityPacket(): Packet<*> {
        return NetworkManager.createAddEntityPacket(this)
    }

    override fun getDimensions(pPose: Pose) = DIMENSIONS
    fun <T> addEntityProperty(accessor: EntityDataAccessor<T>, initialValue: T): EntityProperty<T> {
        val property = EntityProperty(
            entityData = entityData,
            accessor = accessor,
            initialValue = initialValue
        )
        entityProperties.add(property)
        return property
    }

    private fun attemptCatch(pokemonEntity: PokemonEntity) {
        pokemonEntity.busyLocks.add(this)
        val displace = deltaMovement
        captureState.set(CaptureState.HIT.ordinal.toByte())
        val mul = if (random.nextBoolean()) 1 else -1
        level.playSoundServer(position(), CobbledSounds.POKEBALL_HIT.get())
        deltaMovement = displace.multiply(-1.0, 0.0, -1.0).normalize().yRot(mul * PI/3).multiply(0.1, 0.0, 0.1).add(0.0, 1.0 / 3, 0.0)
        pokemonEntity.phasingTargetId.set(this.id)
        after(seconds = 0.7F) {
            deltaMovement = Vec3.ZERO
            isNoGravity = true
            level.playSoundServer(position(), CobbledSounds.CAPTURE_STARTED.get(), volume = 0.2F)
            pokemonEntity.beamModeEmitter.set(2.toByte())
        }

        after(seconds = 2.2F) {
            pokemonEntity.phasingTargetId.set(-1)
            pokemonEntity.beamModeEmitter.set(0.toByte())
            pokemonEntity.isInvisible = true
            captureState.set(CaptureState.FALL.ordinal.toByte())
        }
    }

    override fun saveAdditionalSpawnData(buf: FriendlyByteBuf) {
        buf.writeUtf(pokeBall.name.toString())
    }

    override fun loadAdditionalSpawnData(buf: FriendlyByteBuf) {
        pokeBall = PokeBalls.getPokeBall(buf.readUtf().asResource()) ?: PokeBalls.POKE_BALL
    }
}