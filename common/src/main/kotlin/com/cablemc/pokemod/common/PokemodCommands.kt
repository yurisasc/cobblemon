/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.command.ChangeScaleAndSize
import com.cablemc.pokemod.common.command.CheckSpawnsCommand
import com.cablemc.pokemod.common.command.ClickTextCommand
import com.cablemc.pokemod.common.command.GetNBT
import com.cablemc.pokemod.common.command.GivePokemon
import com.cablemc.pokemod.common.command.HealPokemonCommand
import com.cablemc.pokemod.common.command.LevelUp
import com.cablemc.pokemod.common.command.OpenStarterScreenCommand
import com.cablemc.pokemod.common.command.PokemonEditCommand
import com.cablemc.pokemod.common.command.SpawnAllPokemon
import com.cablemc.pokemod.common.command.SpawnPokemon
import com.cablemc.pokemod.common.command.StopBattleCommand
import com.cablemc.pokemod.common.command.TakePokemon
import com.cablemc.pokemod.common.command.TeachCommand
import com.cablemc.pokemod.common.command.TestCommand
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object PokemodCommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registry: CommandRegistryAccess, selection: CommandManager.RegistrationEnvironment) {
        SpawnPokemon.register(dispatcher)
        GivePokemon.register(dispatcher)
        TakePokemon.register(dispatcher)
        ChangeScaleAndSize.register(dispatcher)
        TestCommand.register(dispatcher)
        ClickTextCommand.register(dispatcher)
        PokemonEditCommand.register(dispatcher)
        TeachCommand.register(dispatcher)
        LevelUp.register(dispatcher)
        HealPokemonCommand.register(dispatcher)
        StopBattleCommand.register(dispatcher)
        CheckSpawnsCommand.register(dispatcher)
        GetNBT.register(dispatcher)
        OpenStarterScreenCommand.register(dispatcher)
        SpawnAllPokemon.register(dispatcher)
    }
}