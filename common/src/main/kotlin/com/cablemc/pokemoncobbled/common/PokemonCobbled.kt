package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.drop.CommandDropEntry
import com.cablemc.pokemoncobbled.common.api.drop.DropEntry
import com.cablemc.pokemoncobbled.common.api.drop.ItemDropEntry
import com.cablemc.pokemoncobbled.common.api.abilities.Abilities
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.PLAYER_JOIN
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.PLAYER_QUIT
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.SERVER_STARTED
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.SERVER_STOPPING
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.TICK_POST
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.net.serializers.StringSetDataSerializer
import com.cablemc.pokemoncobbled.common.api.net.serializers.Vec3DataSerializer
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.Gen7CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffectRegistry
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.StandardExperienceCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.feature.FlagSpeciesFeature
import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.api.spawning.BestSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.CobbledSpawningProspector
import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.api.storage.adapter.NBTStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.factory.FileBackedPokemonStoreFactory
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerDataStoreManager
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.BattleSide
import com.cablemc.pokemoncobbled.common.battles.ShowdownThread
import com.cablemc.pokemoncobbled.common.battles.actor.PokemonBattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.command.argument.PokemonPropertiesArgumentType
import com.cablemc.pokemoncobbled.common.command.argument.SpawnBucketArgumentType
import com.cablemc.pokemoncobbled.common.config.CobbledConfig
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.events.ServerTickHandler
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.aspects.GENDER_ASPECT
import com.cablemc.pokemoncobbled.common.pokemon.aspects.SHINY_ASPECT
import com.cablemc.pokemoncobbled.common.pokemon.features.SunglassesFeature
import com.cablemc.pokemoncobbled.common.pokemon.properties.UncatchableProperty
import com.cablemc.pokemoncobbled.common.pokemon.properties.UntradeableProperty
import com.cablemc.pokemoncobbled.common.registry.CompletableRegistry
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.ifDedicatedServer
import com.cablemc.pokemoncobbled.common.world.CobbledGameRules
import com.cablemc.pokemoncobbled.common.worldgen.CobbledWorldgen
import com.google.gson.GsonBuilder
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.hooks.item.tool.AxeItemHooks
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import net.minecraft.client.MinecraftClient
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.util.WorldSavePath
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager

object PokemonCobbled {
    const val MODID = "pokemoncobbled"
    const val VERSION = "0.0.1"
    const val CONFIG_PATH = "config/$MODID/main.json"
    val LOGGER = LogManager.getLogger()

    lateinit var implementation: PokemonCobbledModImplementation
    lateinit var showdown: ShowdownConnection
    var captureCalculator: CaptureCalculator = Gen7CaptureCalculator
    var experienceCalculator: ExperienceCalculator = StandardExperienceCalculator
    var isDedicatedServer = false
    var showdownThread = ShowdownThread()
    var config = CobbledConfig()
    var prospector: SpawningProspector = CobbledSpawningProspector
    var areaContextResolver: AreaContextResolver = object : AreaContextResolver {}
    val bestSpawner = BestSpawner
    var storage = PokemonStoreManager()
    var playerData = PlayerDataStoreManager()

    fun preinitialize(implementation: PokemonCobbledModImplementation) {
        DropEntry.register("command", CommandDropEntry::class.java)
        DropEntry.register("item", ItemDropEntry::class.java, isDefault = true)

        this.loadConfig()
        this.implementation = implementation

        CobbledEntities.register()
        CobbledBlocks.register()
        CobbledBlockEntities.register()
        CobbledItems.register()
        CobbledSounds.register()
        CobbledNetwork.register()
        CobbledFeatures.register()
        CobbledGameRules.register()

        ShoulderEffectRegistry.register()
        PLAYER_JOIN.subscribe { storage.onPlayerLogin(it) }
        PLAYER_QUIT.subscribe { PCLinkManager.removeLink(it.uuid) }
        TrackedDataHandlerRegistry.register(Vec3DataSerializer)
        TrackedDataHandlerRegistry.register(StringSetDataSerializer)
        //Command Arguments
        ArgumentTypes.register("pokemoncobbled:pokemon", PokemonArgumentType::class.java, ConstantArgumentSerializer(PokemonArgumentType::pokemon))
        ArgumentTypes.register("pokemoncobbled:pokemonproperties", PokemonPropertiesArgumentType::class.java, ConstantArgumentSerializer(PokemonPropertiesArgumentType::properties))
        ArgumentTypes.register("pokemoncobbled:spawnbucket", SpawnBucketArgumentType::class.java, ConstantArgumentSerializer(SpawnBucketArgumentType::spawnBucket))
    }

