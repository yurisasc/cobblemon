/*
 * Copyright (C) 2024 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.battleRequest

import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.battles.BattleFormat
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.net.messages.client.PlayerInteractOptionsPacket
import com.cobblemon.mod.common.net.messages.server.*
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

class BattleConfigureGUI(private val packet: PlayerInteractOptionsPacket) : Screen(lang("challenge.request_battle_title")) {
    companion object {
        const val SIZE = 113
        private val backgroundResource = cobblemonResource("textures/gui/interact/request/battle_request.png")
        private val battleArrowsResource = cobblemonResource("textures/gui/interact/request/battle_request_arrows.png")

        val battleRequestMap = mutableMapOf(
                Pair(
                        PlayerInteractOptionsPacket.Options.SINGLE_BATTLE,
                        BattleTypeTile(
                                battleFormat = BattleFormat.GEN_9_SINGLES,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_single.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_overlay.png"),
                                title = lang("challenge.request_battle_title").bold(),
                                subTitle = lang("battle.types.single").bold(),
                                onRequest = { packet, battleFormat -> sendBattleRequest(battleFormat, packet) },
                                onResponse = { packet, accept -> sendBattleResponse(packet, accept) }
                        )
                ),
                Pair(
                        PlayerInteractOptionsPacket.Options.DOUBLE_BATTLE,
                        BattleTypeTile(
                                battleFormat = BattleFormat.GEN_9_DOUBLES,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_double.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_overlay.png"),
                                title = lang("challenge.request_battle_title").bold(),
                                subTitle = lang("battle.types.double").bold(),
                                onRequest = { packet, battleFormat -> sendBattleRequest(battleFormat, packet) },
                                onResponse = { packet, accept -> sendBattleResponse(packet, accept) }
                        ),
                ),
                Pair(
                        PlayerInteractOptionsPacket.Options.TRIPLE_BATTLE,
                        BattleTypeTile(
                                battleFormat = BattleFormat.GEN_9_TRIPLES,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_triple.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_overlay.png"),
                                title = lang("challenge.request_battle_title").bold(),
                                subTitle = lang("battle.types.triple").bold(),
                                onRequest = { packet, battleFormat -> sendBattleRequest(battleFormat, packet) },
                                onResponse = { packet, accept -> sendBattleResponse(packet, accept) }
                        ),
                ),
                Pair(
                        PlayerInteractOptionsPacket.Options.TEAM_REQUEST,
                        BattleTypeTile(
                                battleFormat = BattleFormat.GEN_9_MULTI,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_multi.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_multi_overlay_partner.png"),
                                title = lang("challenge.request_team_join_title").bold(),
                                subTitle = lang("battle.types.multi").bold(),
                                color = ColourLibrary.SIDE_1_ALLY_BATTLE_COLOUR,
                                onRequest = { packet, battleFormat -> BattleTeamRequestPacket(packet.numericTargetId).sendToServer()  },
                                onResponse = { packet, accept -> BattleTeamResponsePacket(packet.numericTargetId, accept).sendToServer() }
                        ),
                ),
                Pair(
                        PlayerInteractOptionsPacket.Options.TEAM_LEAVE,
                        BattleTypeTile(
                                battleFormat = BattleFormat.GEN_9_MULTI,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_multi.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_multi_overlay_partner.png"),
                                title = lang("challenge.request_team_leave_title").bold(),
                                subTitle = lang("battle.types.multi").bold(),
                                color = ColourLibrary.SIDE_1_ALLY_BATTLE_COLOUR,
                                onRequest = { _, _ -> BattleTeamLeavePacket().sendToServer() },
                                onResponse = { _, _ -> Unit }
                        ),
                ),
                Pair(
                        PlayerInteractOptionsPacket.Options.MULTI_BATTLE,
                        BattleTypeTile(
                                battleFormat = BattleFormat.GEN_9_MULTI,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_multi.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_multi_overlay_opponent.png"),
                                title = lang("challenge.request_battle_title").bold(),
                                subTitle = lang("battle.types.multi").bold(),
                                onRequest = { packet, battleFormat -> sendBattleRequest(battleFormat, packet) },
                                onResponse = { packet, accept -> sendBattleResponse(packet, accept) }
                        ),
                ),
                Pair(
                        PlayerInteractOptionsPacket.Options.ROYAL_BATTLE,
                        BattleTypeTile(
                                battleFormat =  BattleFormat.GEN_9_ROYAL,
                                tileTexture = cobblemonResource("textures/gui/interact/request/battle_request_royal.png"),
                                overlayTexture = cobblemonResource("textures/gui/interact/request/battle_request_royal_overlay.png"),
                                title = lang("challenge.request_battle_title").bold(),
                                subTitle = lang("battle.types.freeforall").bold(),
                                onRequest = { packet, battleFormat -> sendBattleRequest(battleFormat, packet) },
                                onResponse = { packet, accept -> sendBattleResponse(packet, accept) }
                        ),
                ),
        )

        fun sendBattleRequest(battleFormat: BattleFormat, packet: PlayerInteractOptionsPacket) {
            BattleChallengePacket(packet.numericTargetId, packet.selectedPokemonId, battleFormat).sendToServer()
        }
        private fun sendBattleResponse(packet: PlayerInteractOptionsPacket, accept: Boolean) {
            BattleChallengeResponsePacket(packet.numericTargetId, packet.selectedPokemonId, accept).sendToServer()
        }
        private var battleTypeTiles: List<BattleTypeTile> = emptyList()
        private val blinkRate = 35
    }

    class BattleTypeTile(
            val battleFormat: BattleFormat,
            val tileTexture: ResourceLocation?,
            val overlayTexture: ResourceLocation?,
            val title: MutableComponent,
            val subTitle: MutableComponent,
            val color: Int = ColourLibrary.SIDE_1_BATTLE_COLOUR,
            val onRequest: (packet : PlayerInteractOptionsPacket, battleFormat: BattleFormat) -> Unit,
            val onResponse: (packet: PlayerInteractOptionsPacket, accept: Boolean) -> Unit,
    )

    private var currentPage = 0
        set(value) {
            // If value is within min and max
            field = if (value > 0 && value < battleTypeTiles.count()) value
            // If value is less than zero, wrap around to end
            else if (value < 0) battleTypeTiles.count() - 1
            // Else it's greater than max, wrap around to start
            else 0
        }

    private var r:Float = 0F
    private var g:Float = 0F
    private var b:Float = 0F
    private var targetName = Component.literal("Target Name").bold()
    private var hasRequest = false
    private var ticksPassed = 0F

    override fun renderBlurredBackground(delta: Float) { }
    override fun renderMenuBackground(context: GuiGraphics) {}


    override fun init() {
        var color =  ColourLibrary.SIDE_1_BATTLE_COLOUR
        r = ((color shr 16) and 0b11111111) / 255F
        g = ((color shr 8) and 0b11111111) / 255F
        b = (color and 0b11111111) / 255F
        val challenge = CobblemonClient.requests.battleChallenges.firstOrNull { it.challengerIds.contains(packet.targetId) }
        targetName = Minecraft.getInstance().player?.level()?.getPlayerByUUID(packet.targetId)?.name?.plainCopy()?.bold() ?: targetName
        val hasTeamRequest = CobblemonClient.requests.multiBattleTeamRequests.any { it.challengerIds.contains(packet.targetId) }
        hasRequest = challenge != null || hasTeamRequest
        if (hasTeamRequest) {
            battleTypeTiles = listOf(battleRequestMap[PlayerInteractOptionsPacket.Options.TEAM_REQUEST]).mapNotNull { it }
        } else if (challenge != null) {
            battleTypeTiles = listOf(packet.options.mapNotNull { battleRequestMap[it] }.first { it.battleFormat.battleType.name == challenge.battleFormat?.battleType?.name })
        } else {
            battleTypeTiles = packet.options.mapNotNull { battleRequestMap[it] }
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        ticksPassed += delta
        val battleTypeData = battleTypeTiles[currentPage]
        // Render background panel
        val (x, y) = getDimensions()
        blitk(
            matrixStack = context.pose(),
            texture = backgroundResource,
            x = x,
            y = y,
            width = 113,
            height = 126
        )

        // Render Screen title
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = battleTypeData.title,
            x = x + 42,
            y = y + 2,
            centered = true,
            shadow = true
        )

        // Render battle type icon
        blitk(
            matrixStack = context.pose(),
            texture = battleTypeData.tileTexture,
            x = 2*(x + 113 / 2 - 190 / 4),
            y = 2*(y + 126 / 2 - 120 / 4 - 10),
            width = 190,
            height = 120,
            scale = 0.5F,
        )
        // Render battle type overlay
        if(ticksPassed % blinkRate < blinkRate / 2) {
            blitk(
                matrixStack = context.pose(),
                texture = battleTypeData.overlayTexture,
                x = 2*(x + 113 / 2 - 190 / 4),
                y = 2*(y + 126 / 2 - 120 / 4 - 10),
                width = 190,
                height = 120,
                scale = 0.5F,
            )
        }


        // Battle type display text
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = battleTypeData.subTitle,
            x = x + 55,
            y = y + 14,
            centered = true,
            shadow = true
        )

        if(!hasRequest) {
            // Selection buttons
            this.addRenderableWidget(
                    BattleRequestNavigationButton(
                            pX = x + 2,
                            pY = y + 50,
                            forward = false
                    ) { currentPage = (currentPage - 1) % battleTypeTiles.count() }
            )

            this.addRenderableWidget(
                    BattleRequestNavigationButton(
                            pX = x + 106,
                            pY = y + 50,
                            forward = true
                    ) { currentPage = (currentPage + 1) % battleTypeTiles.count() }
            )
        }

        // Opponent Display name
        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = targetName,
            x = x + 55,
            y = y + 87,
            centered = true,
            shadow = true
        )

        // Draw Arrows
        blitk(
            matrixStack = context.pose(),
            texture = battleArrowsResource,
            x = x + 1,
            y = y + 106,
            width = 111,
            height = 5,
            scale = 1F,
            red = ((battleTypeData.color shr 16) and 0b11111111) / 255F,
            green = ((battleTypeData.color shr 8) and 0b11111111) / 255F,
            blue = (battleTypeData.color and 0b11111111) / 255F,
        )

        if(hasRequest) {
            // Draw Accept/Decline buttons
            this.addRenderableWidget(
                    BattleResponseButton(
                            x + 22,
                            y + 99,
                            true
                    ) {
                        battleTypeTiles[currentPage].onResponse(packet, true)
                        closeGUI()
                    }
            )

            this.addRenderableWidget(
                    BattleResponseButton(
                            x + 56,
                            y + 99,
                            false
                    ) {
                        battleTypeTiles[currentPage].onResponse(packet, false)
                        closeGUI()
                    }
            )

        } else {
            // Draw Challenge button
            this.addRenderableWidget(
                BattleRequestButton(
                        x + 22,
                        y + 99,
                        lang("challenge.challenge"),
                ) {
                    //TODO: add additional battle rules, otherwise this call feels pretty silly
                    battleTypeTiles[currentPage].onRequest(packet, battleTypeTiles[currentPage].battleFormat)
                    closeGUI()
                }
            )
        }

        super.render(context, mouseX, mouseY, delta)
    }

    private fun getDimensions(): Pair<Int, Int> {
        return Pair(
            (width - SIZE) / 2,
            (height - SIZE) / 2
        )
    }


    override fun isPauseScreen() = false

//    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
//        return super.mouseClicked(mouseX, mouseY, button)
//    }
    private fun closeGUI() {
        Minecraft.getInstance().setScreen(null)
    }

}