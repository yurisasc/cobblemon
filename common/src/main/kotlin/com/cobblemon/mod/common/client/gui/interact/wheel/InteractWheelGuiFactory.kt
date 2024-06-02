/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.wheel

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.interaction.PokemonInteractionGUICreationEvent
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.net.messages.client.PlayerInteractOptionsPacket
import com.cobblemon.mod.common.net.messages.server.*
import com.cobblemon.mod.common.net.messages.server.battle.SpectateBattlePacket
import com.cobblemon.mod.common.net.messages.server.pokemon.interact.InteractPokemonPacket
import com.cobblemon.mod.common.net.messages.server.trade.AcceptTradeRequestPacket
import com.cobblemon.mod.common.net.messages.server.trade.OfferTradePacket
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import java.util.*
import org.joml.Vector3f

fun createPokemonInteractGui(pokemonID: UUID, canMountShoulder: Boolean): InteractWheelGUI {
    val mountShoulder = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_shoulder.png"),
        tooltipText = "cobblemon.ui.interact.mount.shoulder",
        onPress = {
            if (canMountShoulder) {
                InteractPokemonPacket(pokemonID, true).sendToServer()
                closeGUI()
            }
        }
    )
    val giveItem = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_held_item.png"),
        tooltipText = "cobblemon.ui.interact.give.item",
        onPress = {
            InteractPokemonPacket(pokemonID, false).sendToServer()
            closeGUI()
        }
    )
    val options: Multimap<Orientation, InteractWheelOption> = ArrayListMultimap.create()
    options.put(Orientation.TOP_RIGHT, giveItem)
    if (canMountShoulder) {
        options.put(Orientation.TOP_LEFT, mountShoulder)
    }
    CobblemonEvents.POKEMON_INTERACTION_GUI_CREATION.post(PokemonInteractionGUICreationEvent(pokemonID, canMountShoulder, options))
    return InteractWheelGUI(options, Text.translatable("cobblemon.ui.interact.pokemon"))
}

