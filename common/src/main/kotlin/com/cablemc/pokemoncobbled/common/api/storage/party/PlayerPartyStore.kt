package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID
import kotlin.random.Random

/**
 * A [PartyStore] used for a single player. This uses the player's UUID as the store's UUID, and is declared as its own
 * class so that the purpose of this store is clear in practice. It also automatically adds the player's UUID as an
 * observer UUID as per [PartyStore.observerUUIDs]
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class PlayerPartyStore(
    /** The UUID of the player this store is for. */
    val playerUUID: UUID
) : PartyStore(playerUUID) {
    override fun initialize() {
        super.initialize()
        observerUUIDs.add(playerUUID)
    }

    /**
     * Called on the party every second for routine party updates
     * ex: Passive healing, statuses, etc
     */
    fun onPartyTick(player: ServerPlayerEntity) {
        val random = Random.Default
        for(pokemon in this) {
            // Awake from fainted
            if(pokemon.isFainted()) {
                pokemon.faintedTimer--
                if(pokemon.faintedTimer == -1) {
                    pokemon.currentHealth = (pokemon.hp * PokemonCobbled.config.faintAwakenHealthPercent).toInt()
                    // TODO: Message for waking up from fainted
                }
            }
            // Passive healing while less than full health
            else if(pokemon.currentHealth < pokemon.hp) {
                if(random.nextInt(PokemonCobbled.config.healChance) == 0) {
                    pokemon.currentHealth += PokemonCobbled.config.randomHealAmount.random()
                }
            }

            // Statuses
            val status = pokemon.status
            if(status != null) {
                if(status.isExpired()) {
                    status.status.onStatusExpire(player, pokemon, random)
                    pokemon.status = null
                } else {
                    status.status.onStatusTick(player, pokemon, random)
                    status.tickTimer()
                }
            }
        }
    }
}