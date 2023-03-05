package com.cobblemon.mod.common.trading

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.pokemon.Pokemon
import java.util.UUID
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity

class ActiveTrade(val player1: ServerPlayerEntity, val player2: ServerPlayerEntity) {
    val player1Offer = TradeOffer()
    val player2Offer = TradeOffer()

    fun getOffer(player: ServerPlayerEntity) = if (player == player1) player1Offer else player2Offer
    fun getOpposingOffer(player: ServerPlayerEntity) = if (player == player1) player2Offer else player1Offer

    fun updateOffer(player: ServerPlayerEntity, pokemon: Pokemon) {
        getOffer(player).updateOffer(pokemon)
    }

    fun acceptOffer(player: ServerPlayerEntity) {
        getOpposingOffer(player).accepted = true
    }

    fun performTrade() {
        TradeManager.performTrade(
            player1 = player1,
            player2 = player2,
            pokemon1 = player1Offer.pokemon!!,
            pokemon2 = player2Offer.pokemon!!
        )
    }
}