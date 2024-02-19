/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class PokerodItem(settings: Settings?) : FishingRodItem(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        val bobber = getBobberFromItem(itemStack)
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
                val bobberEntity = PokeRodFishingBobberEntity(user, bobber, world, j, i)
                world.spawnEntity(bobberEntity)
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START)
        }
        return TypedActionResult.success(itemStack, world.isClient())
    }

    fun getBobberFromItem(itemStack: ItemStack): ItemStack {
        return when (itemStack) {
                CobblemonItems.AZURE_ROD.defaultStack -> CobblemonItems.AZURE_BALL.defaultStack
                CobblemonItems.CHERISH_ROD.defaultStack -> CobblemonItems.CHERISH_BALL.defaultStack
                CobblemonItems.CITRINE_ROD.defaultStack -> CobblemonItems.CITRINE_BALL.defaultStack
                CobblemonItems.DIVE_ROD.defaultStack -> CobblemonItems.DIVE_BALL.defaultStack
                CobblemonItems.DREAM_ROD.defaultStack -> CobblemonItems.DREAM_BALL.defaultStack
                CobblemonItems.DUSK_ROD.defaultStack -> CobblemonItems.DUSK_BALL.defaultStack
                CobblemonItems.FAST_ROD.defaultStack -> CobblemonItems.FAST_BALL.defaultStack
                CobblemonItems.FRIEND_ROD.defaultStack -> CobblemonItems.FRIEND_BALL.defaultStack
                CobblemonItems.GREAT_ROD.defaultStack -> CobblemonItems.GREAT_BALL.defaultStack
                CobblemonItems.HEAL_ROD.defaultStack -> CobblemonItems.HEAL_BALL.defaultStack
                CobblemonItems.HEAVY_ROD.defaultStack -> CobblemonItems.HEAVY_BALL.defaultStack
                CobblemonItems.LEVEL_ROD.defaultStack -> CobblemonItems.LEVEL_BALL.defaultStack
                CobblemonItems.LOVE_ROD.defaultStack -> CobblemonItems.LOVE_BALL.defaultStack
                CobblemonItems.LURE_ROD.defaultStack -> CobblemonItems.LURE_BALL.defaultStack
                CobblemonItems.LUXURY_ROD.defaultStack -> CobblemonItems.LUXURY_BALL.defaultStack
                CobblemonItems.MASTER_ROD.defaultStack -> CobblemonItems.MASTER_BALL.defaultStack
                CobblemonItems.MOON_ROD.defaultStack -> CobblemonItems.MOON_BALL.defaultStack
                CobblemonItems.NEST_ROD.defaultStack -> CobblemonItems.NEST_BALL.defaultStack
                CobblemonItems.NET_ROD.defaultStack -> CobblemonItems.NET_BALL.defaultStack
                CobblemonItems.PARK_ROD.defaultStack -> CobblemonItems.PARK_BALL.defaultStack
                CobblemonItems.POKE_ROD.defaultStack -> CobblemonItems.POKE_BALL.defaultStack
                CobblemonItems.PREMIER_ROD.defaultStack -> CobblemonItems.PREMIER_BALL.defaultStack
                CobblemonItems.QUICK_ROD.defaultStack -> CobblemonItems.QUICK_BALL.defaultStack
                CobblemonItems.REPEAT_ROD.defaultStack -> CobblemonItems.REPEAT_BALL.defaultStack
                CobblemonItems.ROSEATE_ROD.defaultStack -> CobblemonItems.ROSEATE_BALL.defaultStack
                CobblemonItems.SAFARI_ROD.defaultStack -> CobblemonItems.SAFARI_BALL.defaultStack
                CobblemonItems.SLATE_ROD.defaultStack -> CobblemonItems.SLATE_BALL.defaultStack
                CobblemonItems.SPORT_ROD.defaultStack -> CobblemonItems.SPORT_BALL.defaultStack
                CobblemonItems.TIMER_ROD.defaultStack -> CobblemonItems.TIMER_BALL.defaultStack
                CobblemonItems.ULTRA_ROD.defaultStack -> CobblemonItems.ULTRA_BALL.defaultStack
                CobblemonItems.VERDANT_ROD.defaultStack -> CobblemonItems.VERDANT_BALL.defaultStack
            else -> CobblemonItems.POKE_BALL.defaultStack // Return a default
        }
    }

    override fun getEnchantability(): Int {
        return 1
    }
}
