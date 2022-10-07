/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.data.DataProvider
import com.cablemc.pokemoncobbled.common.api.drop.CommandDropEntry
import com.cablemc.pokemoncobbled.common.api.drop.DropEntry
import com.cablemc.pokemoncobbled.common.api.drop.ItemDropEntry
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.PLAYER_JOIN
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.PLAYER_QUIT
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.SERVER_STARTED
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.SERVER_STOPPING
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.TICK_POST
import com.cablemc.pokemoncobbled.common.api.net.serializers.PoseTypeDataSerializer
import com.cablemc.pokemoncobbled.common.api.net.serializers.StringSetDataSerializer
import com.cablemc.pokemoncobbled.common.api.net.serializers.Vec3DataSerializer
import com.cablemc.pokemoncobbled.common.api.permission.PermissionValidator
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.CobbledGen348CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffectRegistry
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.ExperienceGroups
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.StandardExperienceCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.feature.FlagSpeciesFeature
import com.cablemc.pokemoncobbled.common.api.pokemon.feature.SpeciesFeature
import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.takeFirst
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.api.spawning.BestSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.CobbledSpawnPools
import com.cablemc.pokemoncobbled.common.api.spawning.CobbledSpawningProspector
import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemoncobbled.common.api.starter.StarterHandler
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.api.storage.adapter.conversions.ReforgedConversion
import com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle.FileStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle.JSONStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle.NBTStoreAdapter
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
import com.cablemc.pokemoncobbled.common.config.CobbledConfig
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.config.starter.StarterConfig
import com.cablemc.pokemoncobbled.common.data.CobbledDataProvider
import com.cablemc.pokemoncobbled.common.events.ServerTickHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.settings.ServerSettingsPacket
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
import com.cablemc.pokemoncobbled.common.permission.CobbledPermissionValidator
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.aspects.GENDER_ASPECT
import com.cablemc.pokemoncobbled.common.pokemon.aspects.SHINY_ASPECT
import com.cablemc.pokemoncobbled.common.pokemon.feature.BattleCriticalHitsFeature
import com.cablemc.pokemoncobbled.common.pokemon.feature.DamageTakenFeature
import com.cablemc.pokemoncobbled.common.pokemon.properties.UncatchableProperty
import com.cablemc.pokemoncobbled.common.pokemon.properties.UntradeableProperty
import com.cablemc.pokemoncobbled.common.pokemon.properties.tags.PokemonFlagProperty
import com.cablemc.pokemoncobbled.common.registry.CompletableRegistry
import com.cablemc.pokemoncobbled.common.starter.CobbledStarterHandler
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.ifDedicatedServer
import com.cablemc.pokemoncobbled.common.util.removeAmountIf
import com.cablemc.pokemoncobbled.common.world.CobbledGameRules
import com.cablemc.pokemoncobbled.common.worldgen.CobbledWorldgen
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.hooks.item.tool.AxeItemHooks
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.PrintWriter
import java.util.UUID
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
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

object PokemonCobbled {
    const val MODID = "pokemoncobbled"
    const val VERSION = "0.0.1"
    const val CONFIG_PATH = "config/$MODID/main.json"
    val LOGGER = LogManager.getLogger()

    lateinit var implementation: PokemonCobbledModImplementation
    lateinit var showdown: ShowdownConnection
    var captureCalculator: CaptureCalculator = CobbledGen348CaptureCalculator
    var experienceCalculator: ExperienceCalculator = StandardExperienceCalculator
    var starterHandler: StarterHandler = CobbledStarterHandler()
    var isDedicatedServer = false
    var showdownThread = ShowdownThread()
    lateinit var config: CobbledConfig
    var prospector: SpawningProspector = CobbledSpawningProspector
    var areaContextResolver: AreaContextResolver = object : AreaContextResolver {}
    val bestSpawner = BestSpawner
    var storage = PokemonStoreManager()
    lateinit var playerData: PlayerDataStoreManager
    lateinit var starterConfig: StarterConfig
    val dataProvider: DataProvider = CobbledDataProvider
    var permissionValidator: PermissionValidator by Delegates.observable(CobbledPermissionValidator().also { it.initiate() }) { _, _, newValue -> newValue.initiate() }

    fun preinitialize(implementation: PokemonCobbledModImplementation) {
        DropEntry.register("command", CommandDropEntry::class.java)
        DropEntry.register("item", ItemDropEntry::class.java, isDefault = true)

        ExperienceGroups.registerDefaults()
        PokemonSpecies.observable.subscribe { CobbledSpawnPools.load() }

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

        CobbledWorldgen.register()

        // Start up the data provider.
        CobbledDataProvider.registerDefaults()

        SHINY_ASPECT.register()
        GENDER_ASPECT.register()

        config.flagSpeciesFeatures.forEach(FlagSpeciesFeature::registerWithPropertyAndAspect)
        config.globalFlagSpeciesFeatures.forEach(FlagSpeciesFeature::registerWithPropertyAndAspect)
        SpeciesFeature.registerGlobalFeature(DamageTakenFeature.ID) { DamageTakenFeature() }
        SpeciesFeature.registerGlobalFeature(BattleCriticalHitsFeature.ID) { BattleCriticalHitsFeature() }

        CustomPokemonProperty.register(UntradeableProperty)
        CustomPokemonProperty.register(UncatchableProperty)
        CustomPokemonProperty.register(PokemonFlagProperty)

        CommandRegistrationEvent.EVENT.register(CobbledCommands::register)

        ifDedicatedServer {
            isDedicatedServer = true
            TICK_POST.subscribe { ScheduledTaskTracker.update() }
        }

        CobbledBlocks.completed.thenAccept {
            AxeItemHooks.addStrippable(CobbledBlocks.APRICORN_LOG.get(), CobbledBlocks.STRIPPED_APRICORN_LOG.get())
            AxeItemHooks.addStrippable(CobbledBlocks.APRICORN_WOOD.get(), CobbledBlocks.STRIPPED_APRICORN_WOOD.get())
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
        CobbledEvents.EVOLUTION_COMPLETE.subscribe(Priority.LOWEST) { event ->
            val pokemon = event.pokemon
            val ninjaskIdentifier = cobbledResource("ninjask")
            val shedinjaIdentifier = cobbledResource("shedinja")
            // Ensure the config option is enabled and that the result was a ninjask and that shedinja exists
            if (this.config.ninjaskCreatesShedinja && pokemon.species.resourceIdentifier == ninjaskIdentifier && PokemonSpecies.getByIdentifier(shedinjaIdentifier) != null) {
                val player = pokemon.getOwnerPlayer()
                if (player?.inventory?.containsAny { it.item is PokeBallItem } == true) {
                    player.inventory.removeAmountIf(1) { it.item is PokeBallItem }
                    val properties = event.evolution.result.copy()
                    properties.species = ninjaskIdentifier.toString()
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
        PokemonSpecies.observable.subscribe { starterConfig = this.loadStarterConfig() }
    }

    fun loadStarterConfig(): StarterConfig {
        val file = File("config/pokemoncobbled/starters.json")
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
            CobbledConfig.GSON.toJson(this.config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }
}