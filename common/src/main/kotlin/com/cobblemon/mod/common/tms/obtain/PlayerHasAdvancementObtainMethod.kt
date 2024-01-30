package com.cobblemon.mod.common.tms.obtain

import com.cobblemon.mod.common.api.tms.ObtainMethod
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * An [ObtainMethod] that triggers when the player has an advancement.
 */
class PlayerHasAdvancementObtainMethod : ObtainMethod {

    companion object {
        val ID = cobblemonResource("advancement")
    }

    override val passive = true
    val advancement: Identifier? = null

    override fun matches(player: ServerPlayerEntity): Boolean {
        if (advancement == null) return false

        for (entry in player.advancementTracker.progress) {
            if (entry.key.id == advancement && entry.value.isDone) return true
        }
        return false
    }

}