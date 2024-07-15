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
import com.cobblemon.mod.common.api.text.blue
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
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.FishingRodItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class PokerodItem(val pokeRodId: ResourceLocation, settings: Properties) : FishingRodItem(settings) {

    companion object {
        fun getBaitOnRod(stack: ItemStack): FishingBait? {
            return stack.components.get(CobblemonItemComponents.BAIT)?.bait
        }

        fun setBait(stack: ItemStack, bait: ItemStack) {
            if (bait.isEmpty) {
                stack.set<RodBaitComponent>(CobblemonItemComponents.BAIT, null)
                return
            }
            val fishingBait = FishingBaits.getFromBaitItemStack(bait) ?: return
            stack.set(CobblemonItemComponents.BAIT, RodBaitComponent(fishingBait))
            // add a new component that stores the itemStack as a component?
        }

        fun getBaitEffects(stack: ItemStack): List<FishingBait.Effect> {
            return getBaitOnRod(stack)?.effects ?: return emptyList()
        }
    }

    //var bait: ItemStack = ItemStack.EMPTY
    //var baitEffects: List<FishingBait.Effect>? = mutableListOf()

    override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        // if item in mainhand is berry item then don't do anything
        if (user.getItemInHand(InteractionHand.MAIN_HAND).item is BerryItem)
            return InteractionResultHolder(
                InteractionResult.FAIL,
                user.getItemInHand(hand)
            )

        val itemStack = user.getItemInHand(hand)
        val offHandItem = user.getItemInHand(InteractionHand.OFF_HAND)
        val offHandBait = FishingBaits.getFromBaitItemStack(offHandItem)

        // if there already is bait on the bobber then drop it on the ground
        var baitOnRod = getBaitOnRod(itemStack)

        // if the item in the offhand is a bait item and the mainhand item is a pokerod then apply the bait
        if (!world.isClientSide && user.fishing == null && offHandBait != null && offHandBait != baitOnRod && !user.isShiftKeyDown) {

            if (baitOnRod != null) {
                val item = world.itemRegistry.get(baitOnRod.item)
                if (item != null) {
                    user.spawnAtLocation(ItemStack(item))
                }
            }

            // set the bait and bait effects on the bobber
            setBait(itemStack, offHandItem.copyWithCount(1))

            // remove 1 bait from the offhand
            offHandItem.shrink(1)

            // remove old bait tooltip from rod
//            removeBaitTooltip(itemStack, world)

            // set new bait tooltip to rod
//            setBaitTooltips(itemStack, world)
        }

        // if the user is sneaking when casting then remove the bait from the bobber
//        if (!world.isClientSide && user.fishing == null && user.isShiftKeyDown) {
//            // If there is a bait on the bobber
//            if (baitOnRod != null) {
//                // drop the stack of bait
//                val item = world.itemRegistry.get(baitOnRod.item)
//                if (item != null) {
//                    user.spawnAtLocation(ItemStack(item))
//                }
//                //set the bait and bait effects on the rod to be empty
//                setBait(itemStack, ItemStack.EMPTY)
//
//                // remove old bait tooltip from rod
//                removeBaitTooltip(itemStack, world)
//            }
//        }

        val i: Int
        if (user.fishing != null) { // if the bobber is out yet
            if (!world.isClientSide) {
                i = user.fishing!!.retrieve(itemStack)
                itemStack.hurtAndBreak(i, user, hand.toEquipmentSlot())
            }

            // stop sound of casting when reeling in
            //(MinecraftClient.getInstance().getSoundManager() as SoundManagerDuck).stopSounds(CobblemonSounds.FISHING_ROD_CAST.id, SoundCategory.PLAYERS)



            //(MinecraftClient.getInstance().getSoundManager()).stop

            world.playSound(null as Player?, user.x, user.y, user.z, CobblemonSounds.FISHING_ROD_REEL_IN, SoundSource.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
            user.gameEvent(GameEvent.ITEM_INTERACT_FINISH)
        } else { // if the bobber is not out yet
            // play the Rod casting sound and set it
            world.playSound(null as Player?, user.x, user.y, user.z, CobblemonSounds.FISHING_ROD_CAST, SoundSource.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f))

            // create a SoundInstance for the casting sound to be also sent to the bobber
            val castingSoundInstance = SimpleSoundInstance(
                    CobblemonSounds.FISHING_ROD_CAST,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f / (world.getRandom().nextFloat() * 0.4f + 0.8f),
                    world.random,
                    user.x,
                    user.y,
                    user.z
            )

            if (!world.isClientSide) {
                val lureLevel = EnchantmentHelper.getItemEnchantmentLevel(world.enchantmentRegistry.getHolder(Enchantments.LURE).get(), itemStack)
                val luckLevel = EnchantmentHelper.getItemEnchantmentLevel(world.enchantmentRegistry.getHolder(Enchantments.LUCK_OF_THE_SEA).get(), itemStack)

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


                val bobberEntity = PokeRodFishingBobberEntity(user, pokeRodId, getBaitOnRod(itemStack)?.toItemStack(world.itemRegistry) ?: ItemStack.EMPTY, world, luckLevel, lureLevel, castingSoundInstance)

                // Set the casting sound to the bobber entity
                //bobberEntity.castingSound = castingSoundInstance

                world.addFreshEntity(bobberEntity)
                CobblemonCriteria.CAST_POKE_ROD.trigger(user as ServerPlayer, baitOnRod != null)
            }

            user.awardStat(Stats.ITEM_USED.get(this))
            user.gameEvent(GameEvent.ITEM_INTERACT_START)
        }
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide)
    }

    override fun getEnchantmentValue(): Int {
        return 1
    }

    override fun appendHoverText(
        stack: ItemStack,
        tooltipContext: TooltipContext,
        tooltip: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val rod = (stack.item as? PokerodItem)?.pokeRodId?.let { PokeRods.getPokeRod(it) } ?: return
        val ball = PokeBalls.getPokeBall(rod.pokeBallId) ?: return

        // Add the description of the Poke Ball used in the rod
        ball.item.description?.let {
            val bobberDescription = Component.literal("Bobber: ").append(it.copy().gray())
            tooltip.add(bobberDescription)
        }

        val client = Minecraft.getInstance()
        val itemRegistry = client.level?.registryAccess()?.registryOrThrow(Registries.ITEM)
        itemRegistry?.let { registry ->
            FishingBaits.getFromRodItemStack(stack)?.toItemStack(registry)?.item?.description?.copy()?.gray()?.let {
                tooltip.add(Component.literal("Bait: ").append(it))
            }
        }

        val formatter = DecimalFormat("0.##")
        getBaitEffects(stack).takeIf { it.isNotEmpty() }?.also {
            tooltip.add(Component.literal("")) // blank line
            tooltip.add(lang("fishing_bait_effect_header").blue())
        }?.forEach { effect ->
            val effectType = effect.type.path.toString()
            val effectSubcategory = effect.subcategory?.path.toString()
            var effectChance = effect.chance * 100
            val effectValue = when (effectType) {
                "bite_time" -> (effect.value * 100).toInt()
                else -> effect.value.toInt()
            }

            val subcategoryString = when (effectType) {
                "nature", "ev", "iv" -> effectSubcategory?.let { sub ->
                    com.cobblemon.mod.common.api.pokemon.stats.Stats.getStat(sub)?.name?.replace("_"," ")
                        ?.split(" ")?.joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } } ?: ""
                }
                "gender" -> Gender.valueOf(effectSubcategory ?: "").name
                    .split('_')
                    .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } }
                "tera" -> ElementalTypes.get(effectSubcategory)?.name
                    ?.split('_')
                    ?.joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } } ?: ""
                else -> ""
            }

            // handle reformatting of shiny chance effectChance
            if (effectType == "shiny_reroll") {
                effectChance = BigDecimal((effectChance / 100.0) + 1).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            }

            tooltip.add(
                lang(
                    "fishing_bait_effects.$effectType.tooltip",
                    formatter.format(effectChance),
                    subcategoryString,
                    effectValue.toString()
                )
            )
        }
    }

    override fun getDescriptionId(): String {
        return "item.cobblemon.poke_rod"
    }

}
