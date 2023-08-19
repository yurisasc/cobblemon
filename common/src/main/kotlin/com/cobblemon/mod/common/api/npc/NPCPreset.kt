package com.cobblemon.mod.common.api.npc

import net.minecraft.util.Identifier

class NPCPreset {
    lateinit var id: Identifier
    var aspects: Set<String>? = null
    var party: NPCPartyProvider? = null
//    var mergeMode = MergeMode.KEEP
}