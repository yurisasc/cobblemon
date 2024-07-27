/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.battles.*
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.math.toRGB
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

/**
 * Button for toggling gimmicks during a battle.
 *
 * @param gimmick The [ShowdownMoveset.Gimmick] tied to this button.
 * @property tiles The [GimmickTile]s to render when this button is [toggled].
 *
 * @author Segfault Guy
 * @since July 8th, 2023
 */
abstract class BattleGimmickButton(gimmick: ShowdownMoveset.Gimmick, x: Int, y: Int, private val onToggle: BattleGimmickButton.(Boolean) -> Unit) : PressableWidget(x, y, (WIDTH * SCALE).toInt(), (HEIGHT * SCALE).toInt(), Text.empty()) {
    companion object {
        const val WIDTH = 36
        const val HEIGHT = 34
        const val SCALE = 0.5F
        const val XOFF = WIDTH * SCALE
        const val YOFF = HEIGHT * SCALE
        const val SPACING = 26

        /** Factory for creating an instance of [BattleGimmickButton] based on [ShowdownMoveset.Gimmick]. */
        fun create(gimmick: ShowdownMoveset.Gimmick, moveSelection: BattleMoveSelection, x: Int, y: Int, onToggle: BattleGimmickButton.(Boolean) -> Unit): BattleGimmickButton {
            return when(gimmick) {
                ShowdownMoveset.Gimmick.Z_POWER, ShowdownMoveset.Gimmick.ULTRA_BURST ->
                    ZPowerButton(moveSelection, x, y, onToggle)
                ShowdownMoveset.Gimmick.DYNAMAX ->
                    DynamaxButton(moveSelection, x, y, onToggle)
                else ->
                    object: BattleGimmickButton(gimmick, x, y, onToggle) {
                        override var tiles: List<BattleMoveSelection.MoveTile> = moveSelection.baseTiles.map { tile ->
                            GimmickTile(gimmick, moveSelection, tile.move, tile.x, tile.y)
                        }
                    }
            }
        }
    }

    abstract val tiles: List<BattleMoveSelection.MoveTile>
    var toggled = false
    private val sfx = PositionedSoundInstance.master(SoundEvents.BLOCK_ANVIL_LAND, 1.0F)
    private val texture = gimmick.id

    fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        blitk(
            matrixStack = matrices,
            texture = cobblemonResource("textures/gui/battle/battle_gimmick_${texture}.png"),
            x = x * 2,
            y = y * 2,
            height = HEIGHT,
            width = WIDTH,
            vOffset = if (toggled || isHovered || isFocused) HEIGHT else 0,
            textureHeight = HEIGHT * 2,
            scale = SCALE
        )
    }

    override fun onPress() {
        toggled = !toggled
        MinecraftClient.getInstance().soundManager.play(sfx)
        onToggle(toggled)
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
    }

    /**
     * Tile for an [InBattleMove] when a [BattleGimmickButton] is toggled. Triggers a gimmick when executed.
     *
     * @param moveSelection The [BattleMoveSelection] subscreen this tile is rendered on.
     * @param move The [InBattleMove] this tile is rendered for. May or may not have an associated [InBattleGimmickMove].
     * @property gimmick The [ShowdownMoveset.Gimmick] that is triggered.
     *
     * @author Segfault Guy
     * @since July 15th, 2023
     */
    open class GimmickTile(
        private val gimmick: ShowdownMoveset.Gimmick,
        moveSelection: BattleMoveSelection,
        move: InBattleMove,
        x: Int,
        y: Int
    ) : BattleMoveSelection.MoveTile(moveSelection, move, x, y) {

        // if there isn't a compatible gimmick for this move, the rendered moveTemplate will default to the base template
        init {
            gimmickMoveTemplate?.let {
                moveTemplate = it
                rgb = it.elementalType.hue.toRGB()
            }
        }

        protected val gimmickMove = move.gimmickMove

        // showdown already translates the base move id to the gimmick variant
        override val response: MoveActionResponse
            get() = MoveActionResponse(move.id, targetPnx, gimmick.id)
        override val targetList: List<Targetable>?
            get() = if (gimmickMove != null) gimmickMove.target.targetList(moveSelection.request.activePokemon) else super.targetList
        override val selectable: Boolean
            get() = if (gimmickMove != null) !gimmickMove.disabled else super.selectable

        /**
         * Couple reasons for making a unique template for gimmick moves:
         * 1. Z versions of status moves aren't registered as distinct moves, so can't get a template by name or num
         * 2. Damaging moves are registered as physical and it's confusing since the actual damageCategory is inherited from the base move
         */
        private val gimmickMoveTemplate: MoveTemplate? get() {
            val gimmickMoveID = move.gimmickMove?.move?.lowercase()?.replace(ShowdownIdentifiable.REGEX, "") ?: return null
            val gimmickTemplate = Moves.getByName(gimmickMoveID)
            return MoveTemplate(
                name = gimmickMoveID,
                num = gimmickTemplate?.num ?: -1,
                elementalType = gimmickTemplate?.elementalType ?: moveTemplate.elementalType,
                damageCategory = moveTemplate.damageCategory,
                power = gimmickTemplate?.power ?: moveTemplate.power,
                target = gimmickTemplate?.target ?: moveTemplate.target,
                accuracy = gimmickTemplate?.accuracy ?: moveTemplate.accuracy,
                pp = gimmickTemplate?.pp ?: moveTemplate.pp,
                priority = gimmickTemplate?.priority ?: moveTemplate.priority,
                critRatio = gimmickTemplate?.critRatio ?: moveTemplate.critRatio,
                effectChances = gimmickTemplate?.effectChances ?: moveTemplate.effectChances,
                actionEffect = null
            )
        }
    }
}

