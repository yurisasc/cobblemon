/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.item.CobbledItem
import com.cablemc.pokemoncobbled.common.item.CobbledItemGroups

open class EvolutionItem(properties: Settings = Settings().group(CobbledItemGroups.EVOLUTION_ITEM_GROUP)) : CobbledItem(properties)