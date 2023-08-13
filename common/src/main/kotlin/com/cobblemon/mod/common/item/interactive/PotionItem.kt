/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.item.PokemonSelectingItem
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.battle.BagItem
import com.cobblemon.mod.common.pokemon.Pokemon
import java.lang.Integer.min
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class PotionItem(val type: PotionType) : CobblemonItem(Settings()), PokemonSelectingItem {
    override val bagItem = type
    override fun canUseOnPokemon(pokemon: Pokemon) = !pokemon.isFullHealth() && pokemon.currentHealth > 0
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (user is ServerPlayerEntity) {
            return use(user, user.getStackInHand(hand))
        }
        return super<CobblemonItem>.use(world, user, hand)
    }

    override fun applyToPokemon(
        player: ServerPlayerEntity,
        stack: ItemStack,
        pokemon: Pokemon
    ): TypedActionResult<ItemStack>? {
        if (pokemon.isFullHealth()) {
            return TypedActionResult.fail(stack)
        }

        val healthToRestore = type.amountToHeal
        pokemon.currentHealth = min(pokemon.currentHealth + healthToRestore, pokemon.hp)
        if (type.curesStatus) {
            pokemon.status = null
        }
        player.playSound(CobblemonSounds.MEDICINE_SPRAY_USE, SoundCategory.PLAYERS, 1F, 1F)
        return TypedActionResult.success(stack)
    }

    override fun applyToBattlePokemon(player: ServerPlayerEntity, stack: ItemStack, battlePokemon: BattlePokemon) {
        super.applyToBattlePokemon(player, stack, battlePokemon)
        player.playSound(CobblemonSounds.MEDICINE_SPRAY_USE, SoundCategory.PLAYERS, 1F, 1F)
    }
}

enum class PotionType(val amountToHeal: Int, val curesStatus: Boolean) : BagItem {
    POTION(20, false) {
        override val itemName = "item.cobblemon.potion"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "potion $amountToHeal"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    },
    SUPER_POTION(50, false) {
        override val itemName = "item.cobblemon.super_potion"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "potion $amountToHeal"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    },
    HYPER_POTION(200, false) {
        override val itemName = "item.cobblemon.hyper_potion"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "potion $amountToHeal"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    },
    MAX_POTION(999999, false) {
        override val itemName = "item.cobblemon.max_potion"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "potion ${battlePokemon.maxHealth - battlePokemon.health}"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    },
    FULL_RESTORE(999999, true) {
        override val itemName = "item.cobblemon.full_restore"
        override fun getShowdownInput(actor: BattleActor, battlePokemon: BattlePokemon, data: String?) = "full_restore"
        override fun canUse(battle: PokemonBattle, target: BattlePokemon) =  target.health < target.maxHealth && target.health > 0
    }
}