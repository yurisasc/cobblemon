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
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
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
        val bobber = PokeRods.getPokeRod(Registries.ITEM.getId(itemStack.item))?.bobberType ?: CobblemonItems.POKE_BALL.defaultStack // get the default stack of the pokeball used as the bobber
        val lineRGB = PokeRods.getPokeRod(Registries.ITEM.getId(itemStack.item))?.lineColor ?: Triple(0,0,0) // get the line color RGB values used for the fishing line

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
                val bobberEntity = PokeRodFishingBobberEntity(user, bobber, lineRGB, world, j, i)
                world.spawnEntity(bobberEntity)
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START)
        }
        return TypedActionResult.success(itemStack, world.isClient())
    }

    fun getBobberFromItem(itemStack: ItemStack): ItemStack {
        return when {
            itemStack.item == CobblemonItems.AZURE_ROD -> CobblemonItems.AZURE_BALL.defaultStack
            itemStack.item == CobblemonItems.CHERISH_ROD -> CobblemonItems.CHERISH_BALL.defaultStack
            itemStack.item == CobblemonItems.CITRINE_ROD -> CobblemonItems.CITRINE_BALL.defaultStack
            itemStack.item == CobblemonItems.DIVE_ROD -> CobblemonItems.DIVE_BALL.defaultStack
            itemStack.item == CobblemonItems.DREAM_ROD -> CobblemonItems.DREAM_BALL.defaultStack
            itemStack.item == CobblemonItems.DUSK_ROD -> CobblemonItems.DUSK_BALL.defaultStack
            itemStack.item == CobblemonItems.FAST_ROD -> CobblemonItems.FAST_BALL.defaultStack
            itemStack.item == CobblemonItems.FRIEND_ROD -> CobblemonItems.FRIEND_BALL.defaultStack
            itemStack.item == CobblemonItems.GREAT_ROD -> CobblemonItems.GREAT_BALL.defaultStack
            itemStack.item == CobblemonItems.HEAL_ROD -> CobblemonItems.HEAL_BALL.defaultStack
            itemStack.item == CobblemonItems.HEAVY_ROD -> CobblemonItems.HEAVY_BALL.defaultStack
            itemStack.item == CobblemonItems.LEVEL_ROD -> CobblemonItems.LEVEL_BALL.defaultStack
            itemStack.item == CobblemonItems.LOVE_ROD -> CobblemonItems.LOVE_BALL.defaultStack
            itemStack.item == CobblemonItems.LURE_ROD -> CobblemonItems.LURE_BALL.defaultStack
            itemStack.item == CobblemonItems.LUXURY_ROD -> CobblemonItems.LUXURY_BALL.defaultStack
            itemStack.item == CobblemonItems.MASTER_ROD -> CobblemonItems.MASTER_BALL.defaultStack
            itemStack.item == CobblemonItems.MOON_ROD -> CobblemonItems.MOON_BALL.defaultStack
            itemStack.item == CobblemonItems.NEST_ROD -> CobblemonItems.NEST_BALL.defaultStack
            itemStack.item == CobblemonItems.NET_ROD -> CobblemonItems.NET_BALL.defaultStack
            itemStack.item == CobblemonItems.PARK_ROD -> CobblemonItems.PARK_BALL.defaultStack
            itemStack.item == CobblemonItems.POKE_ROD -> CobblemonItems.POKE_BALL.defaultStack
            itemStack.item == CobblemonItems.PREMIER_ROD -> CobblemonItems.PREMIER_BALL.defaultStack
            itemStack.item == CobblemonItems.QUICK_ROD -> CobblemonItems.QUICK_BALL.defaultStack
            itemStack.item == CobblemonItems.REPEAT_ROD -> CobblemonItems.REPEAT_BALL.defaultStack
            itemStack.item == CobblemonItems.ROSEATE_ROD -> CobblemonItems.ROSEATE_BALL.defaultStack
            itemStack.item == CobblemonItems.SAFARI_ROD -> CobblemonItems.SAFARI_BALL.defaultStack
            itemStack.item == CobblemonItems.SLATE_ROD -> CobblemonItems.SLATE_BALL.defaultStack
            itemStack.item == CobblemonItems.SPORT_ROD -> CobblemonItems.SPORT_BALL.defaultStack
            itemStack.item == CobblemonItems.TIMER_ROD -> CobblemonItems.TIMER_BALL.defaultStack
            itemStack.item == CobblemonItems.ULTRA_ROD -> CobblemonItems.ULTRA_BALL.defaultStack
            itemStack.item == CobblemonItems.VERDANT_ROD -> CobblemonItems.VERDANT_BALL.defaultStack
            else -> CobblemonItems.POKE_BALL.defaultStack // Return a default
        }
    }

    fun getLineColorFromItem(itemStack: ItemStack): Triple<Int, Int, Int> {
        return when {
            itemStack.item == CobblemonItems.AZURE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.CHERISH_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.CITRINE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.DIVE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.DREAM_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.DUSK_ROD -> Triple(139,0,0)
            itemStack.item == CobblemonItems.FAST_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.FRIEND_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.GREAT_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.HEAL_ROD -> Triple(0,150,255)
            itemStack.item == CobblemonItems.HEAVY_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.LEVEL_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.LOVE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.LURE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.LUXURY_ROD -> Triple(250,250,250)
            itemStack.item == CobblemonItems.MASTER_ROD -> Triple(203,195,227)
            itemStack.item == CobblemonItems.MOON_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.NEST_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.NET_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.PARK_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.POKE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.PREMIER_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.QUICK_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.REPEAT_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.ROSEATE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.SAFARI_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.SLATE_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.SPORT_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.TIMER_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.ULTRA_ROD -> Triple(0,0,0)
            itemStack.item == CobblemonItems.VERDANT_ROD -> Triple(0,0,0)
            else -> Triple(0,0,0)
        }
    }

    override fun getEnchantability(): Int {
        return 1
    }
}