/**
 * Button for toggling Z-Power variations of compatible moves.
 *
 * @property tiles The Z-Moves for this moveset.
 *
 * @author Segfault Guy
 * @since July 15th, 2023
 */
class ZPowerButton(moveSelection: BattleMoveSelection, x: Int, y: Int, onToggle: BattleGimmickButton.(Boolean) -> Unit) :
    BattleGimmickButton(ShowdownMoveset.Gimmick.Z_POWER, x, y, onToggle) {

    override var tiles: List<BattleMoveSelection.MoveTile> = moveSelection.baseTiles.map { tile ->
        ZPowerTile(moveSelection, tile.move, tile.x, tile.y)
    }

    /**
     * Tile for Z-Power variation of a move.
     *
     * @param move The base move.
     * @property gimmickMove The respective Z-Move (if it exists).
     *
     * @author Segfault Guy
     * @since July 16th, 2023
     */
    class ZPowerTile(
        moveSelection: BattleMoveSelection,
        move: InBattleMove,
        x: Int,
        y: Int
    ) : GimmickTile(ShowdownMoveset.Gimmick.Z_POWER, moveSelection, move, x, y) {
        override val selectable: Boolean
            get() = gimmickMove != null && !gimmickMove.disabled
    }
}

/**
 * Button for toggling Max variations of moves.
 *
 * @property tiles The Max Moves for this moveset.
 *
 * @author Segfault Guy
 * @since July 27th, 2023
 */
class DynamaxButton(moveSelection: BattleMoveSelection, x: Int, y: Int, onToggle: BattleGimmickButton.(Boolean) -> Unit) :
    BattleGimmickButton(ShowdownMoveset.Gimmick.DYNAMAX, x, y, onToggle) {

    override var tiles: List<BattleMoveSelection.MoveTile> = moveSelection.baseTiles.map { tile ->
        DynamaxTile(moveSelection, tile.move, tile.x, tile.y)
    }

    /**
     * Tile for Max variation of a move.
     *
     * @param move The base move.
     * @property gimmickMove The respective Max Move.
     *
     * @author Segfault Guy
     * @since July 27th, 2023
     */
    class DynamaxTile(
        moveSelection: BattleMoveSelection,
        move: InBattleMove,
        x: Int,
        y: Int
    ) : GimmickTile(ShowdownMoveset.Gimmick.DYNAMAX, moveSelection, move, x, y) {
        override val selectable: Boolean
            get() = gimmickMove != null && !gimmickMove.disabled
    }
}