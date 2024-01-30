/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.abilities.AbilityPool
import com.cobblemon.mod.common.api.abilities.AbilityTemplate
import com.cobblemon.mod.common.api.ai.SleepDepth
import com.cobblemon.mod.common.api.conditional.RegistryLikeCondition
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.drop.DropEntry
import com.cobblemon.mod.common.api.drop.ItemDropMethod
import com.cobblemon.mod.common.api.entity.EntityDimensionsAdapter
import com.cobblemon.mod.common.api.moves.MoveTemplate
import com.cobblemon.mod.common.api.moves.adapters.MoveTemplateAdapter
import com.cobblemon.mod.common.api.pokemon.effect.ShoulderEffect
import com.cobblemon.mod.common.api.pokemon.effect.adapter.ShoulderEffectAdapter
import com.cobblemon.mod.common.api.pokemon.egg.EggGroup
import com.cobblemon.mod.common.pokemon.transformation.evolution.Evolution
import com.cobblemon.mod.common.api.pokemon.transformation.evolution.PreEvolution
import com.cobblemon.mod.common.api.pokemon.transformation.requirement.TransformationRequirement
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceGroup
import com.cobblemon.mod.common.api.pokemon.experience.ExperienceGroupAdapter
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels
import com.cobblemon.mod.common.api.pokemon.moves.Learnset
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.transformation.trigger.TransformationTrigger
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.adapters.ElementalTypeAdapter
import com.cobblemon.mod.common.net.messages.client.data.SpeciesRegistrySyncPacket
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.SpeciesAdditions
import com.cobblemon.mod.common.pokemon.transformation.predicate.NbtItemPredicate
import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager
import com.cobblemon.mod.common.pokemon.transformation.adapters.*
import com.cobblemon.mod.common.pokemon.transformation.form.PermanentForm
import com.cobblemon.mod.common.pokemon.transformation.form.StandardForm
import com.cobblemon.mod.common.pokemon.transformation.form.TemporaryForm
import com.cobblemon.mod.common.util.adapters.*
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.common.collect.HashBasedTable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.block.Block
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.structure.Structure

object PokemonSpecies : JsonDataRegistry<Species> {

