/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.advancement.PokemodCriteria
import com.cablemc.pokemod.common.api.Priority
import com.cablemc.pokemod.common.api.data.DataProvider
import com.cablemc.pokemod.common.api.drop.CommandDropEntry
import com.cablemc.pokemod.common.api.drop.DropEntry
import com.cablemc.pokemod.common.api.drop.ItemDropEntry
import com.cablemc.pokemod.common.api.events.PokemodEvents
import com.cablemc.pokemod.common.api.events.PokemodEvents.EGG_HATCH
import com.cablemc.pokemod.common.api.events.PokemodEvents.EVOLUTION_COMPLETE
import com.cablemc.pokemod.common.api.events.PokemodEvents.PLAYER_JOIN
import com.cablemc.pokemod.common.api.events.PokemodEvents.PLAYER_QUIT
import com.cablemc.pokemod.common.api.events.PokemodEvents.POKEMON_CAPTURED
import com.cablemc.pokemod.common.api.events.PokemodEvents.SERVER_STARTED
import com.cablemc.pokemod.common.api.events.PokemodEvents.SERVER_STOPPING
import com.cablemc.pokemod.common.api.events.PokemodEvents.TICK_POST
import com.cablemc.pokemod.common.api.net.serializers.PoseTypeDataSerializer
import com.cablemc.pokemod.common.api.net.serializers.StringSetDataSerializer
import com.cablemc.pokemod.common.api.net.serializers.Vec3DataSerializer
import com.cablemc.pokemod.common.api.permission.PermissionValidator
import com.cablemc.pokemod.common.api.pokeball.catching.calculators.CaptureCalculator
import com.cablemc.pokemod.common.api.pokeball.catching.calculators.CobbledGen348CaptureCalculator
import com.cablemc.pokemod.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemod.common.api.pokemon.effect.ShoulderEffectRegistry
import com.cablemc.pokemod.common.api.pokemon.experience.ExperienceCalculator
import com.cablemc.pokemod.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemod.common.api.pokemon.experience.StandardExperienceCalculator
import com.cablemc.pokemod.common.api.pokemon.feature.EnumSpeciesFeature
import com.cablemc.pokemod.common.api.pokemon.feature.FlagSpeciesFeature
import com.cablemc.pokemod.common.api.pokemon.feature.SpeciesFeature
import com.cablemc.pokemod.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemod.common.api.reactive.Observable.Companion.takeFirst
import com.cablemc.pokemod.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemod.common.api.spawning.BestSpawner
import com.cablemc.pokemod.common.api.spawning.CobbledSpawningProspector
import com.cablemc.pokemod.common.api.spawning.PokemodSpawnPools
import com.cablemc.pokemod.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemod.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemod.common.api.starter.StarterHandler
import com.cablemc.pokemod.common.api.storage.PokemonStoreManager
import com.cablemc.pokemod.common.api.storage.adapter.conversions.ReforgedConversion
import com.cablemc.pokemod.common.api.storage.adapter.flatifle.FileStoreAdapter
import com.cablemc.pokemod.common.api.storage.adapter.flatifle.JSONStoreAdapter
import com.cablemc.pokemod.common.api.storage.adapter.flatifle.NBTStoreAdapter
import com.cablemc.pokemod.common.api.storage.factory.FileBackedPokemonStoreFactory
import com.cablemc.pokemod.common.api.storage.pc.PCStore
import com.cablemc.pokemod.common.api.storage.pc.link.PCLinkManager
import com.cablemc.pokemod.common.api.storage.player.PlayerDataStoreManager
import com.cablemc.pokemod.common.battles.BattleFormat
import com.cablemc.pokemod.common.battles.BattleRegistry
import com.cablemc.pokemod.common.battles.BattleSide
import com.cablemc.pokemod.common.battles.ShowdownThread
import com.cablemc.pokemod.common.battles.actor.PokemonBattleActor
import com.cablemc.pokemod.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemod.common.battles.runner.ShowdownConnection
import com.cablemc.pokemod.common.config.PokemodConfig
import com.cablemc.pokemod.common.config.constraint.IntConstraint
import com.cablemc.pokemod.common.config.starter.StarterConfig
import com.cablemc.pokemod.common.data.CobbledDataProvider
import com.cablemc.pokemod.common.events.AdvancementHandler
import com.cablemc.pokemod.common.events.ServerTickHandler
import com.cablemc.pokemod.common.item.PokeBallItem
import com.cablemc.pokemod.common.net.messages.client.settings.ServerSettingsPacket
import com.cablemc.pokemod.common.permission.LaxPermissionValidator
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.pokemon.aspects.GENDER_ASPECT
import com.cablemc.pokemod.common.pokemon.aspects.SHINY_ASPECT
import com.cablemc.pokemod.common.pokemon.aspects.SnakePatternAspect
import com.cablemc.pokemod.common.pokemon.feature.BattleCriticalHitsFeature
import com.cablemc.pokemod.common.pokemon.feature.DamageTakenFeature
import com.cablemc.pokemod.common.pokemon.feature.SNAKE_PATTERN
import com.cablemc.pokemod.common.pokemon.feature.SnakePatternFeature
import com.cablemc.pokemod.common.pokemon.properties.UncatchableProperty
import com.cablemc.pokemod.common.pokemon.properties.UntradeableProperty
import com.cablemc.pokemod.common.pokemon.properties.tags.PokemonFlagProperty
import com.cablemc.pokemod.common.registry.CompletableRegistry
import com.cablemc.pokemod.common.starter.CobbledStarterHandler
import com.cablemc.pokemod.common.util.getServer
import com.cablemc.pokemod.common.util.ifDedicatedServer
import com.cablemc.pokemod.common.util.pokemodResource
import com.cablemc.pokemod.common.util.removeAmountIf
import com.cablemc.pokemod.common.world.PokemodGameRules
import com.cablemc.pokemod.common.world.generation.PokemodWorldGeneration
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.hooks.item.tool.AxeItemHooks
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.util.WorldSavePath
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

