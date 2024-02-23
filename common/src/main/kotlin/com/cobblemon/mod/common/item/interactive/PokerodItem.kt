/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.item.BerryItem
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class PokerodItem(val pokeRodId: Identifier, settings: Settings?) : FishingRodItem(settings) {
    var bait: ItemStack = ItemStack.EMPTY

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        val otherHand = if (hand == Hand.MAIN_HAND) Hand.OFF_HAND else Hand.MAIN_HAND
        val otherHandItem = user.getStackInHand(otherHand)
        if (!world.isClient && user.fishHook == null && otherHandItem.item is BerryItem && !user.isSneaking) {
            // swap baits if one is already on the hook
            if (bait != ItemStack.EMPTY) {
                user.dropStack(bait) // drop old bait
                bait = itemStack // apply new bait
            }

            // apply it to the rod as bait
            bait = otherHandItem.item.defaultStack

            // decrement 1 stack of that item from the other hand
            otherHandItem.decrement(1)
        }
        if (!world.isClient && user.fishHook == null && user.isSneaking) {
            // remove bait if one is already on the hook
            if (bait != ItemStack.EMPTY) {
                user.dropStack(bait) // drop old bait on the ground
                bait = ItemStack.EMPTY
            }
        }

        val i: Int
        if (user.fishHook != null) { // if the bobber is out yet
            if (!world.isClient) {
                i = user.fishHook!!.use(itemStack)
                itemStack.damage(i, user) { p: PlayerEntity -> p.sendToolBreakStatus(hand) }
            }
            world.playSound(null as PlayerEntity?, user.x, user.y, user.z, SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH)
        } else { // if the bobber is not out yet
            world.playSound(null as PlayerEntity?, user.x, user.y, user.z, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
            if (!world.isClient) {
                i = EnchantmentHelper.getLure(itemStack)
                val j = EnchantmentHelper.getLuckOfTheSea(itemStack)
                val bobberEntity = PokeRodFishingBobberEntity(user, pokeRodId, bait/*Registries.ITEM.getId(bait?.item)*/, world, j, i)
                world.spawnEntity(bobberEntity)
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START)
        }
        return TypedActionResult.success(itemStack, world.isClient())
    }

    override fun getEnchantability(): Int {
        return 1
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        val rod = PokeRods.getPokeRod((stack.item as PokerodItem).pokeRodId) ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return
        tooltip.add(ball.item.name.copy().gray())
        super.appendTooltip(stack, world, tooltip, context)
    }

    override fun getTranslationKey(): String {
        return "item.cobblemon.poke_rod"
    }

}
