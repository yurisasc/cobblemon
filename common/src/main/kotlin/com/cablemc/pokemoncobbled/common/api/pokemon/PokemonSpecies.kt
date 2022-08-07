package com.cablemc.pokemoncobbled.common.api.pokemon

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityPool
import com.cablemc.pokemoncobbled.common.api.abilities.AbilityTemplate
import com.cablemc.pokemoncobbled.common.api.ai.SleepDepth
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeCondition
import com.cablemc.pokemoncobbled.common.api.data.JsonDataRegistry
import com.cablemc.pokemoncobbled.common.api.drop.DropEntry
import com.cablemc.pokemoncobbled.common.api.drop.ItemDropMethod
import com.cablemc.pokemoncobbled.common.api.entity.EntityDimensionsAdapter
import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate
import com.cablemc.pokemoncobbled.common.api.moves.adapters.MoveTemplateAdapter
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.adapter.ShoulderEffectAdapter
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.PreEvolution
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroup
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroupAdapter
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.adapters.StatAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledEvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledPreEvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledRequirementAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.*
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlin.io.path.Path
import kotlin.reflect.KProperty
import net.minecraft.block.Block
import net.minecraft.entity.EntityDimensions
import net.minecraft.nbt.NbtCompound
import net.minecraft.resource.ResourceType
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

object PokemonSpecies : JsonDataRegistry<Species> {

    override val id = cobbledResource("species")
    override val type = ResourceType.SERVER_DATA

    override val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Stat::class.java, StatAdapter)
        .registerTypeAdapter(ElementalType::class.java, ElementalTypeAdapter)
        .registerTypeAdapter(AbilityTemplate::class.java, AbilityTemplateAdapter)
        .registerTypeAdapter(ShoulderEffect::class.java, ShoulderEffectAdapter)
        .registerTypeAdapter(MoveTemplate::class.java, MoveTemplateAdapter)
        .registerTypeAdapter(ExperienceGroup::class.java, ExperienceGroupAdapter)
        .registerTypeAdapter(EntityDimensions::class.java, EntityDimensionsAdapter)
        .registerTypeAdapter(Evolution::class.java, CobbledEvolutionAdapter)
        .registerTypeAdapter(AbilityPool::class.java, AbilityPoolAdapter)
        .registerTypeAdapter(EvolutionRequirement::class.java, CobbledRequirementAdapter)
        .registerTypeAdapter(PreEvolution::class.java, CobbledPreEvolutionAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(Set::class.java, Evolution::class.java).type, LazySetAdapter(Evolution::class))
        .registerTypeAdapter(IntRange::class.java, IntRangeAdapter)
        .registerTypeAdapter(PokemonProperties::class.java, pokemonPropertiesShortAdapter)
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
        .registerTypeAdapter(TimeRange::class.java, TimeRangeAdapter)
        .registerTypeAdapter(ItemDropMethod::class.java, ItemDropMethod.adapter)
        .registerTypeAdapter(SleepDepth::class.java, SleepDepth.adapter)
        .registerTypeAdapter(DropEntry::class.java, DropEntryAdapter)
        .registerTypeAdapter(NbtCompound::class.java, NbtCompoundAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Biome::class.java).type, BiomeLikeConditionAdapter)
        .registerTypeAdapter(TypeToken.getParameterized(RegistryLikeCondition::class.java, Block::class.java).type, BlockLikeConditionAdapter)
        .disableHtmlEscaping()
        .enableComplexMapKeySerialization()
        .create()

    override val typeToken: TypeToken<Species> = TypeToken.get(Species::class.java)
    override val resourcePath = Path("species")

    override val observable = SimpleObservable<PokemonSpecies>()

    private val speciesByIdentifier = hashMapOf<Identifier, Species>()
    private val speciesByDex = hashMapOf<Int, Species>()

    val species: Collection<Species>
        get() = this.speciesByIdentifier.values

    object SpeciesByNameDelegate {
        operator fun getValue(_species: PokemonSpecies, property: KProperty<*>) = getByIdentifier(cobbledResource(property.name.lowercase()))
    }

    val BULBASAUR by SpeciesByNameDelegate
    val IVYSAUR by SpeciesByNameDelegate
    val VENUSAUR by SpeciesByNameDelegate
    val CHARMANDER by SpeciesByNameDelegate
    val CHARMELEON by SpeciesByNameDelegate
    val CHARIZARD by SpeciesByNameDelegate
    val SQUIRTLE by SpeciesByNameDelegate
    val WARTORTLE by SpeciesByNameDelegate
    val BLASTOISE by SpeciesByNameDelegate
    val CATERPIE by SpeciesByNameDelegate
    val METAPOD by SpeciesByNameDelegate
    val BUTTERFREE by SpeciesByNameDelegate
    val WEEDLE by SpeciesByNameDelegate
    val KAKUNA by SpeciesByNameDelegate
    val BEEDRILL by SpeciesByNameDelegate
    val PIDGEY by SpeciesByNameDelegate
    val PIDGEOTTO by SpeciesByNameDelegate
    val PIDGEOT by SpeciesByNameDelegate
    val EKANS by SpeciesByNameDelegate
    val ZUBAT by SpeciesByNameDelegate
    val DIGLETT by SpeciesByNameDelegate
    val DUGTRIO by SpeciesByNameDelegate
    val MAGIKARP by SpeciesByNameDelegate
    val GYARADOS by SpeciesByNameDelegate
    val EEVEE by SpeciesByNameDelegate
    val RATTATA by SpeciesByNameDelegate
    val RATICATE by SpeciesByNameDelegate

    /**
     * Finds a species by the pathname of their [Identifier].
     * This method exists for the convenience of finding Cobble default Pokémon.
     * This uses [getByIdentifier] using the [PokemonCobbled.MODID] as the namespace and the [name] as the path.
     *
     * @param name The path of the species asset.
     * @return The [Species] if existing.
     */
    fun getByName(name: String) = this.getByIdentifier(cobbledResource(name))

    /**
     * Finds a [Species] by its national Pokédex entry number.
     *
     * @param ndex The [Species.nationalPokedexNumber].
     * @return The [Species] if existing.
     */
    fun getByPokedexNumber(ndex: Int) = this.speciesByDex[ndex]

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
    fun random(): Species = this.species.random()

    override fun reload(data: Map<Identifier, Species>) {
        this.speciesByIdentifier.clear()
        this.speciesByDex.clear()
        data.forEach { (identifier, species) ->
            // ToDo Decide if we wanna skip or replace
            if (this.speciesByDex.containsKey(species.nationalPokedexNumber)) {
                PokemonCobbled.LOGGER.warn("Found duplicate national Pokédex entry for species {}, skipping...", identifier.toString())
                return@forEach
            }
            this.speciesByIdentifier[identifier] = species
            this.speciesByDex[species.nationalPokedexNumber] = species
            species.resourceIdentifier = identifier
            species.forms.forEach { form ->
                form.initialize(species)
            }
        }
        PokemonCobbled.LOGGER.info("Loaded {} Pokémon species", this.speciesByIdentifier.size)
        this.observable.emit(this)
    }
}