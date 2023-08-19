package com.cobblemon.mod.common.api.npc

import com.cobblemon.mod.common.api.npc.configuration.NPCBattleConfiguration

/**
 * An NPC configuration is like a snapshot of an NPC setup. It can be taken from an existing NPC and loaded onto
 * others. Useful as a backup mechanism or as shorthand for copying an NPC's configuration without building an
 * entire class for them.
 *
 * @author Hiroku
 * @since August 16th, 2023
 */
class NPCConfiguration {
    var battle: NPCBattleConfiguration? = null

}