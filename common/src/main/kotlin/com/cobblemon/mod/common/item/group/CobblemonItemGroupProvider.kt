/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.group

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier

internal class CobblemonItemGroupProvider(
    override val identifier: Identifier,
    override val displayName: Text,
    override val icon: () -> ItemStack
) : ItemGroupProvider {

    lateinit var group: ItemGroup

    override fun group(): ItemGroup = this.group

    override fun assign(group: ItemGroup) {
        this.group = group
    }

}