/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.entity.pokemon

import com.cablemc.pokemod.common.PokemodSounds
import com.cablemc.pokemod.common.api.entity.PokemonSideDelegate
import com.cablemc.pokemod.common.api.pokemon.stats.Stats
import com.cablemc.pokemod.common.battles.BattleRegistry
import com.cablemc.pokemod.common.entity.PoseType
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.activestate.ActivePokemonState
import com.cablemc.pokemod.common.pokemon.activestate.SentOutState
import com.cablemc.pokemod.common.util.playSoundServer
import net.minecraft.entity.Entity
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

/** Handles purely server logic for a Pokémon */
class PokemonServerDelegate : PokemonSideDelegate {
    lateinit var entity: PokemonEntity
    var acknowledgedHPStat = -1
    override fun changePokemon(pokemon: Pokemon) {
        entity.initGoals()
        updateMaxHealth()
    }

    fun updateMaxHealth() {
        val currentHealthRatio = entity.health.toDouble() / entity.maxHealth
        acknowledgedHPStat = entity.form.baseStats[Stats.HP]!!

        val minStat = 50 // Metapod's base HP
        val maxStat = 150 // Slaking's base HP
        val baseStat = acknowledgedHPStat.coerceIn(minStat..maxStat)
        val r = (baseStat - minStat) / (maxStat - minStat).toDouble()
        val minPossibleHP = 10.0 // half of a player's HP
        val maxPossibleHP = 100.0 // Iron Golem HP
        val maxHealth = minPossibleHP + r * (maxPossibleHP - minPossibleHP)

        entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)?.baseValue = maxHealth
        entity.health = currentHealthRatio.toFloat() * maxHealth.toFloat()
    }

    override fun initialize(entity: PokemonEntity) {
        this.entity = entity
        with(entity) {
            speed = 0.1F
            entity.despawner.beginTracking(this)
            subscriptions.add(behaviourFlags.subscribe { updatePoseType() })
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

        if (entity.form.baseStats[Stats.HP]!! != acknowledgedHPStat) {
            updateMaxHealth()
        }
        if (entity.ownerUuid != entity.pokemon.getOwnerUUID()) {
            entity.ownerUuid = entity.pokemon.getOwnerUUID()
        }
        if (entity.pokemon.species.resourceIdentifier.toString() != entity.species.get()) {
            entity.species.set(entity.pokemon.species.resourceIdentifier.toString())
        }
        if (entity.aspects.get() != entity.pokemon.aspects) {
            entity.aspects.set(entity.pokemon.aspects)
        }
        if (entity.labelLevel.get() != entity.pokemon.level) {
            entity.labelLevel.set(entity.pokemon.level)
        }
        val isMoving = !entity.navigation.isIdle
        if (isMoving && !entity.isMoving.get()) {
            entity.isMoving.set(true)
        } else if (!isMoving && entity.isMoving.get()) {
            entity.isMoving.set(false)
        }

        updatePoseType()
    }

    fun updatePoseType() {
        val isSleeping = entity.getBehaviourFlag(PokemonBehaviourFlag.SLEEPING)
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
                entity.world.playSoundServer(owner.pos, PokemodSounds.RECALL.get(), volume = 0.2F)
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