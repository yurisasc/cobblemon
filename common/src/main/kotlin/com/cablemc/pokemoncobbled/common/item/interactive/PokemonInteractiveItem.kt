package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack

/**
 * An [InteractiveItem] targeting [PokemonEntity]s.
 *
 * @param accepted The [Ownership] variants for this interaction to fire [PokemonInteractiveItem.processInteraction].
 */
abstract class PokemonInteractiveItem(properties: Properties, vararg accepted: Ownership) : InteractiveItem<PokemonEntity>(properties) {

    private val accepted = accepted.toSet()

    final override fun onInteraction(player: ServerPlayer, entity: PokemonEntity, stack: ItemStack) {
        val pokemon = entity.pokemon
        val storeCoordinates = pokemon.storeCoordinates.get()
        val ownership = when {
            storeCoordinates == null -> Ownership.WILD
            storeCoordinates.store.uuid == player.uuid -> Ownership.OWNER
            else -> Ownership.OWNED_ANOTHER
        }
        if (this.accepted.contains(ownership))
            this.processInteraction(player, entity, stack)
    }

    /**
     * Fired after [InteractiveItem.onInteraction] the [Ownership] is checked if contained in [accepted].
     *
     * @param player The [ServerPlayer] interacting with the [entity].
     * @param entity The [PokemonEntity] being interacted with.
     * @param stack The [ItemStack] used in this interaction. [ItemStack.getItem] will always be of the same type as this [InteractiveItem].
     */
    abstract fun processInteraction(player: ServerPlayer, entity: PokemonEntity, stack: ItemStack)

    /**
     * Represents the ownership status of a Pokemon relative to a Player.
     *
     * @author Licious
     * @since March 24th, 2022
     */
    enum class Ownership {

        /**
         * When the player owns the Pokemon.
         */
        OWNER,

        /**
         * When the Pokemon is owned by another entity.
         */
        OWNED_ANOTHER,

        /**
         * When the Pokemon has no owner.
         */
        WILD

    }

}