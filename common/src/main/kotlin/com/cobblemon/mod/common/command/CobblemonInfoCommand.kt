/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.command

import com.cobblemon.mod.common.CobblemonBuildDetails
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.network.chat.Component
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting

object CobblemonInfoCommand {

    private val RED: TextColor = TextColor.fromRgb(0xC74242)
    private val YELLOW: TextColor = TextColor.fromRgb(0xDEDE00)
    private val GREEN: TextColor = TextColor.fromRgb(0x42C742)

    private val INDENT: Component = Component.literal("  ")
    private val NEW_LINE: Component = Component.literal("\n")
    private val SPACE: Component = Component.literal(" ")

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("cobblemon")
            .then(LiteralArgumentBuilder.literal<ServerCommandSource?>("info")
                .executes { ctx ->
                    val message = Component.empty().append(
                        Component.literal("Cobblemon Build Details").styled { it.withColor(this.YELLOW) })
                    message.append(this.NEW_LINE)
                        .append(this.INDENT)
                        .append(Component.literal("Version:").styled { it.withColor(Formatting.GRAY) })
                        .append(this.SPACE)
                        .append(CobblemonBuildDetails.VERSION)

                    message.append(this.NEW_LINE)
                        .append(this.INDENT)
                        .append(Component.literal("Is Snapshot:").styled { it.withColor(Formatting.GRAY) })
                        .append(this.SPACE)
                        .append(Component.literal(if(CobblemonBuildDetails.SNAPSHOT) "Yes" else "No").styled {
                            it.withColor(if(CobblemonBuildDetails.SNAPSHOT) this.GREEN else this.RED)
                        })

                    message.append(this.NEW_LINE)
                        .append(this.INDENT)
                        .append(Component.literal("Git Commit:").styled { it.withColor(Formatting.GRAY) })
                        .append(this.SPACE)
                        .append(Component.literal(CobblemonBuildDetails.smallCommitHash()).styled {
                            val link = "https://gitlab.com/cable-mc/cobblemon/-/commit/${CobblemonBuildDetails.GIT_COMMIT}"

                            it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(link)))
                                .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, link))
                        })

                    message.append(this.NEW_LINE)
                        .append(this.INDENT)
                        .append(Component.literal("Branch:").styled { it.withColor(Formatting.GRAY) })
                        .append(this.SPACE)
                        .append(CobblemonBuildDetails.BRANCH)

                    ctx.source.sendMessage(message)
                    0
                }
            )
        )
    }
}