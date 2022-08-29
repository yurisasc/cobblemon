package com.cablemc.pokemoncobbled.common.api.drop

import com.cablemc.pokemoncobbled.common.util.substitute
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

/**
 * A drop entry which 'drops' by running a command. The command supports the following placeholders:
 * {{player}} - the player name.
 * {{world}} - the identifier for the world it's being 'dropped', such as minecraft:the_overworld
 * {{x}} - The decimal x coordinate of where it's being 'dropped'.
 * {{y}} - The decimal y coordinate of where it's being 'dropped'.
 * {{z}} - The decimal z coordinate of where it's being 'dropped'.
 *
 * If [requiresPlayer] is true, then it will not be able to drop without a player being the cause of the drop.
 *
 * @author Hiroku
 * @since July 24th, 2022
 */
class CommandDropEntry : DropEntry {
    val requiresPlayer = true
    val command = ""
    override val percentage = 100F
    override val quantity = 1
    override val maxSelectableTimes = 1

    override fun drop(entity: LivingEntity?, world: ServerWorld, pos: Vec3d, player: ServerPlayerEntity?) {
        if (requiresPlayer && player == null) {
            return
        }

        world.server.commandManager.executeWithPrefix(
            world.server.commandSource,
            command.substitute("player", player?.name?.string ?: "")
                .substitute("world", world.registryKey.value)
                .substitute("x", pos.x)
                .substitute("y", pos.y)
                .substitute("z", pos.z)
        )
    }
}