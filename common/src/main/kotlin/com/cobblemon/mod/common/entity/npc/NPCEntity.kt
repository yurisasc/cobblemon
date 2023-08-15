/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.net.serializers.IdentifierDataSerializer
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.net.serializers.StringSetDataSerializer
import com.cobblemon.mod.common.api.npc.NPCClasses
import com.cobblemon.mod.common.entity.EntityProperty
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import java.util.Optional
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Npc
import net.minecraft.entity.ai.goal.LookAroundGoal
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.World

class NPCEntity : PassiveEntity, Npc, Poseable {
    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder = createMobAttributes()
        val NPC_CLASS = DataTracker.registerData(NPCEntity::class.java, IdentifierDataSerializer)
        val ASPECTS = DataTracker.registerData(NPCEntity::class.java, StringSetDataSerializer)
        val POSE_TYPE = DataTracker.registerData(NPCEntity::class.java, PoseTypeDataSerializer)
        val BATTLE_ID = DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.OPTIONAL_UUID)
    }

    var npc = NPCClasses.random()
        set(value) {
            _npcClass.set(value.resourceIdentifier)
            field = value
        }

    val appliedAspects = mutableSetOf<String>()
    val entityProperties = mutableListOf<EntityProperty<*>>()

    val delegate = if (world.isClient) {
        com.cobblemon.mod.common.client.entity.NPCClientDelegate()
    } else {
        NPCServerDelegate()
    }

    private val _npcClass = addEntityProperty(NPC_CLASS, npc.resourceIdentifier)
    val aspects = addEntityProperty(ASPECTS, emptySet())
    val poseType = addEntityProperty(POSE_TYPE, PoseType.STAND)
    val battleId = addEntityProperty(BATTLE_ID, Optional.empty())


    /* TODO NPC Valuables to add:
     *
     * An 'interaction' configuration. This can be loaded from a JSON or API or even a .js (ambitious). Handles what happens
     * when you right click. Can be a dialogue tree with some complexity, or provides options to open a shopkeeper GUI,
     * that sort of deal. As extensible as we can manage it (and we can manage a lot).
     *
     * A 'party provider' configuration. This is for an NPC that's going to be used as a trainer. A stack of configuration
     * planning has been done by Vera and Design, get it from them and tweak to be clean.
     *
     * A pathing configuration. Another one that could be loaded from JSON or .js or API. Controls AI.
     *
     * npcs should be able to sleep lol
     */

    constructor(world: World): super(CobblemonEntities.NPC, world)

    override fun createChild(world: ServerWorld, entity: PassiveEntity) = null // No lovemaking! Unless...
    override fun getPoseType() = this.poseType.get()

    fun <T> addEntityProperty(accessor: TrackedData<T>, initialValue: T): EntityProperty<T> {
        val property = EntityProperty(
            dataTracker = dataTracker,
            accessor = accessor,
            initialValue = initialValue
        )
        entityProperties.add(property)
        return property
    }

    fun updateAspects() {
        aspects.set(appliedAspects)
    }

    fun isInBattle() = battleId.get().isPresent

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(5, WanderAroundGoal(this, 0.4, 30))
        goalSelector.add(6, LookAtEntityGoal(this, LivingEntity::class.java, 8F, 0.2F))
        goalSelector.add(6, LookAroundGoal(this))
    }
}