    override val id = cobblemonResource("species")
    override val type = ResourceType.SERVER_DATA

    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Stat::class.java, Cobblemon.statProvider.typeAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .registerTypeAdapter(AbilityTemplate::class.java, AbilityTemplateAdapter)
        .registerTypeAdapter(ShoulderEffect::class.java, ShoulderEffectAdapter)
        .registerTypeAdapter(MoveTemplate::class.java, MoveTemplateAdapter)
        .registerTypeAdapter(ExperienceGroup::class.java, ExperienceGroupAdapter)
        .registerTypeAdapter(EntityDimensions::class.java, EntityDimensionsAdapter)
        .registerTypeAdapter(Learnset::class.java, LearnsetAdapter)
        .registerTypeAdapter(Box::class.java, BoxAdapter)
        .registerTypeAdapter(AbilityPool::class.java, AbilityPoolAdapter)
        .registerTypeAdapter(PreEvolution::class.java, CobblemonPreEvolutionAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(Set::class.java, Evolution::class.java).type, LazySetAdapter(Evolution::class))
        .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
        .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(TimeRange::class.java, IntRangesAdapter(TimeRange.timeRanges) { TimeRange(*it) })
        .registerTypeAdapter(ItemDropMethod::class.java, ItemDropMethod.adapter)
        .registerTypeAdapter(SleepDepth::class.java, SleepDepth.adapter)
        .registerTypeAdapter(DropEntry::class.java, DropEntryAdapter)
        .registerTypeAdapter(NbtCompound::class.java, NbtCompoundAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Biome::class.java).type, BiomeLikeConditionAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Block::class.java).type, BlockLikeConditionAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Item::class.java).type, ItemLikeConditionAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Structure::class.java).type, StructureLikeConditionAdapter)
        .registerTypeAdapter(EggGroup::class.java, EggGroupAdapter)
        .registerTypeAdapter(StatusEffect::class.java, RegistryElementAdapter<StatusEffect> { Registries.STATUS_EFFECT })
        .registerTypeAdapter(NbtItemPredicate::class.java, NbtItemPredicateAdapter)
        .registerTypeAdapter(TransformationTrigger::class.java, CobblemonTriggerAdapter)
        .registerTypeAdapter(TransformationRequirement::class.java, CobblemonRequirementAdapter)
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .create()

    override val typeToken: TypeToken<Species> = TypeToken.get(Species::class.java)
    override val resourcePath = "species"

    override val observable = SimpleObservable<PokemonSpecies>()

    private val speciesByIdentifier = hashMapOf<Identifier, Species>()
    private val speciesByDex = HashBasedTable.create<String, Int, Species>()

    val species: Collection<Species>
        get() = this.speciesByIdentifier.values
    val implemented = mutableListOf<Species>()

    init {
        SpeciesAdditions.observable.subscribe {
            this.species.forEach(Species::initialize)
            this.species.forEach(Species::resolveEvolutionMoves)
            Cobblemon.showdownThread.queue {
                it.registerSpecies()
                it.indicateSpeciesInitialized()
                // Reload this with the mod
                CobblemonHeldItemManager.load()
                Cobblemon.LOGGER.info("Loaded {} Pokémon species", this.speciesByIdentifier.size)
                this.observable.emit(this)
            }
        }
    }

    /**
     * Finds a species by the pathname of their [Identifier].
     * This method exists for the convenience of finding Cobble default Pokémon.
     * This uses [getByIdentifier] using the [Cobblemon.MODID] as the namespace and the [name] as the path.
     *
     * @param name The path of the species asset.
     * @return The [Species] if existing.
     */
    fun getByName(name: String) = this.getByIdentifier(cobblemonResource(name))

    /**
     * Finds a [Species] by its national Pokédex entry number.
     *
     * @param ndex The [Species.nationalPokedexNumber].
     * @return The [Species] if existing.
     */
    fun getByPokedexNumber(ndex: Int, namespace: String = Cobblemon.MODID) = this.speciesByDex.get(namespace, ndex)

    /**
     * Finds a [Species] by its unique [Identifier].
     *
     * @param identifier The unique [Species.resourceIdentifier] of the [Species].
     * @return The [Species] if existing.
     */
    fun getByIdentifier(identifier: Identifier) = this.speciesByIdentifier[identifier]

    /**
     * Counts the currently loaded species.
     *
     * @return The loaded species amount.
     */
    fun count() = this.speciesByIdentifier.size

    /**
     * Picks a random [Species].
     *
     * @throws [NoSuchElementException] if there are no Pokémon species loaded.
     *
     * @return A randomly selected [Species].
     */
    fun random(): Species = this.implemented.random()

    override fun reload(data: Map<Identifier, Species>) {
        this.speciesByIdentifier.clear()
        this.implemented.clear()
        this.speciesByDex.clear()
        data.forEach { (identifier, species) ->
            species.resourceIdentifier = identifier
            this.speciesByIdentifier.put(identifier, species)?.let { old ->
                this.speciesByDex.remove(old.resourceIdentifier.namespace, old.nationalPokedexNumber)
            }
            this.speciesByDex.put(species.resourceIdentifier.namespace, species.nationalPokedexNumber, species)
            if (species.implemented) {
                this.implemented.add(species)
            }
        }
    }

    override fun sync(player: ServerPlayerEntity) {
        SpeciesRegistrySyncPacket(species.toList()).sendToPlayer(player)
    }

    /**
     * The representation of [FormData] in the context of Showdown.
     * This is intended as a sort of DTO that can be easily converted between JSON and Java/JS objects.
     *
     * @param form The [FormData] being converted into a species (Showdown considers them species).
     */
    @Suppress("unused")
    internal open class ShowdownSpecies(form: FormData) {
        val num = form.species.nationalPokedexNumber
        val name = form.showdownName()
        val types = form.types.map { it.name.replaceFirstChar(Char::uppercase) }
        val eggGroups = form.eggGroups.map { it.showdownID }
        val gender: String? = when (form.maleRatio) {
            0F -> "F"
            1F -> "M"
            -1F, 1.125F -> "N"
            else -> null
        }
        val genderRatio = if (this.gender == null)
            mapOf(
                "maleRatio" to (form.maleRatio),
                "femaleRation" to (1F - (form.maleRatio))
            ) else null
        val baseStats = Stats.PERMANENT.associateBy({ it.showdownId }, { form.baseStats[it] ?: 1 })
        val heightm = form.height / 10
        val weightkg = form.weight / 10
        // This is ugly, but we already have it hardcoded in the mod anyway
        val maxHP = if (this.num == 292) 1 else null
        val cannotDynamax = form.dynamaxBlocked
    }

    /**
     * The representation of a [PermanentForm] in the context of Showdown.
     *
     * @param form The [PermanentForm] being converted into a species (Showdown considers them species).
     */
    internal open class ShowdownPermanentForm(form: PermanentForm) : ShowdownSpecies(form) {
        val forme = if (form !is StandardForm) form.name else null
        val baseSpecies = if (form !is StandardForm) form.species.standardForm.showdownName() else null
        val preevo = form.preEvolution?.form?.showdownName()
        // For the context of battles the content here doesn't matter whatsoever and due to how PokemonProperties work we can't guarantee an actual specific species is defined.
        val evos = if (form.evolutions.isEmpty()) emptyList() else arrayListOf("")
        val nfe = this.evos.isNotEmpty()
        val canGigantamax = form.temporaryForms.firstOrNull { it.gigantamaxMove != null }?.gigantamaxMove
    }

    /**
     * The representation of a [StandardForm] in the context of Showdown.
     *
     * @param form The [StandardForm] being converted into a species.
     */
    internal class ShowdownStandardForm(form: StandardForm) : ShowdownPermanentForm(form) {
        val otherFormes = form.flattenForms().map { it.showdownName() }
        val formeOrder = if (this.otherFormes.isNotEmpty()) arrayListOf(this.name, *this.otherFormes.toTypedArray()) else null
    }

    /**
     * The representation of a [TemporaryForm] in the context of Showdown.
     *
     * @param form The [TemporaryForm] being converted into a species (Showdown considers them species).
     * @param baseForm The [PermanentForm] that transitions into this [TemporaryForm].
     */
    internal class ShowdownTemporaryForm(form: TemporaryForm) : ShowdownSpecies(form) {
        val forme = form.name
        val baseSpecies = form.species.standardForm.showdownName()
        // forms this form can change from during a battle
        val battleOnly = if (form.battleOnly) form.showdownParentForm() else null
        // forms this form can change from outside of battle (mutually exclusive with above)
        val changesFrom = if (!form.battleOnly) form.showdownParentForm() else null
    }

    /** Accommodate for Showdown bullshit. */
    private fun TemporaryForm.showdownParentForm(): Any? {
        // megas and primal don't use battleOnly because... fuck you?
        if (this.battleOnly && this.labels.any { it == CobblemonPokemonLabels.MEGA || it == CobblemonPokemonLabels.PRIMAL })
            return null
        // Necrozma-Ultra and Zygarde-Complete are the only forms where parent form != the forms it can change from
        else if (this.parentOverrides.isNotEmpty())
            return this.species.standardForm.flattenForms().filter { parentOverrides.contains(it.showdownId()) }.map { it.showdownName() }
        else
            return parentForm?.showdownName()
    }
}