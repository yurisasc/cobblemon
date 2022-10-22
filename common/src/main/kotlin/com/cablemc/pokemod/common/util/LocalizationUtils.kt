/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.util

fun lang(
    subKey: String,
    vararg objects: Any
) = "pokemod.$subKey".asTranslated(*objects)

fun commandLang(subKey: String, vararg objects: Any ) = lang("command.$subKey", *objects)
fun battleLang(key: String, vararg objects: Any) = lang("battle.$key", *objects)