package com.cobblemon.mod.common.api.events.item

import com.cobblemon.mod.common.api.events.Cancelable
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Fired when eating an apple triggers the creation of a Leftovers. The stack that will be given
 * can be changed via [leftovers]. Cancelling the event will prevent a Leftovers from being given.
 *
 * @author Hiroku
 * @since March 11th, 2023
 */
class LeftoversCreatedEvent(val playerEntity: ServerPlayerEntity, var leftovers: ItemStack) : Cancelable()