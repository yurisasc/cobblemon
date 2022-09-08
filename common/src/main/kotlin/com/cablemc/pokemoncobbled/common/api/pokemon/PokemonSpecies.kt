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
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.api.types.adapters.ElementalTypeAdapter
import com.cablemc.pokemoncobbled.common.pokemon.Species
import com.cablemc.pokemoncobbled.common.pokemon.adapters.StatAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledEvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledPreEvolutionAdapter
import com.cablemc.pokemoncobbled.common.pokemon.evolution.adapters.CobbledRequirementAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.AbilityPoolAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.AbilityTemplateAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.BiomeLikeConditionAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.BlockLikeConditionAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.BoxAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.DropEntryAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.IdentifierAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.IntRangeAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.LazySetAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.LearnsetAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.NbtCompoundAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.TimeRangeAdapter
import com.cablemc.pokemoncobbled.common.util.adapters.pokemonPropertiesShortAdapter
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.V8Runtime
import com.google.common.collect.HashBasedTable
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
import net.minecraft.util.math.Box
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
        .registerTypeAdapter(Learnset::class.java, LearnsetAdapter)
        .registerTypeAdapter(Evolution::class.java, CobbledEvolutionAdapter)
        .registerTypeAdapter(Box::class.java, BoxAdapter)
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
    private val speciesByDex = HashBasedTable.create<String, Int, Species>()

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
    val CLEFFA by SpeciesByNameDelegate
    val CLEFABLE by SpeciesByNameDelegate
    val CLEFAIRY by SpeciesByNameDelegate

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
    fun getByPokedexNumber(ndex: Int, namespace: String = PokemonCobbled.MODID) = this.speciesByDex.get(namespace, ndex)

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
        val executable = StringBuilder("""
                const PokemonShowdown = require('pokemon-showdown');

                PokemonShowdown.Dex.modsLoaded = false;

                Object.keys(PokemonShowdown.CobbledPokedex).forEach(key => {
                    delete PokemonShowdown.CobbledPokedex[key];
                });
            """.trimIndent())
        data.forEach { (identifier, species) ->
            species.resourceIdentifier = identifier
            this.speciesByIdentifier.put(identifier, species)?.let { old ->
                this.speciesByDex.remove(old.resourceIdentifier.namespace, old.nationalPokedexNumber)
            }
            this.speciesByDex.put(species.resourceIdentifier.namespace, species.nationalPokedexNumber, species)
            species.initialize()
            this.createShowdownRepresentation(executable, species)
        }
        PokemonCobbled.LOGGER.info("Loaded {} Pokémon species", this.speciesByIdentifier.size)
        this.observable.emit(this)
        V8Host.getNodeInstance().createV8Runtime<V8Runtime>().use { runtime ->
            val executor = runtime.getExecutor(executable.toString())
            executor.resourceName = "./node_modules"
            executor.executeVoid()
        }
    }

    private fun createShowdownRepresentation(executable: StringBuilder, species: Species) {
        // We will use this as the name too as it doesn't really matter for our use case, some properties will also be ignored due to not affecting us
        val baseSpeciesKey = "${species.resourceIdentifier.namespace}${species.resourceIdentifier.path}".lowercase()
        // Convert the gender ratio to the appropriate showdown format
        val genderDetails = when (species.maleRatio) {
            1F -> "gender: \"F\""
            0F -> "gender: \"M\""
            -0.125F -> "gender: \"N\""
            else -> "genderRatio: { M: ${species.maleRatio}, F: ${1F - species.maleRatio} }"
        }
        // ToDo types will need a refresh when we introduce custom typing support
        // ToDo generate forms data too
        // ToDo ability conversion
        // ToDo weight and height on our end as it is necessary for battle mechanics
        // ToDo Egg groups on our end
        // ToDo Ability to dynamax on our end
        // ToDo Signature GMax move on our end
        executable.append("""
            PokemonShowdown.CobbledPokedex["$baseSpeciesKey"] = {
                num: ${species.nationalPokedexNumber},
                name: "$baseSpeciesKey",
                types: ["${species.primaryType.name}"${if (species.secondaryType != null) ", \"${species.secondaryType.name}\"" else ""}],
                $genderDetails,
                baseStats: { hp: ${species.baseStats[Stats.HP]}, atk: ${species.baseStats[Stats.ATTACK]}, def: ${species.baseStats[Stats.DEFENCE]}, spa: ${species.baseStats[Stats.SPECIAL_ATTACK]}, spd: ${species.baseStats[Stats.SPECIAL_DEFENCE]}, spe: ${species.baseStats[Stats.SPEED]} },
                abilities: { 0: "Overgrow", H: "Chlorophyll" },
                heightm: 1,
                weightkg: 1,
                color: "White",
                prevo: "Ivysaur",
                evos: [],
                evoLevel: 32,
                eggGroups: ["Undiscovered"],
                otherFormes: ["Venusaur-Mega"],
                formeOrder: ["Venusaur", "Venusaur-Mega"],
            };
        """.trimIndent())
    }

}