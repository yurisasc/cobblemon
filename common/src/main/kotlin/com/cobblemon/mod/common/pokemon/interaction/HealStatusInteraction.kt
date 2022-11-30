package com.cobblemon.mod.common.pokemon.interaction

import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.api.pokemon.status.Status
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

/**
 * An interaction that will heal the Pokémon of a specific status.
 * This interaction consumes the [ItemStack] behind the trigger.
 *
 * @property status A collection of valid [Status] to heal.
 */
class HealStatusInteraction(
    private val status: Collection<Status>
) : PokemonEntityInteraction {

    constructor() : this(emptyList())

    override val accepted: Set<PokemonEntityInteraction.Ownership> = EnumSet.of(PokemonEntityInteraction.Ownership.OWNER)

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        val status = entity.pokemon.status?.status ?: return false
        if (status in this.status) {
            entity.pokemon.status = null
            this.consumeItem(player, stack)
            return true
        }
        return false
    }

    companion object {

        val TYPE_ID = cobblemonResource("heal_status")

    }

}