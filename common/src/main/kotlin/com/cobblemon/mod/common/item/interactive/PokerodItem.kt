/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.CobblemonItemComponents
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.advancement.CobblemonCriteria
import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.fishing.PokeRods
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.item.RodBaitComponent
import com.cobblemon.mod.common.item.berry.BerryItem
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.util.enchantmentRegistry
import com.cobblemon.mod.common.util.itemRegistry
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.toEquipmentSlot
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FishingRodItem
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.stat.Stats
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent


class PokerodItem(val pokeRodId: Identifier, settings: Settings?) : FishingRodItem(settings) {

    companion object {
        fun getBaitOnRod(stack: ItemStack): FishingBait? {
            return stack.components.get(CobblemonItemComponents.BAIT)?.bait
        }

        fun setBait(stack: ItemStack, bait: ItemStack) {
            if (bait.isEmpty) {
                stack.set<RodBaitComponent>(CobblemonItemComponents.BAIT, null)
                return
            }
            val fishingBait = FishingBaits.getFromRodItemStack(bait) ?: return
            stack.set(CobblemonItemComponents.BAIT, RodBaitComponent(fishingBait))
        }

        fun getBaitEffects(stack: ItemStack): List<FishingBait.Effect> {
            return getBaitOnRod(stack)?.effects ?: return emptyList()
        }
    }

    //var bait: ItemStack = ItemStack.EMPTY
    //var baitEffects: List<FishingBait.Effect>? = mutableListOf()

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        // if item in mainhand is berry item then don't do anything
        if (user.getStackInHand(Hand.MAIN_HAND).item is BerryItem)
            return TypedActionResult(ActionResult.FAIL, user.getStackInHand(hand))

        val itemStack = user.getStackInHand(hand)
        val offHandItem = user.getStackInHand(Hand.OFF_HAND)
        val offHandBait = FishingBaits.getFromBaitItemStack(offHandItem)

        // if there already is bait on the bobber then drop it on the ground
        var baitOnRod = getBaitOnRod(itemStack)

        // if the item in the offhand is a bait item and the mainhand item is a pokerod then apply the bait
        if (!world.isClient && user.fishHook == null && offHandBait != null && offHandBait != baitOnRod && !user.isSneaking) {

            if (baitOnRod != null) {
                val item = world.itemRegistry.get(baitOnRod.item)
                if (item != null) {
                    user.dropStack(ItemStack(item))
                }
            }

            // set the bait and bait effects on the bobber
            setBait(itemStack, offHandItem.copyWithCount(1))

            // remove 1 bait from the offhand
            offHandItem.decrement(1)

            // remove old bait tooltip from rod
            removeBaitTooltip(itemStack, world)

            // set new bait tooltip to rod
            setBaitTooltips(itemStack, world)
        }

        // if the user is sneaking when casting then remove the bait from the bobber
        if (!world.isClient && user.fishHook == null && user.isSneaking) {
            // If there is a bait on the bobber
            if (baitOnRod != null) {
                // drop the stack of bait
                val item = world.itemRegistry.get(baitOnRod.item)
                if (item != null) {
                    user.dropStack(ItemStack(item))
                }
                //set the bait and bait effects on the rod to be empty
                setBait(itemStack, ItemStack.EMPTY)

                // remove old bait tooltip from rod
                removeBaitTooltip(itemStack, world)
            }
        }

        val i: Int
        if (user.fishHook != null) { // if the bobber is out yet
            if (!world.isClient) {
                i = user.fishHook!!.use(itemStack)
                itemStack.damage(i, user, hand.toEquipmentSlot())
            }
            // stop sound of casting when reeling in
            //(MinecraftClient.getInstance().getSoundManager() as SoundManagerDuck).stopSounds(CobblemonSounds.FISHING_ROD_CAST.id, SoundCategory.PLAYERS)



            //(MinecraftClient.getInstance().getSoundManager()).stop

            world.playSound(null as PlayerEntity?, user.x, user.y, user.z, CobblemonSounds.FISHING_ROD_REEL_IN, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH)
        } else { // if the bobber is not out yet
            // play the Rod casting sound and set it
            world.playSound(null as PlayerEntity?, user.x, user.y, user.z, CobblemonSounds.FISHING_ROD_CAST, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f))

