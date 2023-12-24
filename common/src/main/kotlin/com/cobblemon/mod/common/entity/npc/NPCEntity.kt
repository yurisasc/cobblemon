/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc

import com.bedrockk.molang.runtime.struct.VariableStruct
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.api.entity.PokemonSender
import com.cobblemon.mod.common.api.net.serializers.IdentifierDataSerializer
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.net.serializers.StringSetDataSerializer
import com.cobblemon.mod.common.api.net.serializers.UUIDSetDataSerializer
import com.cobblemon.mod.common.api.npc.NPCClasses
import com.cobblemon.mod.common.api.npc.configuration.NPCBattleConfiguration
import com.cobblemon.mod.common.api.npc.configuration.NPCBehaviourConfiguration
import com.cobblemon.mod.common.api.npc.configuration.NPCInteractConfiguration
import com.cobblemon.mod.common.api.scheduling.Schedulable
import com.cobblemon.mod.common.api.scheduling.SchedulingTracker
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.npc.ai.StayPutInBattleGoal
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.DataKeys
import java.util.UUID
import java.util.concurrent.CompletableFuture
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Npc
import net.minecraft.entity.ai.goal.LookAroundGoal
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

class NPCEntity(world: World) : PassiveEntity(CobblemonEntities.NPC, world), Npc, Poseable, PokemonSender, Schedulable {
    override val schedulingTracker = SchedulingTracker()
    var npc = NPCClasses.random()
        set(value) {
            dataTracker.set(NPC_CLASS, value.resourceIdentifier)
            field = value
        }

    val appliedAspects = mutableSetOf<String>()
    val delegate = if (world.isClient) {
        com.cobblemon.mod.common.client.entity.NPCClientDelegate()
    } else {
        NPCServerDelegate()
    }

    var battle: NPCBattleConfiguration? = null
    var interact: NPCInteractConfiguration? = null
    var behaviour: NPCBehaviourConfiguration? = null

    var variables = VariableStruct()


    val aspects: Set<String>
        get() = dataTracker.get(ASPECTS)

    val battleIds: Set<UUID>
        get() = dataTracker.get(BATTLE_IDS)


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

    init {
        delegate.initialize(this)
    }

    // This has to be below constructor and entity tracker fields otherwise initialization order is weird and breaks them syncing
    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder = createMobAttributes()

        val NPC_CLASS = DataTracker.registerData(NPCEntity::class.java, IdentifierDataSerializer)
        val ASPECTS = DataTracker.registerData(NPCEntity::class.java, StringSetDataSerializer)
        val POSE_TYPE = DataTracker.registerData(NPCEntity::class.java, PoseTypeDataSerializer)
        val BATTLE_IDS = DataTracker.registerData(NPCEntity::class.java, UUIDSetDataSerializer)

        const val SEND_OUT_ANIMATION = "send-out"
        const val RECALL_ANIMATION = "recall"
        const val LOSE_ANIMATION = "lose"
        const val WIN_ANIMATION = "win"
    }

    override fun createChild(world: ServerWorld, entity: PassiveEntity) = null // No lovemaking! Unless...
    override fun getPoseType(): PoseType = this.getDataTracker().get(POSE_TYPE)

    override fun initDataTracker() {
        super.initDataTracker()
        dataTracker.startTracking(NPC_CLASS, Identifier("a"))
        dataTracker.startTracking(ASPECTS, emptySet())
        dataTracker.startTracking(POSE_TYPE, PoseType.STAND)
        dataTracker.startTracking(BATTLE_IDS, setOf())
    }

    fun updateAspects() {
        dataTracker.set(ASPECTS, appliedAspects)
    }

    fun isInBattle() = battleIds.isNotEmpty()
    fun getBattleConfiguration() = battle ?: npc.battleConfiguration

    override fun tick() {
        super.tick()
        delegate.tick(this)
        schedulingTracker.update(1/20F)
    }

    override fun initGoals() {
        super.initGoals()
        goalSelector.add(2, StayPutInBattleGoal(this))
        goalSelector.add(5, WanderAroundGoal(this, 0.4, 30))
        goalSelector.add(6, LookAtEntityGoal(this, LivingEntity::class.java, 8F, 0.2F))
        goalSelector.add(6, LookAroundGoal(this))
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)
        nbt.putString(DataKeys.NPC_CLASS, npc.resourceIdentifier.toString())
        nbt.put(DataKeys.NPC_ASPECTS, NbtList().also { list -> appliedAspects.forEach { list.add(NbtString.of(it)) } })
        val battle = battle
        if (battle != null) {
            val battleNBT = NbtCompound()
            battle.saveToNBT(battleNBT)
            nbt.put(DataKeys.NPC_BATTLE_CONFIGURATION, battleNBT)
        }
        return nbt
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        npc = NPCClasses.getByIdentifier(Identifier(nbt.getString(DataKeys.NPC_CLASS))) ?: NPCClasses.classes.first()
        appliedAspects.addAll(nbt.getList(DataKeys.NPC_ASPECTS, NbtList.STRING_TYPE.toInt()).map { it.asString() })
        val battleNBT = nbt.getCompound(DataKeys.NPC_BATTLE_CONFIGURATION)
        if (!battleNBT.isEmpty) {
            battle = NPCBattleConfiguration().also { it.loadFromNBT(battleNBT) }
        }
        updateAspects()
    }

    override fun interactMob(player: PlayerEntity, hand: Hand): ActionResult {
        if (player is ServerPlayerEntity) {
            val battle = getBattleConfiguration()
            if (battle.canChallenge) {
                val provider = battle.party
                if (provider != null) {
                    val party = provider.provide(this, listOf(player))
                    val result = BattleBuilder.pvn(
                        player = player,
                        npcEntity = this
                    )
                }
            }
        }
        return super.interactMob(player, hand)
    }

    fun playAnimation(animationType: String) {
        delegate.playAnimation(animationType)
    }

    override fun recalling(pokemonEntity: PokemonEntity): CompletableFuture<Unit> {
        playAnimation(RECALL_ANIMATION)
        return delayedFuture(seconds = 1.6F)
    }

    override fun sendingOut(): CompletableFuture<Unit> {
        playAnimation(SEND_OUT_ANIMATION)
        return delayedFuture(seconds = 1.6F)
    }
}