fun createPlayerInteractGui(optionsPacket: PlayerInteractOptionsPacket): InteractWheelGUI {
    val trade = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_trade.png"),
        colour = { if (CobblemonClient.requests.tradeOffers.any { it.traderId == optionsPacket.targetId }) Vector3f(0F, 0.6F, 0F) else null },
        tooltipText = "cobblemon.ui.interact.trade",
        onPress = {
            val tradeOffer = CobblemonClient.requests.tradeOffers.find { it.traderId == optionsPacket.targetId }
            if (tradeOffer == null) {
                CobblemonNetwork.sendToServer(OfferTradePacket(optionsPacket.targetId))
            } else {
                CobblemonClient.requests.tradeOffers -= tradeOffer
                CobblemonNetwork.sendToServer(AcceptTradeRequestPacket(tradeOffer.tradeOfferId))
            }
            closeGUI()
        }
    )
    val singleBattle = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"),
        colour = { null },
        tooltipText = "cobblemon.challenge.singlebattle",
        onPress = {
            // This can be improved in future with more detailed battle challenge data.
            BattleChallengePacket(optionsPacket.numericTargetId, optionsPacket.selectedPokemonId, "singles").sendToServer()
            closeGUI()
        }
    )
    val doubleBattle = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need double battle icon
            colour = { null },
            tooltipText = "cobblemon.challenge.doublebattle",
            onPress = {
                // This can be improved in future with more detailed battle challenge data.
                BattleChallengePacket(optionsPacket.numericTargetId, optionsPacket.selectedPokemonId, "doubles").sendToServer()
                closeGUI()
            }
    )
    val tripleBattle = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need triple battle icon
            colour = { null },
            tooltipText = "cobblemon.challenge.triplebattle",
            onPress = {
                // This can be improved in future with more detailed battle challenge data.
                BattleChallengePacket(optionsPacket.numericTargetId, optionsPacket.selectedPokemonId, "triples").sendToServer()
                closeGUI()
            }
    )
    val multiBattle = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need Multi battle icon
            colour = { if (CobblemonClient.requests.battleChallenges.any { it.challengerId == optionsPacket.targetId }) Vector3f(0F, 0.6F, 0F) else null },
            tooltipText = "cobblemon.challenge.multibattle",
            onPress = {
                // This can be improved in future with more detailed battle challenge data.
                BattleChallengePacket(optionsPacket.numericTargetId, optionsPacket.selectedPokemonId, "multi").sendToServer()
                closeGUI()
            }
    )
    // TODO: Need more details on the UI to discern between battles and team requests
    val battleAccept = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need Accept icon
        colour = { Vector3f(0F, 0.6F, 0F) },
        tooltipText = "cobblemon.ui.interact.accept",
        onPress = {
            BattleChallengeResponsePacket(optionsPacket.numericTargetId, optionsPacket.selectedPokemonId, true).sendToServer()
            closeGUI()
        }
    )
    val battleDecline = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need Decline icon
        colour = { Vector3f(0.6F, 0F, 0F) },
        tooltipText = "cobblemon.ui.interact.decline",
        onPress = {
            BattleChallengeResponsePacket(optionsPacket.numericTargetId, optionsPacket.selectedPokemonId, false).sendToServer()
            closeGUI()
        }
    )

    val teamAccept = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need Accept icon
            colour = { Vector3f(0F, 0.6F, 0F) },
            tooltipText = "cobblemon.ui.interact.accept",
            onPress = {
                BattleTeamResponsePacket(optionsPacket.numericTargetId,true).sendToServer()
                closeGUI()
            }
    )
    val teamDecline = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Need Decline icon
            colour = { Vector3f(0.6F, 0F, 0F) },
            tooltipText = "cobblemon.ui.interact.decline",
            onPress = {
                BattleTeamResponsePacket(optionsPacket.numericTargetId,false).sendToServer()
                closeGUI()
            }
    )

    val teamRequest = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Team Request Icon
            colour = { Vector3f(0.384F, 0.811F, 0.976F) },
            tooltipText = "cobblemon.ui.interact.team_request",
            onPress = {
                BattleTeamRequestPacket(optionsPacket.numericTargetId).sendToServer()
                closeGUI()
            }
    )

    val teamLeave = InteractWheelOption(
            iconResource = cobblemonResource("textures/gui/interact/icon_battle.png"), // Team Abandon Icon
            colour = { Vector3f(0.8F, 0F, 0.0F) },
            tooltipText = "cobblemon.ui.interact.team_leave",
            onPress = {
                BattleTeamLeavePacket().sendToServer()
                closeGUI()
            }
    )

    val spectate = InteractWheelOption(
        iconResource = cobblemonResource("textures/gui/interact/icon_spectate_battle.png"),
        colour = { if (CobblemonClient.requests.battleChallenges.any { it.challengerId == optionsPacket.targetId }) Vector3f(0F, 0.6F, 0F) else null },
        onPress = {
            SpectateBattlePacket(optionsPacket.targetId).sendToServer()
            closeGUI()
        },
        tooltipText = "cobblemon.ui.interact.spectate"
    )
    val options: Multimap<Orientation, InteractWheelOption> = ArrayListMultimap.create()
    val hasChallenge = CobblemonClient.requests.battleChallenges.any { it.challengerId == optionsPacket.targetId }
    val hasTeamRequest = CobblemonClient.requests.multiBattleTeamRequests.any { it.challengerId == optionsPacket.targetId }
    //The way things are positioned should probably be more thought out if more options are added
    optionsPacket.options.map {
        if (it.equals(PlayerInteractOptionsPacket.Options.TRADE)) {
            options.put(Orientation.TOP_LEFT, trade)
        }
        if (it.equals(PlayerInteractOptionsPacket.Options.BATTLE)) {
            if (hasChallenge) {
                options.put(Orientation.BOTTOM_RIGHT, battleAccept)
                options.put(Orientation.BOTTOM_LEFT, battleDecline)
            } else if (hasTeamRequest) {
                options.put(Orientation.BOTTOM_RIGHT, teamAccept)
                options.put(Orientation.BOTTOM_LEFT, teamDecline)
            } else {
                options.put(Orientation.TOP_RIGHT, singleBattle)
                options.put(Orientation.BOTTOM_RIGHT, doubleBattle)
                options.put(Orientation.BOTTOM_LEFT, tripleBattle)
            }
        }
        if (it.equals(PlayerInteractOptionsPacket.Options.TEAM_REQUEST)) {
            options.put(Orientation.TOP_LEFT, teamRequest)
        }
        if (it.equals(PlayerInteractOptionsPacket.Options.MULTI_BATTLE)) {
            options.put(Orientation.BOTTOM_LEFT, multiBattle)
        }
        if (it.equals(PlayerInteractOptionsPacket.Options.SPECTATE_BATTLE)) {
            if(!hasChallenge) {
                options.put(Orientation.TOP_RIGHT, spectate)
            }
        }
        if (it.equals(PlayerInteractOptionsPacket.Options.TEAM_LEAVE)) {
            options.put(Orientation.TOP_LEFT, teamLeave)
        }
    }
    return InteractWheelGUI(options, Text.translatable("cobblemon.ui.interact.player"))
}

private fun closeGUI() {
    MinecraftClient.getInstance().setScreen(null)
}