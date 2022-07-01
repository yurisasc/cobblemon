package com.cablemc.pokemoncobbled.common.api.events.pokemon.interaction

import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.item.interactive.CandyItem
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity

/**
 * The base of the [CandyItem] related events.
 * For the generic experience gain event see ToDo(Link Experience event here)
 *
 * @author Licious
 * @since May 5th, 2022
 */
interface ExperienceCandyUseEvent {

    /**
     * The [ServerPlayerEntity] that fired the interaction.
     */
    val player: ServerPlayerEntity

    /**
     * The [Pokemon] being targeted.
     */
    val pokemon: Pokemon

    /**
     * item The [CandyItem] variant being used.
     */
    val item: CandyItem

    /**
     * Fired when a player attempts to use an experience candy on a Pokémon.
     * Canceling this event will prevent the consumption of the item and the experience yield.
     * For the event that is fired after all the calculations took place see [ExperienceCandyUseEvent.Post].
     * For the generic experience gain event see ToDo(Link Experience event here)
     *
     * @property baseExperienceYield The default amount of experience the [pokemon] would earn.
     * @property experienceYield The current amount of experience the [pokemon] will earn.
     */
    class Pre(
        override val player: ServerPlayerEntity,
        override val pokemon: Pokemon,
        override val item: CandyItem,
        val baseExperienceYield: Int,
        var experienceYield: Int
    ) : ExperienceCandyUseEvent, Cancelable()

    /**
     * Fired after a player used an experience candy on a Pokémon.
     * This event is posted before the experience is given and the item is consumed.
     * For the event that is fired before all the calculations took place see [ExperienceCandyUseEvent.Pre].
     *
     * @property experienceYield The amount of experience the [pokemon] earned.
     */
    class Post(
        override val player: ServerPlayerEntity,
        override val pokemon: Pokemon,
        override val item: CandyItem,
        val experienceYield: Int
    ) : ExperienceCandyUseEvent

}