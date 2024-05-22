/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.ai

import com.cobblemon.mod.common.CobblemonActivities
import com.cobblemon.mod.common.CobblemonMemories
import com.cobblemon.mod.common.CobblemonSensors
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.entity.ai.*
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.entity.pokemon.ai.tasks.*
import com.cobblemon.mod.common.entity.pokemon.ai.tasks.WakeUpTask
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.toDF
import com.google.common.collect.ImmutableList
import com.mojang.datafixers.util.Pair
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.entity.ai.brain.Brain
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.sensor.Sensor
import net.minecraft.entity.ai.brain.sensor.SensorType
import net.minecraft.entity.ai.brain.task.*
import net.minecraft.util.math.intprovider.UniformIntProvider

// brain sensors / memory definitions split from PokemonEntity
// to better represent vanillas layout.
object PokemonBrain {

    private val ADULT_FOLLOW_RANGE = UniformIntProvider.create(5, 16)

    val SENSORS: Collection<SensorType<out Sensor<in PokemonEntity>>> = listOf(
        SensorType.NEAREST_LIVING_ENTITIES,
        SensorType.HURT_BY,
        SensorType.NEAREST_PLAYERS,
        CobblemonSensors.POKEMON_DISTURBANCE,
        CobblemonSensors.POKEMON_DROWSY,
        CobblemonSensors.POKEMON_ADULT,
        SensorType.IS_IN_WATER,

//            CobblemonSensors.BATTLING_POKEMON,
//            CobblemonSensors.NPC_BATTLING
    )

    val MEMORY_MODULES: List<MemoryModuleType<*>> = ImmutableList.of(
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.ATTACK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.PATH,
        MemoryModuleType.IS_PANICKING,
        MemoryModuleType.VISIBLE_MOBS,
        CobblemonMemories.POKEMON_FLYING,
//            CobblemonMemories.NPC_BATTLING,
//            CobblemonMemories.BATTLING_POKEMON,
        MemoryModuleType.HURT_BY,
        MemoryModuleType.HURT_BY_ENTITY,
        MemoryModuleType.NEAREST_VISIBLE_PLAYER,
        MemoryModuleType.ANGRY_AT,
        MemoryModuleType.ATTACK_COOLING_DOWN,
        CobblemonMemories.POKEMON_DROWSY,
        CobblemonMemories.POKEMON_BATTLE,
        MemoryModuleType.HOME,
        CobblemonMemories.REST_PATH_COOLDOWN,
        CobblemonMemories.TARGETED_BATTLE_POKEMON,
        MemoryModuleType.NEAREST_VISIBLE_ADULT,
        MemoryModuleType.IS_IN_WATER,
        MemoryModuleType.DISTURBANCE_LOCATION
    )

    fun makeBrain(pokemon: Pokemon, brain: Brain<out PokemonEntity>): Brain<*> {
        brain.setTaskList(
            Activity.CORE,
            ImmutableList.copyOf(coreTasks(pokemon))
        )
        brain.setTaskList(
            Activity.IDLE,
            ImmutableList.copyOf(idleTasks(pokemon))
        )
        brain.setTaskList(
            CobblemonActivities.BATTLING_ACTIVITY,
            ImmutableList.copyOf(battlingTasks())
        )
        brain.setTaskList(
            CobblemonActivities.POKEMON_SLEEPING_ACTIVITY,
            ImmutableList.copyOf(sleepingTasks())
        )

        brain.setCoreActivities(setOf(Activity.CORE))
        brain.setDefaultActivity(Activity.IDLE)
        brain.resetPossibleActivities()

        return brain
    }

    private fun coreTasks(pokemon: Pokemon) = buildList<Pair<Int, Task<in PokemonEntity>>> {
        if (!pokemon.form.behaviour.moving.swim.canBreatheUnderwater) {
            add(0 toDF StayAfloatTask(0.8F))
        }

        add(0 toDF GetAngryAtAttackerTask.create())
        add(0 toDF ForgetAngryAtTargetTask.create())
        add(0 toDF HandleBattleActivityGoal.create())
        add(0 toDF FollowWalkTargetTask())
    }


    private fun idleTasks(pokemon: Pokemon) = buildList<Pair<Int, Task<in PokemonEntity>>> {
        if (pokemon.form.behaviour.moving.canLook) {
            if (pokemon.form.behaviour.moving.looksAtEntities) {
                add(0 toDF LookAtMobTask.create(15F))
            }

            add(0 toDF LookAroundTask(45, 90))
        }

        add(0 toDF ChooseLandWanderTargetTask.create(pokemon.form.behaviour.moving.wanderChance, horizontalRange = 10, verticalRange = 5, walkSpeed = 0.33F, completionRange = 1))
        add(0 toDF GoToSleepTask.create())
        add(0 toDF FindRestingPlaceTask.create(16, 8))
        add(0 toDF EatGrassTask())
        add(0 toDF AttackAngryAtTask.create())
        add(0 toDF MoveToAttackTargetTask.create())
        add(0 toDF MoveToOwnerTask.create(completionRange = 4, maxDistance = 14F, teleportDistance = 24F))
        add(0 toDF WalkTowardsParentSpeciesTask.create(ADULT_FOLLOW_RANGE, 0.4f))
        add(0 toDF HuntPlayerTask())

        if (pokemon.primaryType == ElementalTypes.GRASS) {
            add(1 toDF FertilizerTask())
        }
        if (pokemon.primaryType == ElementalTypes.FIRE) {
            add(1 toDF IgniteTask())
        }
        if (pokemon.form.behaviour.moving.swim.canBreatheUnderwater) {
            add(0 toDF ChooseWaterWanderTargetTask.create(pokemon.form.behaviour.moving.wanderChance, horizontalRange = 10, verticalRange = 5, swimSpeed = 0.33F, completionRange = 1))
            add(1 toDF JumpOutOfWaterTask())
        }
        if (pokemon.form.behaviour.moving.fly.canFly) {
            add(0 toDF ChooseFlightWanderTargetTask.create(pokemon.form.behaviour.moving.wanderChance, horizontalRange = 10, verticalRange = 5, flySpeed = 0.33F, completionRange = 1))
        }
    }

    private fun battlingTasks() = buildList<Pair<Int, Task<in PokemonEntity>>> {
        add(0 toDF LookAtTargetedBattlePokemonTask.create())
        add(0 toDF LookAroundTask(Int.MAX_VALUE - 1, Int.MAX_VALUE - 1))
    }

    private fun sleepingTasks() = buildList<Pair<Int, Task<in PokemonEntity>>> {
        add(1 toDF WakeUpTask.create())
    }

}