/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.registry.CreativeTabRegistry.create
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

object CobbledItemGroups {
    val POKE_BALL_GROUP: ItemGroup = create(cobbledResource("pokeball")) { ItemStack(CobbledItems.POKE_BALL.get()) }
    val EVOLUTION_ITEM_GROUP: ItemGroup = create(cobbledResource("evolution_item")) { ItemStack(CobbledItems.BLACK_AUGURITE.get()) }
    val MEDICINE_ITEM_GROUP: ItemGroup = create(cobbledResource("medicine")) { ItemStack(CobbledItems.RARE_CANDY.get()) }
}