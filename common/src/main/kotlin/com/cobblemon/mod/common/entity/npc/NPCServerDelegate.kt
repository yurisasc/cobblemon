/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.entity.npc

import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.cobblemon.mod.common.api.dialogue.ActiveDialogue
import com.cobblemon.mod.common.api.dialogue.DialogueManager
import com.cobblemon.mod.common.api.dialogue.Dialogues
import com.cobblemon.mod.common.api.entity.NPCSideDelegate
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.ObjectValue
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.battles.BattleBuilder
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.server.network.ServerPlayerEntity

class NPCServerDelegate : NPCSideDelegate {
    lateinit var entity: NPCEntity

    override fun initialize(entity: NPCEntity) {
        super.initialize(entity)
        this.entity = entity
        this.entity.customName = entity.npc.names.randomOrNull() ?: "NPC".text()
    }

    override fun addToStruct(struct: QueryStruct) {
        super.addToStruct(struct)
        struct
            .addFunction("data") { entity.data }
            .addFunction("save_data") { entity.data } // Handled as part of NBT saving
            .addFunction("battle") { params ->
                val opponentValue = params.get<MoValue>(0)
                val opponent = if (opponentValue is ObjectValue<*>) {
                    opponentValue.obj as ServerPlayerEntity
                } else {
                    opponentValue.asString().let { entity.server!!.playerManager.getPlayer(it)!! }
                }
                val battleStartResult = BattleBuilder.pvn(
                    player = opponent,
                    npcEntity = entity
                )

                var returnValue: MoValue = DoubleValue(false)
                battleStartResult.ifSuccessful { returnValue = DoubleValue(true) }
                return@addFunction returnValue
            }
            .addFunction("run_dialogue") { params ->
                val player = params.get<ObjectValue<ServerPlayerEntity>>(0).obj
                val dialogue = Dialogues.dialogues[params.getString(1).asIdentifierDefaultingNamespace()]!!
                DialogueManager.startDialogue(
                    ActiveDialogue(player, dialogue).also {
                        it.runtime.environment.getQueryStruct().addFunction("npc") { struct }
                    }
                )
            }
            .addFunction("was_hurt_by") { params ->
                val entity = params.get<ObjectValue<LivingEntity>>(0).obj
                val hurtByEntity = this.entity.brain.getOptionalRegisteredMemory(MemoryModuleType.HURT_BY_ENTITY).orElse(null)
                return@addFunction DoubleValue(hurtByEntity == entity)
            }
    }
}