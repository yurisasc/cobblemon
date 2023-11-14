/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.berry

import com.bedrockk.molang.Expression
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.item.PokemonSelectingItem
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.item.BerryItem
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.genericRuntime
import com.cobblemon.mod.common.util.resolveInt
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

/**
 * A berry that heals a Pokémon by a fixed amount.
 *
 * @author Hiroku
 * @since August 4th, 2023
 */
class HealingBerryItem(block: BerryBlock, val amount: () -> Expression): BerryItem(block), PokemonSelectingItem {
    override val bagItem = object : BagItem {
        override val itemName: String get() = "item.cobblemon.${this@HealingBerryItem.berry()!!.identifier.path}"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "potion ${ genericRuntime.resolveInt(amount(), battlePokemon) }"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    }

    override fun canUseOnPokemon(pokemon: Pokemon) = !pokemon.isFainted() && !pokemon.isFullHealth()
    override fun applyToPokemon(
        player: ServerPlayerEntity,
        stack: ItemStack,
        pokemon: Pokemon
    ): TypedActionResult<ItemStack>? {
        if (pokemon.isFullHealth() || pokemon.isFainted() || pokemon.isFull()) {
            if (pokemon.isFull()) {
                val message = "${pokemon.species.name} has eaten ${pokemon.currentFullness}/${pokemon.getMaxFullness()} times. It is too full!"
                player.sendMessage(Text.of(message))
            }
            else if (pokemon.isFullHealth()) {
                val message = "${pokemon.species.name} is at Max HP already!"
                player.sendMessage(Text.of(message))
            }

            return TypedActionResult.fail(stack)
        }
        pokemon.feedPokemon(1)
        //var message = "Fullness for ${pokemon.species.name} is ${pokemon.currentFullness}. It can eat ${(pokemon.getMaxFullness() - pokemon.currentFullness)} more poke_food"
        //player.sendMessage(Text.of(message))

        pokemon.currentHealth = Integer.min(pokemon.currentHealth + genericRuntime.resolveInt(amount(), pokemon), pokemon.hp)

        val fullnessPercent = ((pokemon.currentFullness).toFloat() / (pokemon.getMaxFullness()).toFloat()) * (.5).toFloat()

        //message = "Pitch for Berry Food is at ${(fullnessPercent) + 1F}"
        //player.sendMessage(Text.of(message))
        if (pokemon.currentFullness >= pokemon.getMaxFullness()) {
            player.playSound(CobblemonSounds.BERRY_EAT_FULL, SoundCategory.PLAYERS, 1F, 1F)
        }
        else {
            player.playSound(CobblemonSounds.BERRY_EAT, SoundCategory.PLAYERS, 1F, 1F + fullnessPercent)
        }

        if (!player.isCreative) {
            stack.decrement(1)
        }
        return TypedActionResult.success(stack)
    }

    override fun applyToBattlePokemon(player: ServerPlayerEntity, stack: ItemStack, battlePokemon: BattlePokemon) {
        super.applyToBattlePokemon(player, stack, battlePokemon)
        player.playSound(CobblemonSounds.BERRY_EAT, SoundCategory.PLAYERS, 1F, 1F)
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world is ServerWorld && user is ServerPlayerEntity) {
            return use(user, user.getStackInHand(hand))
        }
        return super<BerryItem>.use(world, user, hand)
    }
}