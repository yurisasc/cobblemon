/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc

import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.net.serializers.StringSetDataSerializer
import com.cobblemon.mod.common.entity.EntityProperty
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.entity.Npc
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
        val ASPECTS = DataTracker.registerData(NPCEntity::class.java, StringSetDataSerializer)
        val POSE_TYPE = DataTracker.registerData(NPCEntity::class.java, PoseTypeDataSerializer)
    }

    /**
     * shape should change from id to something akin to species
     * registry entry people can make custom ones for
     * this mainly just controls hitbox, ends up being used as the primary
     * resource identifier when loading the model (looks for resolver named ${shape}
     */
    var shape = cobblemonResource("regular")
    val appliedAspects = mutableSetOf<String>()
    val entityProperties = mutableListOf<EntityProperty<*>>()
    val delegate = if (world.isClient) {
        com.cobblemon.mod.common.client.entity.NPCClientDelegate()
    } else {
        NPCServerDelegate()
    }


    val aspects = addEntityProperty(ASPECTS, emptySet())
    val poseType = addEntityProperty(POSE_TYPE, PoseType.STAND)

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
     */

    constructor(world: World): super(CobblemonEntities.NPC, world) {}

    override fun createChild(world: ServerWorld, entity: PassiveEntity) = null // No lovemaking.
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
}