object Pokemod {
    const val MODID = "pokemod"
    const val VERSION = "1.0.0"
    const val CONFIG_PATH = "config/$MODID/main.json"
    val LOGGER = LogManager.getLogger()

    lateinit var implementation: PokemodImplementation
    lateinit var showdown: ShowdownConnection
    var captureCalculator: CaptureCalculator = CobbledGen348CaptureCalculator
    var experienceCalculator: ExperienceCalculator = StandardExperienceCalculator
    var starterHandler: StarterHandler = CobbledStarterHandler()
    var isDedicatedServer = false
    var showdownThread = ShowdownThread()
    lateinit var config: PokemodConfig
    var prospector: SpawningProspector = CobbledSpawningProspector
    var areaContextResolver: AreaContextResolver = object : AreaContextResolver {}
    val bestSpawner = BestSpawner
    var storage = PokemonStoreManager()
    lateinit var playerData: PlayerDataStoreManager
    lateinit var starterConfig: StarterConfig
    val dataProvider: DataProvider = CobbledDataProvider
    var permissionValidator: PermissionValidator by Delegates.observable(LaxPermissionValidator().also { it.initialize() }) { _, _, newValue -> newValue.initialize() }

    fun preinitialize(implementation: PokemodImplementation) {
        DropEntry.register("command", CommandDropEntry::class.java)
        DropEntry.register("item", ItemDropEntry::class.java, isDefault = true)

        ExperienceGroups.registerDefaults()
        PokemonSpecies.observable.subscribe { PokemodSpawnPools.load() }

        this.loadConfig()
        this.implementation = implementation

        PokemodCriteria // Init the fields and register the criteria
        PokemodEntities.register()
        PokemodBlocks.register()
        PokemodBlockEntities.register()
        PokemodItems.register()
        PokemodSounds.register()
        PokemodNetwork.register()
        PokemodFeatures.register()
        PokemodGameRules.register()

        ShoulderEffectRegistry.register()
        PLAYER_JOIN.subscribe {
            storage.onPlayerLogin(it)
            playerData.get(it).sendToPlayer(it)
            starterHandler.handleJoin(it)
            ServerSettingsPacket().sendToPlayer(it)
        }
        PLAYER_QUIT.subscribe { PCLinkManager.removeLink(it.uuid) }
        TrackedDataHandlerRegistry.register(Vec3DataSerializer)
        TrackedDataHandlerRegistry.register(StringSetDataSerializer)
        TrackedDataHandlerRegistry.register(PoseTypeDataSerializer)
    }

