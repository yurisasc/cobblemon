package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.item.CobbledItem
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

/**
 * An item that will affect an [Entity].
 *
 * @param T The type of the [Entity] this item will affect.
 *
 * @param properties The [Item.Settings] of this item.
 */
abstract class InteractiveItem<T : Entity>(properties: Settings) : CobbledItem(properties) {

    /**
     * Fired when a [ServerPlayerEntity] interacts with the target entity.
     *
     * @param player The [ServerPlayerEntity] interacting with the [entity].
     * @param entity The [Entity] of type [T] being interacted.
     * @param stack The [ItemStack] used in this interaction. [ItemStack.getItem] will always be of the same type as this [InteractiveItem].
     */
    abstract fun onInteraction(player: ServerPlayerEntity, entity: T, stack: ItemStack)

}