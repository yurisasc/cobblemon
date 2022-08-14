package com.cablemc.pokemoncobbled.common.client.gui.battle

import com.cablemc.pokemoncobbled.common.battles.MoveActionResponse
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionResponse
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattleActor
import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen.BattleActionSelection
import com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen.BattleGeneralActionSelection
import com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen.BattleMoveSelection
import com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen.BattleSwitchPokemonSelection
import com.cablemc.pokemoncobbled.common.client.gui.battle.widgets.BattleMessagePane
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.util.battleLang
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class BattleGUI : Screen(battleLang("gui.title")) {
    companion object {
        const val OPTION_VERTICAL_SPACING = 25
        const val OPTION_HORIZONTAL_SPACING = 7
        const val OPTION_ROOT_X = 40
        const val OPTION_VERTICAL_OFFSET = 125

        val fightResource = cobbledResource("ui/battle/battle_menu_fight.png")
        val runResource = cobbledResource("ui/battle/battle_menu_run.png")
    }

    var opacity = 0F
    val actor = PokemonCobbledClient.battle?.side1?.actors?.find { it.uuid == MinecraftClient.getInstance().player?.uuid }

    var queuedActions = mutableListOf<() -> Unit>()

    override fun init() {
        super.init()
        addDrawableChild(BattleMessagePane(this, PokemonCobbledClient.battle!!.messages))
    }

    fun changeActionSelection(newSelection: BattleActionSelection?) {
        val current = children().find { it is BattleActionSelection }
        queuedActions.add {
            current?.let(this::remove)
            if (newSelection != null) {
                addDrawableChild(newSelection)
            }
        }
    }

    fun getCurrentActionSelection() = children().filterIsInstance<BattleActionSelection>().firstOrNull()

    fun selectAction(request: SingleActionRequest, response: ShowdownActionResponse) {
        val battle = PokemonCobbledClient.battle ?: return
        if (request.response == null) {
            request.response = response
            changeActionSelection(null)
            battle.checkForFinishedChoosing()
        }
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        opacity = PokemonCobbledClient.battleOverlay.opacityRatio.toFloat()

        queuedActions.forEach { it() }
        queuedActions.clear()
        super.render(poseStack, mouseX, mouseY, delta)
        val battle = PokemonCobbledClient.battle
        if (battle == null) {
            close()
            return
        } else if (PokemonCobbledClient.battleOverlay.opacityRatio <= 0.1 && PokemonCobbledClient.battle?.minimised == true) {
            close()
            return
        }

        if (actor != null) {
            if (battle.mustChoose) {
                if (getCurrentActionSelection() == null) {
                    val unanswered = battle.getFirstUnansweredRequest()
                    if (unanswered != null) {
                        changeActionSelection(deriveRootActionSelection(actor, unanswered))
                    }
                }
            } else if (getCurrentActionSelection() != null) {
                changeActionSelection(null)
            }
        }

        drawScaledText(
            matrixStack = poseStack,
            text = battleLang("ui.hide_label", PartySendBinding.currentKey().localizedText),
            scale = 0.8F,
            x = 40,
            y = MinecraftClient.getInstance().window.scaledHeight - 40,
            opacity = 0.75F * opacity
        )

        queuedActions.forEach { it() }
        queuedActions.clear()
    }

    fun deriveRootActionSelection(actor: ClientBattleActor, request: SingleActionRequest): BattleActionSelection {
        return if (request.forceSwitch) {
            BattleSwitchPokemonSelection(this, request)
        } else {
            BattleGeneralActionSelection(this, request)
        }
    }

    override fun shouldPause() = false
    override fun close() {
        super.close()
        PokemonCobbledClient.battle?.minimised = true
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (chr.toString() == PartySendBinding.currentKey().localizedText.string && PokemonCobbledClient.battleOverlay.opacity == BattleOverlay.MAX_OPACITY) {
            val battle = PokemonCobbledClient.battle ?: return false
            battle.minimised = !battle.minimised
            return true
        }
        return super.charTyped(chr, modifiers)
    }
}