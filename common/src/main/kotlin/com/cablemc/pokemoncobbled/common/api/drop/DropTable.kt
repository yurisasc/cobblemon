package com.cablemc.pokemoncobbled.common.api.drop

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.LOOT_DROPPED
import com.cablemc.pokemoncobbled.common.api.events.drops.LootDroppedEvent
import kotlin.random.Random
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

/**
 * A table of drops that can produce a list of [DropEntry]. You can produce a drop list from [getDrops] to
 * then handle the drops yourself, or make it select and perform the drop by running [drop].
 *
 * A drop table is defined by its [entries], and by the [amount] range which act as the default constraint around the
 * total dropped quantity. These are only defaults, as both [getDrops] and [drop] allow the range to be overwritten.
 * These amounts are based on [DropEntry.quantity]. In some cases a single entry can be worth multiple. An example of
 * this is an item drop which drops 3 of an item. While it may only be 1 entry, it is considered 3 drops.
 *
 * A drop can be guaranteed by having a [DropEntry.percentage] of 100. This will still obey the rules around how many
 * times it can be repeated and not being selected if its quantity would exceed the amount that has been chosen for
 * the drop. For example, a 5 item drop for a drop action maxed out at 3 will never be selected even if the 5 item drop
 * was marked as a guaranteed entry.
 *
 * @author Hiroku
 * @since July 24th, 2022
 */
class DropTable {
    /** All [DropEntry] values in the drop table. */
    val entries = mutableListOf<DropEntry>()
    /** The default range of values which might be selected to decide the 'quantity' of drops for a drop attempt. */
    val amount = 1..1

    /**
     * Gets a drop list from this table as a list of [DropEntry]. You can specify the range of drop quantity totals
     * that will be possible, or if you leave it blank it will use [DropTable.amount].
     */
    fun getDrops(amount: IntRange = this.amount): List<DropEntry> {
        val chosenAmount = amount.random()
        val possibleDrops = entries.filter { it.quantity <= chosenAmount }.toMutableList()

        if (possibleDrops.isEmpty()) {
            return emptyList()
        }

        val drops = mutableListOf<DropEntry>()
        var dropCount = 0

        do {
            val drop = possibleDrops.firstOrNull { Random.Default.nextFloat() * 100F < it.percentage }

            if (drop == null) {
                // That counts as a drop in the eyes of the law, otherwise we'd be looping all week and percentages won't mean squat.
                dropCount++
                continue
            }

            drops.add(drop)
            dropCount += drop.quantity
            val remaining = chosenAmount - dropCount
            possibleDrops.removeIf { (it == drop && it.maxSelectableTimes <= drops.count { it == drop }) || it.quantity > remaining }
        } while (dropCount < chosenAmount && possibleDrops.isNotEmpty())

        return drops
    }

    /**
     * Performs a drop, including posting the [LootDroppedEvent]. The entity that is dropping it and the player that caused
     * the drop are both optional, but in some cases a drop will only be possible when these fields are non-null. A
     * command drop that needs the player for its command will do nothing when it is dropped without a player cause.
     *
     * The amount of drop entries that are possible can be changed from the [amount] parameter, and if left blank it
     * will fall back to the [DropTable.amount] range.
     */
    fun drop(
        entity: LivingEntity?,
        world: ServerWorld,
        pos: Vec3d,
        player: ServerPlayerEntity?,
        amount: IntRange = this.amount
    ) {
        val drops = getDrops(amount).toMutableList()
        LOOT_DROPPED.postThen(
            event = LootDroppedEvent(this, player, entity, drops),
            ifSucceeded = { it.drops.forEach { it.drop(entity, world, pos, player) } }
        )
    }
}