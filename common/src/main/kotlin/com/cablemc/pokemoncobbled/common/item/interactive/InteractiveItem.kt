package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.item.CobbledItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack

/**
 * An item that will affect an [Entity].
 *
 * @param T The type of the [Entity] this item will affect.
 *
 * @param properties The [Properties] of this item.
 */
abstract class InteractiveItem<T : Entity>(properties: Properties) : CobbledItem(properties) {

    /**
     * Fired when a [ServerPlayer] interacts with the target entity.
     *
     * @param player The [ServerPlayer] interacting with the [entity].
     * @param entity The [Entity] of type [T] being interacted.
     * @param stack The [ItemStack] used in this interaction. [ItemStack.getItem] will always be of the same type as this [InteractiveItem].
     */
    abstract fun onInteraction(player: ServerPlayer, entity: T, stack: ItemStack)

}