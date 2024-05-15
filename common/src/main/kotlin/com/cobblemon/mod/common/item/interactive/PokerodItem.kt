/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.advancement.CobblemonCriteria
import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.item.BerryItem
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.util.lang
import it.unimi.dsi.fastutil.objects.ObjectLists
import net.minecraft.client.item.TooltipContext
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class PokerodItem(val pokeRodId: Identifier, settings: Settings?) : FishingRodItem(settings) {
    var bait: ItemStack = ItemStack.EMPTY
    var baitEffects: List<FishingBait.Effect>? = FishingBaits.getFromItemStack(bait)?.effects

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        // if item in mainhand is berry item then don't do anything
        if (user.getStackInHand(Hand.MAIN_HAND).item is BerryItem)
            return TypedActionResult(ActionResult.FAIL, user.getStackInHand(hand))

        val itemStack = user.getStackInHand(hand)
        val offHandItem = user.getStackInHand(Hand.OFF_HAND)
        var tooltipList: MutableList<Text> = mutableListOf()
        if (!world.isClient && user.fishHook == null && FishingBaits.isFishingBait(offHandItem) && !ItemStack.areItemsEqual(offHandItem, bait) && !user.isSneaking) {
            // swap baits if one is already on the hook
            if (bait != ItemStack.EMPTY) {
                user.dropStack(bait) // drop old bait
                bait = itemStack // apply new bait
            }

            // apply it to the rod as bait
            bait = offHandItem.item.defaultStack

            // decrement 1 stack of that item from the other hand
            offHandItem.decrement(1)

            // todo add dynamic tooltip here
            appendTooltip(itemStack, world, tooltipList, TooltipContext.BASIC)
            //(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
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
                CobblemonCriteria.CAST_POKE_ROD.trigger(user as ServerPlayerEntity, !bait.isEmpty)
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START)
        }
        return TypedActionResult.success(itemStack, world.isClient())
    }

    override fun getEnchantability(): Int {
        return 1
    }

    // todo lang stuff for dynamic tooltips
    //lang("overflow_no_space", pc.name)

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        val rod = PokeRods.getPokeRod((stack.item as PokerodItem).pokeRodId) ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return
        tooltip.add(ball.item.name.copy().gray())
        // todo for every effect of the bait add a tooltip to the rod
        baitEffects?.forEach {
            val effectType = it.type.namespace.toString()
            val effectSubcategory: String? = it.subcategory?.namespace.toString()
            val effectChance = it.chance * 100 // chance of effect out of 100
            val effectValue = it.value.toInt()
            var subcategoryString: String? = null

            // todo convert subcategory depending on Effect Type to be used as variable
            if (effectSubcategory != null) {
                if (effectType == "nature" || effectType == "ev" || effectType == "iv") {
                    subcategoryString = com.cobblemon.mod.common.api.pokemon.stats.Stats.getStat(effectSubcategory).name
                }
                else if (effectType == "gender") {
                    subcategoryString = Gender.valueOf(effectSubcategory).name
                }
                else if (effectType == "tera") {
                    subcategoryString = ElementalTypes.get(effectSubcategory)?.name
                }
            }

            // for effects with only chance
            if (effectType == "shiny_reroll" ||
                effectType == "ha_chance" ||
                effectType == "pokemon_chance") {
                tooltip.add(lang("fishing_bait_effects." + effectType + ".tooltip", it.chance))
            }

            //for effects with only chance and value
            if (effectType == "bite_time" ||
                effectType == "level_raise" ||
                effectType == "friendship") {
                tooltip.add(lang("fishing_bait_effects." + effectType + ".tooltip", it.chance, it.value))
            }

            // for effects with subcategories and 2 params
            if (effectType == "nature" ||
                effectType == "ev" ||
                effectType == "gender" ||
                effectType == "tera") {
                tooltip.add(lang("fishing_bait_effects." + effectType + ".tooltip", it.chance, subcategoryString!!))
            }


            // for effects with subcategories and 3 params
            if (effectType == "iv") {
                tooltip.add(lang("fishing_bait_effects." + effectType + ".tooltip", it.chance, subcategoryString!!, it.value))
            }
        }

        super.appendTooltip(stack, world, tooltip, context)
    }

    /*fun removeBaitTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        val rod = PokeRods.getPokeRod((stack.item as PokerodItem).pokeRodId) ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return
        tooltip.removeAt(tooltip.size - 1) // remove the last tooltip todo maybe find a way to store the tooltip index as a local variable so it can be sure that it is the right tooltip deleted
    }*/

    override fun getTranslationKey(): String {
        return "item.cobblemon.poke_rod"
    }

}
