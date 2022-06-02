package com.cablemc.pokemoncobbled.common.client.gui.battle

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.api.text.text
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.battle.widgets.BattleOptionTile
import com.cablemc.pokemoncobbled.common.client.keybind.currentKey
import com.cablemc.pokemoncobbled.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemoncobbled.common.util.battleLang
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class BattleGUI : Screen(battleLang("gui.title")) {
    companion object {
        const val OPTION_VERTICAL_SPACING = 25
        const val OPTION_HORIZONTAL_SPACING = 7
        const val OPTION_ROOT_X = 40
        const val OPTION_VERTICAL_OFFSET = 100

        val fightResource = cobbledResource("ui/battle/battle_menu_fight.png")
        val runResource = cobbledResource("ui/battle/battle_menu_run.png")
    }

    var childrenHidden = true

    override fun init() {
        var rank = 0
        addOption(rank++, battleLang("ui.fight"), fightResource) {
            clearChildren()


            // Show move selection
        }

        addOption(rank++, battleLang("ui.switch"), runResource) {
            // Show a party list
        }
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        super.resize(client, width, height)
        childrenHidden = true
//        clearChildren()
//        init()
    }

    private fun addOption(rank: Int, text: MutableText, texture: Identifier, onClick: () -> Unit) {
        addDrawableChild(BattleOptionTile(
            battleGUI = this,
            x = OPTION_ROOT_X + rank * OPTION_HORIZONTAL_SPACING,
            y = MinecraftClient.getInstance().window.scaledHeight - OPTION_VERTICAL_OFFSET + rank * OPTION_VERTICAL_SPACING,
            resource = texture,
            text = text,
            onClick = onClick
        ))
    }

    fun showMoves() {
        val moveset = PokemonCobbledClient.battle!!.actionRequest!!.active!!.first()

//        addDrawableChild()
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(poseStack, mouseX, mouseY, delta)
        val battle = PokemonCobbledClient.battle
        if (battle == null) {
            close()
            return
        } else if (PokemonCobbledClient.battleOverlay.opacityRatio <= 0.1 && PokemonCobbledClient.battle?.minimised == true) {
            close()
            return
        }

        if (battle.mustChoose && childrenHidden) {
            // Show options
            children().filterIsInstance<BattleOptionTile>().forEach { it.visible = true }
            childrenHidden = false
        } else if (!battle.mustChoose && !childrenHidden) {
            children().filterIsInstance<BattleOptionTile>().forEach { it.visible = false }
            childrenHidden = true
        }

//        blitk(
//            matrixStack = poseStack,
//            texture = CobbledResources.RED,
//            x = 100,
//            y = 100,
//            height = 100,
//            width = 100
//        )
    }

    override fun shouldPause() = false
    override fun close() {
        super.close()
        PokemonCobbledClient.battle?.minimised = true
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (chr.toString() == PartySendBinding.currentKey().localizedText.asString() && PokemonCobbledClient.battleOverlay.opacity == BattleOverlay.MAX_OPACITY) {
            val battle = PokemonCobbledClient.battle ?: return false
            battle.minimised = !battle.minimised
            return true
        }
        return super.charTyped(chr, modifiers)
    }
}