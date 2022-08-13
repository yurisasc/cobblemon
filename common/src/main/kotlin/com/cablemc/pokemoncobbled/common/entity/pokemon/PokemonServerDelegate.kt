package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.CobbledSounds
import com.cablemc.pokemoncobbled.common.api.entity.PokemonSideDelegate
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemoncobbled.common.pokemon.activestate.SentOutState
import com.cablemc.pokemoncobbled.common.util.playSoundServer
import net.minecraft.entity.Entity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

/** Handles purely server logic for a Pokémon */
class PokemonServerDelegate : PokemonSideDelegate {
    lateinit var entity: PokemonEntity
    override fun changePokemon(pokemon: Pokemon) {
        entity.initGoals()
    }

    override fun initialize(entity: PokemonEntity) {
        this.entity = entity
        with(entity) {
            speed = 0.35F
            entity.despawner.beginTracking(this)
        }
    }

    override fun tick(entity: PokemonEntity) {
        val state = entity.pokemon.state
        if (state !is ActivePokemonState || state.entity != entity) {
            if (!entity.isDead && entity.health > 0) {
                entity.pokemon.state = SentOutState(entity)
            }
        }

        if (entity.ticksLived % 20 == 0) {
            val activeBattlePokemon = entity.battleId.get().orElse(null)?.let { BattleRegistry.getBattle(it) }
                ?.activePokemon
                ?.find { it.battlePokemon?.uuid == entity.pokemon.uuid }

            if (activeBattlePokemon != null) {
                activeBattlePokemon.position = entity.world as ServerWorld to entity.pos
            }
        }

        if (entity.health.toInt() != entity.pokemon.currentHealth && entity.health > 0) {
            entity.health = entity.pokemon.currentHealth.toFloat()
        }
        if (entity.ownerUuid != entity.pokemon.getOwnerUUID()) {
            entity.ownerUuid = entity.pokemon.getOwnerUUID()
        }
        if (entity.pokemon.species.nationalPokedexNumber != entity.dexNumber.get()) {
            entity.dexNumber.set(entity.pokemon.species.nationalPokedexNumber)
        }
        if (entity.aspects.get() != entity.pokemon.aspects) {
            entity.aspects.set(entity.pokemon.aspects)
        }
        val isMoving = entity.velocity.length() > 0.1
        if (isMoving && !entity.isMoving.get()) {
            entity.isMoving.set(true)
        } else if (!isMoving && entity.isMoving.get()) {
            entity.isMoving.set(false)
        }

        updatePoseType()
    }

    fun updatePoseType() {
        val isSleeping = entity.getBehaviourFlag(PokemonBehaviourFlag.RESTING)
        val isMoving = entity.isMoving.get()
        val isUnderwater = entity.getIsSubmerged()
        val isFlying = entity.getBehaviourFlag(PokemonBehaviourFlag.FLYING)

        val poseType = when {
            isSleeping -> PoseType.SLEEP
            isMoving && isUnderwater  -> PoseType.SWIM
            isUnderwater -> PoseType.FLOAT
            isMoving && isFlying -> PoseType.FLY
            isFlying -> PoseType.HOVER
            isMoving -> PoseType.WALK
            else -> PoseType.STAND
        }

        if (poseType != entity.poseType.get()) {
            entity.poseType.set(poseType)
        }
    }

    override fun drop(source: DamageSource?) {
        val player = source?.source as? ServerPlayerEntity
        if (entity.pokemon.isWild()) {
            entity.killer = player
        }
    }

    override fun updatePostDeath() {
        if (!entity.deathEffectsStarted.get()) {
            entity.deathEffectsStarted.set(true)
        }
        ++entity.deathTime

        if (entity.deathTime == 60) {
            val owner = entity.owner
            if (owner != null) {
                entity.world.playSoundServer(owner.pos, CobbledSounds.RECALL.get(), volume = 0.2F)
                entity.phasingTargetId.set(owner.id)
                entity.beamModeEmitter.set(2)
            }
        }

        if (entity.deathTime == 120) {
            if (entity.owner == null) {
                entity.world.sendEntityStatus(entity, 60.toByte()) // Sends smoke effect
                (entity.drops ?: entity.pokemon.form.drops).drop(entity, entity.world as ServerWorld, entity.pos, entity.killer)
            }

            entity.remove(Entity.RemovalReason.KILLED)
        }
    }
}