    fun initialize() {
        showdownThread.start()

        CompletableRegistry.allRegistriesCompleted.thenAccept {
            LOGGER.info("All registries loaded.")
        }

        PokemodWorldGeneration.register()

        // Start up the data provider.
        CobbledDataProvider.registerDefaults()

        SHINY_ASPECT.register()
        GENDER_ASPECT.register()
        SnakePatternAspect.register()

        config.flagSpeciesFeatures.forEach(FlagSpeciesFeature::registerWithPropertyAndAspect)
        config.globalFlagSpeciesFeatures.forEach(FlagSpeciesFeature::registerWithPropertyAndAspect)
        SpeciesFeature.registerGlobalFeature(DamageTakenFeature.ID) { DamageTakenFeature() }
        SpeciesFeature.registerGlobalFeature(BattleCriticalHitsFeature.ID) { BattleCriticalHitsFeature() }
        EnumSpeciesFeature.registerWithProperty(SNAKE_PATTERN, SnakePatternFeature::class.java)

        CustomPokemonProperty.register(UntradeableProperty)
        CustomPokemonProperty.register(UncatchableProperty)
        CustomPokemonProperty.register(PokemonFlagProperty)

        CommandRegistrationEvent.EVENT.register(PokemodCommands::register)

        ifDedicatedServer {
            isDedicatedServer = true
            TICK_POST.subscribe { ScheduledTaskTracker.update() }
        }

        PokemodBlocks.completed.thenAccept {
            AxeItemHooks.addStrippable(PokemodBlocks.APRICORN_LOG.get(), PokemodBlocks.STRIPPED_APRICORN_LOG.get())
            AxeItemHooks.addStrippable(PokemodBlocks.APRICORN_WOOD.get(), PokemodBlocks.STRIPPED_APRICORN_WOOD.get())
        }

        SERVER_STARTED.subscribe { server ->
            playerData = PlayerDataStoreManager().also { it.setup(server) }
            val pokemonStoreRoot = server.getSavePath(WorldSavePath.ROOT).resolve("pokemon").toFile()

            storage.registerFactory(
                priority = Priority.LOWEST,
                factory = FileBackedPokemonStoreFactory(
                    adapter = if (config.storageFormat == "nbt") {
                        NBTStoreAdapter(pokemonStoreRoot.absolutePath, useNestedFolders = true, folderPerClass = true)
                    } else {
                        JSONStoreAdapter(pokemonStoreRoot.absolutePath, useNestedFolders = true, folderPerClass = true)
                    }.with(ReforgedConversion(server.getSavePath(WorldSavePath.ROOT))) as FileStoreAdapter<*>,
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
        POKEMON_CAPTURED.subscribe { AdvancementHandler.onCapture(it) }
        EGG_HATCH.subscribe { AdvancementHandler.onHatch(it) }
        EVOLUTION_COMPLETE.subscribe { AdvancementHandler.onEvolve(it) }
        PokemodEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST) { event ->
            val pokemon = event.pokemon
            val ninjaskIdentifier = pokemodResource("ninjask")
            // Ensure the config option is enabled and that the result was a ninjask and that shedinja exists
            if (this.config.ninjaskCreatesShedinja && pokemon.species.resourceIdentifier == ninjaskIdentifier && PokemonSpecies.getByIdentifier(Pokemon.SHEDINJA) != null) {
                val player = pokemon.getOwnerPlayer() ?: return@subscribe
                if (player.inventory.containsAny { it.item is PokeBallItem }) {
                    player.inventory.removeAmountIf(1) { it.item is PokeBallItem }
                    val properties = event.evolution.result.copy()
                    properties.species = Pokemon.SHEDINJA.toString()
                    val product = pokemon.clone()
                    properties.apply(product)
                    pokemon.storeCoordinates.get()?.store?.add(product)
                }
            }
        }

        showdownThread.showdownStarted.thenAccept {
            PokemonSpecies.observable.pipe(takeFirst()).subscribe {
                LOGGER.info("Starting dummy Showdown battle to force it to pre-load data.")
                BattleRegistry.startBattle(
                    BattleFormat.GEN_8_SINGLES,
                    BattleSide(PokemonBattleActor(UUID.randomUUID(), BattlePokemon(Pokemon().initialize()), -1F)),
                    BattleSide(PokemonBattleActor(UUID.randomUUID(), BattlePokemon(Pokemon().initialize()), -1F))
                ).apply { mute = true }
            }
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
                this.config = PokemodConfig.GSON.fromJson(fileReader, PokemodConfig::class.java)
                fileReader.close()
            } catch (exception: Exception) {
                LOGGER.error("Failed to load the config! Using default config until the following has been addressed:")
                this.config = PokemodConfig()
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
            this.config = PokemodConfig()
            this.saveConfig()
        }

        bestSpawner.loadConfig()
        PokemonSpecies.observable.subscribe { starterConfig = this.loadStarterConfig() }
    }

    fun loadStarterConfig(): StarterConfig {
        val file = File("config/pokemod/starters.json")
        file.parentFile.mkdirs()
        if (!file.exists()) {
            val config = StarterConfig()
            val pw = PrintWriter(file)
            StarterConfig.GSON.toJson(config, pw)
            pw.close()
            return config
        }
        val reader = FileReader(file)
        val config = StarterConfig.GSON.fromJson(reader, StarterConfig::class.java)
        reader.close()
        return config
    }

    fun saveConfig() {
        try {
            val configFile = File(CONFIG_PATH)
            val fileWriter = FileWriter(configFile)
            // Put the config to json then flush the writer to commence writing.
            PokemodConfig.GSON.toJson(this.config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }
}