    fun initialize() {
        showdownThread.start()

        CompletableRegistry.allRegistriesCompleted.thenAccept {
            LOGGER.info("All registries loaded.")
        }

        ExperienceGroups.registerDefaults()
        Abilities.loadPotentialAbilityInterpreters()

        CobbledWorldgen.register()

        Moves.load()
        LOGGER.info("Loaded ${Moves.count()} Moves.")

        // Touching this object loads them and the stats. Probably better to use lateinit and a dedicated .register for this and stats
        LOGGER.info("Loaded ${PokemonSpecies.count()} Pokémon species.")

        SHINY_ASPECT.register()
        GENDER_ASPECT.register()
        FlagSpeciesFeature.registerWithPropertyAndAspect("sunglasses", SunglassesFeature::class.java)
        CustomPokemonProperty.register(UntradeableProperty)
        CustomPokemonProperty.register(UncatchableProperty)

        CommandRegistrationEvent.EVENT.register(CobbledCommands::register)

        ifDedicatedServer {
            isDedicatedServer = true
            TICK_POST.subscribe { ScheduledTaskTracker.update() }
        }

        CobbledBlocks.completed.thenAccept {
            AxeItemHooks.addStrippable(CobbledBlocks.APRICORN_LOG.get(), CobbledBlocks.STRIPPED_APRICORN_LOG.get())
            AxeItemHooks.addStrippable(CobbledBlocks.APRICORN_WOOD.get(), CobbledBlocks.STRIPPED_APRICORN_WOOD.get())
        }

        SERVER_STARTED.subscribe {
            val pokemonStoreRoot = it.getSavePath(WorldSavePath.PLAYERDATA).parent.resolve("pokemon").toFile()
            storage.registerFactory(
                priority = Priority.LOWEST,
                factory = FileBackedPokemonStoreFactory(
                    adapter = NBTStoreAdapter(pokemonStoreRoot.absolutePath, useNestedFolders = true, folderPerClass = true),
                    createIfMissing = true,
                    pcConstructor = { uuid -> PCStore(uuid).also { it.resize(config.defaultBoxCount) } }
                )
            )
        }

        SERVER_STOPPING.subscribe {
            storage.unregisterAll()
            playerData.saveAll()
        }
        SERVER_STARTED.subscribe { bestSpawner.onServerStarted() }
        TICK_POST.subscribe { ServerTickHandler.onTick(it) }

        showdownThread.showdownStarted.thenAccept {
            LOGGER.info("Starting dummy battle to pre-load data.")
            BattleRegistry.startBattle(
                BattleFormat.GEN_8_SINGLES,
                BattleSide(PokemonBattleActor(UUID.randomUUID(), BattlePokemon(Pokemon().initialize()))),
                BattleSide(PokemonBattleActor(UUID.randomUUID(), BattlePokemon(Pokemon().initialize())))
            ).apply { mute = true }
        }
    }

    fun getLevel(dimension: RegistryKey<World>): World? {
        return if (isDedicatedServer) {
            getServer()?.getWorld(dimension)
        } else {
            val mc = MinecraftClient.getInstance()
            return mc.server?.getWorld(dimension) ?: mc.world
        }
    }

    fun loadConfig() {
        val configFile = File(CONFIG_PATH)
        configFile.parentFile.mkdirs()

        // Check config existence and load if it exists, otherwise create default.
        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                this.config = CobbledConfig.GSON.fromJson(fileReader, CobbledConfig::class.java)
                fileReader.close()
            } catch (exception: Exception) {
                LOGGER.error("Failed to load the config! Using default config until the following has been addressed:")
                this.config = CobbledConfig()
                exception.printStackTrace()
            }

            this.config::class.memberProperties.forEach {
                // Member must have annotations and must be mutable
                if (it.annotations.isEmpty() || it !is KMutableProperty<*>) return@forEach

                var value = it.getter.call(config)
                for (annotation in it.annotations) {
                    when (annotation) {
                        is IntConstraint -> {
                            if (value !is Int) break
                            value = value.coerceIn(annotation.min, annotation.max)
                            it.setter.call(config, value)
                        }
                    }
                }
            }
        } else {
            this.config = CobbledConfig()
            this.saveConfig()
        }

        bestSpawner.loadConfig()
    }

    fun saveConfig() {
        try {
            val configFile = File(CONFIG_PATH)
            val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
            val fileWriter = FileWriter(configFile)

            // Put the config to json then flush the writer to commence writing.
            CobbledConfig.GSON.toJson(this.config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }
}