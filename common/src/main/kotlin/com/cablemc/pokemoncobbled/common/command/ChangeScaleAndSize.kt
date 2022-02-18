package com.cablemc.pokemoncobbled.common.command

import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.entity.pokemon.FormData
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.world.entity.EntityDimensions

object ChangeScaleAndSize {
    fun register(dispatcher : CommandDispatcher<CommandSourceStack>) {
        val command = Commands.literal("changescaleandsize")
            .requires { it.hasPermission(4) }
            .then(
                Commands.argument("pokemon", PokemonArgumentType.pokemon())
                    .then(
                        Commands.argument("scale", FloatArgumentType.floatArg())
                            .then(Commands.argument("width", FloatArgumentType.floatArg())
                                .then(Commands.argument("height", FloatArgumentType.floatArg()).executes { execute(it) })
                            )
                    )

                    .executes { execute(it) })
        dispatcher.register(command)
    }

    private fun execute(context: CommandContext<CommandSourceStack>) : Int {
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