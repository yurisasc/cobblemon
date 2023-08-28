/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.fossil

import net.minecraft.util.StringIdentifiable

enum class FossilVariant(name: String) : StringIdentifiable {
    DOME("dome"),
    SKULL("skull"),
    SHIELD("shield"),
    PLUME("plume"),
    SAIL("sail"),
    OLD_AMBER("old_amber"),
    ROOT("root"),
    JAW("jaw"),
    HELIX("helix"),
    CLAW("claw"),
    COVER("cover");

    override fun asString(): String {
        return name
    }
}
