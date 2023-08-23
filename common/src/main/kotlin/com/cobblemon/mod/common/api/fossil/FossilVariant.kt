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
