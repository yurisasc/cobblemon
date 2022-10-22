/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.item

import com.cablemc.pokemod.common.PokemodItems
import com.cablemc.pokemod.common.util.pokemodResource
import dev.architectury.registry.CreativeTabRegistry.create
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object PokemodItemGroups {
    val POKE_BALL_GROUP: ItemGroup = create(pokemodResource("pokeball")) { ItemStack(PokemodItems.POKE_BALL.get()) }
    val EVOLUTION_ITEM_GROUP: ItemGroup = create(pokemodResource("evolution_item")) { ItemStack(PokemodItems.BLACK_AUGURITE.get()) }
    val MEDICINE_ITEM_GROUP: ItemGroup = create(pokemodResource("medicine")) { ItemStack(PokemodItems.RARE_CANDY.get()) }
}