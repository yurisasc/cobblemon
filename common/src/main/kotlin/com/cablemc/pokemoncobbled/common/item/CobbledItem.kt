/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.item

import net.minecraft.item.Item

/**
 * Base for custom items in Cobbled.
 *
 * Containing common shared code.
 */
open class CobbledItem(settings : Settings) : Item(settings)