            // create a SoundInstance for the casting sound to be also sent to the bobber
            val castingSoundInstance = PositionedSoundInstance(
                    CobblemonSounds.FISHING_ROD_CAST,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f),
                    world.random,
                    user.x,
                    user.y,
                    user.z
            )

            if (!world.isClient) {
                val lureLevel = EnchantmentHelper.getLevel(world.enchantmentRegistry.getEntry(Enchantments.LURE).get(), itemStack)
                val luckLevel = EnchantmentHelper.getLevel(world.enchantmentRegistry.getEntry(Enchantments.LUCK_OF_THE_SEA).get(), itemStack)

                /*// play the Rod casting sound and set it
                world.playSound(null as PlayerEntity?, user.x, user.y, user.z, CobblemonSounds.FISHING_ROD_CAST, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f))

                // create a SoundInstance for the casting sound to be also sent to the bobber
                val castingSoundInstance = CancellableSoundInstance(
                        CobblemonSounds.FISHING_ROD_CAST,
                        SoundCategory.PLAYERS,
                        1.0f,
                        1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f),
                        world.random,
                        user.x,
                        user.y,
                        user.z
                )*/


                val bobberEntity = PokeRodFishingBobberEntity(user, pokeRodId, offHandBait?.toItemStack(world.itemRegistry) ?: ItemStack.EMPTY, world, luckLevel, lureLevel, castingSoundInstance)

                // Set the casting sound to the bobber entity
                //bobberEntity.castingSound = castingSoundInstance

                world.spawnEntity(bobberEntity)
                CobblemonCriteria.CAST_POKE_ROD.trigger(user as ServerPlayerEntity, baitOnRod != null)
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

    fun setBaitTooltips(
            stack: ItemStack,
            world: World?
    ) {
        val rod = PokeRods.getPokeRod((stack.item as PokerodItem).pokeRodId) ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return
        var tooltipList: MutableList<Text> = mutableListOf()
        //var rodTooltipData = stack.tooltipData.get()

        tooltipList.add(ball.item.name.copy().gray())
        // for every effect of the bait add a tooltip to the rod
        getBaitEffects(stack).forEach {
            val effectType = it.type.path.toString()
            val effectSubcategory: String? = it.subcategory?.path.toString()
            val effectChance = it.chance * 100 // chance of effect out of 100
            val effectValue = it.value.toInt()
            var subcategoryString: String? = null

            // convert subcategory depending on Effect Type to be used as a String variable in the lang file
            if (effectSubcategory != null) {
                if (effectType == "nature" || effectType == "ev" || effectType == "iv") {
                    subcategoryString = com.cobblemon.mod.common.api.pokemon.stats.Stats.getStat(effectSubcategory).name.replace("_"," ")
                }
                else if (effectType == "gender") {
                    subcategoryString = Gender.valueOf(effectSubcategory).name
                }
                else if (effectType == "tera") {
                    subcategoryString = ElementalTypes.get(effectSubcategory)?.name
                }
            }

            tooltipList.add(lang("fishing_bait_effects." + effectType + ".tooltip", effectChance, subcategoryString ?: "", effectValue))


        }
        val test = tooltipList

        //rodTooltipData = tooltipList // todo find some way to set the itemStack's tooltip to be this new tooltipList
    }

    fun removeBaitTooltip(
            stack: ItemStack,
            world: World?
    ) {
        val rod = PokeRods.getPokeRod((stack.item as PokerodItem).pokeRodId) ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return
        var tooltipList: MutableList<Text> = mutableListOf()
        //var rodTooltipData = stack.tooltipData.get()

        tooltipList.add(ball.item.name.copy().gray())
        val test = tooltipList

        //rodTooltipData = tooltipList // todo find some way to set the itemStack's tooltip to be this new tooltipList
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        tooltipType: TooltipType
    ) {
        val rod = PokeRods.getPokeRod((stack.item as PokerodItem).pokeRodId) ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return
        tooltip.add(ball.item.name.copy().gray())

        super.appendTooltip(stack, context, tooltip, tooltipType)
    }

    override fun getTranslationKey(): String {
        return "item.cobblemon.poke_rod"
    }

}
