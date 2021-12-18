package com.cablemc.pokemoncobbled.common.entity.pokeball

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.core.Rotations
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult

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
    }

    val captureState = addEntityProperty(CAPTURE_STATE, CaptureState.NOT.ordinal.toByte())
    val hitTargetPosition = addEntityProperty(HIT_TARGET_POSITION, Rotations(0F, 0F, 0F))

    init {
        delegate.initialize(this)
        captureState.subscribe { state ->
            when (CaptureState.values()[state.toInt()]) {
                CaptureState.NOT -> isNoGravity = false
                CaptureState.HIT -> {
                    isNoGravity = true
                    setDeltaMovement(0.0, 0.0, 0.0)
                }
                CaptureState.FALL -> isNoGravity = false
                CaptureState.SHAKE -> isNoGravity = false
            }
        }
    }

    private var isAttemptingCatch = false

    constructor(entityType: EntityType<out EmptyPokeBallEntity>, level: Level) : this(PokeBalls.POKE_BALL, entityType, level)

    constructor(pokeBall: PokeBall, level: Level) : this(pokeBall, EntityRegistry.EMPTY_POKEBALL.get(), level)

    override fun onHitBlock(hitResult: BlockHitResult) {
        super.onHitBlock(hitResult)
        kill()
        spawnAtLocation(defaultItem)
    }

    override fun onHitEntity(hitResult: EntityHitResult) {
        if (hitResult.entity is PokemonEntity) {
            attemptCatch(hitResult.entity as PokemonEntity)
        }
        super.onHitEntity(hitResult)
    }

    override fun tick() {
        delegate.tick(this)
//        if (isAttemptingCatch) {
//            setDeltaMovement(0.0, 0.0, 0.0)
//            when {
//                currentAnimation is OpenAnimation && (currentAnimation as OpenAnimation).isComplete() -> {
//                    currentAnimation = ShakeAnimation(3)
//                }
//            }
//        }
        super.tick()
    }

    private fun attemptCatch(pokemonEntity: PokemonEntity) {
        // TODO: Validate pokemon is not owned by a player
        if (!isAttemptingCatch) {
            isAttemptingCatch = true
            captureState.set(CaptureState.HIT.ordinal.toByte())
            pokemonEntity.isInvisible = true // TODO this is not the way
            after(seconds = 2F) { captureState.set(CaptureState.FALL.ordinal.toByte()) }
        }
    }
}