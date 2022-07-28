package com.cablemc.pokemoncobbled.common.api.events.drops

import com.cablemc.pokemoncobbled.common.api.drop.DropEntry
import com.cablemc.pokemoncobbled.common.api.drop.DropTable
import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Cancelable event posted to [CobbledEvents.LOOT_DROPPED] when a [DropTable] is used to drop items. This
 * event is posted prior to the drops being performed, but after a drop list has been chosen.
 *
 * If the event is cancelled, nothing will be dropped. The final drop list that will be used can
 * be modified from [drops].
 *
 * @param table The [DropTable] that was used for the calculation.
 * @param player The player that is being targeted for the drop actions. This can be null if, for
 *               example, an entity dies and causes drops, but it died from falling.
 * @param entity The entity that is dropping the items, if relevant. In some cases a drop table
 *               could be used without an associated entity.
 * @param drops The [DropEntry] list that will be dropped. You can adjust this list, both to add and
 *               remove, and the final value of this list will be used for the drop action.
 *
 * @author Hiroku
 * @since July 24th, 2022
 */
class LootDroppedEvent(
    val table: DropTable,
    val player: ServerPlayerEntity?,
    val entity: LivingEntity?,
    val drops: MutableList<DropEntry>
) : Cancelable()