package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.pokemon.FormData
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.entity.EntityDimensions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object ChangeScaleAndSize {
    fun register(dispatcher : CommandDispatcher<ServerCommandSource>) {
        val command = CommandManager.literal("changescaleandsize")
            .requires { it.hasPermissionLevel(4) }
            .then(
                CommandManager.argument("pokemon", PokemonArgumentType.pokemon())
                    .then(
                        CommandManager.argument("scale", FloatArgumentType.floatArg())
                            .then(CommandManager.argument("width", FloatArgumentType.floatArg())
                                .then(CommandManager.argument("height", FloatArgumentType.floatArg()).executes { execute(it) })
                            )
                    )

                    .executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<ServerCommandSource>) : Int {
        val pkm = PokemonArgumentType.getPokemon(context, "pokemon")
        val scale = FloatArgumentType.getFloat(context, "scale")
        val width = FloatArgumentType.getFloat(context, "width")
        val height = FloatArgumentType.getFloat(context, "height")

        pkm.baseScale = scale
        pkm.hitbox = EntityDimensions(width, height, false)
        pkm.forms.clear()
        pkm.forms.add(FormData().also { it.species = pkm })
        return Command.SINGLE_SUCCESS
    }
}