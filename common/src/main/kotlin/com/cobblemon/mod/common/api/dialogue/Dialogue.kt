/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.dialogue

import com.cobblemon.mod.common.api.molang.ExpressionLike

/**
 * A dialogue that could be sent to players.
 *
 * @author Hiroku
 * @since December 27th, 2023
 */
class Dialogue(
    val pages: List<DialoguePage> = mutableListOf(),
    val escapeAction: DialogueAction = FunctionDialogueAction { dialogue, _ -> dialogue.close() }
) {
    companion object {
        fun of(
            pages: Iterable<DialoguePage>,
            escapeAction: ExpressionLike
        ): Dialogue {
            return Dialogue(
                pages = pages.toList(),
                escapeAction = ExpressionLikeDialogueAction(escapeAction)
            )
        }

        fun of(
            pages: Iterable<DialoguePage>,
            escapeAction: (ActiveDialogue) -> Unit
        ): Dialogue {
            val dialogue = Dialogue(
                pages = pages.toList(),
                escapeAction = FunctionDialogueAction { activeDialogue, _ -> escapeAction(activeDialogue) }
            )
            return dialogue
        }
    }
}