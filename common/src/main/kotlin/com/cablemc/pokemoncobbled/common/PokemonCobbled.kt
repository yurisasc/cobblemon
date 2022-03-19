package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.net.serializers.Vec3DataSerializer
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.Gen7CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffectRegistry
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.api.spawning.CobbledSpawningProspector
import com.cablemc.pokemoncobbled.common.api.spawning.CobbledWorldSpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.SpawnerManager
import com.cablemc.pokemoncobbled.common.api.spawning.condition.AreaSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.BasicSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.GroundedSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SubmergedSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.AreaContextResolver
import com.cablemc.pokemoncobbled.common.api.spawning.context.GroundedSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.LavafloorSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SeafloorSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.SpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.UnderlavaSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.UnderwaterSpawningContext
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.GroundedSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.LavafloorSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SeafloorSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.UnderlavaSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.UnderwaterSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.api.storage.adapter.NBTStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.factory.FileBackedPokemonStoreFactory
import com.cablemc.pokemoncobbled.common.battles.ShowdownThread
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeybinds
import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.config.CobbledConfig
import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.ifClient
import com.cablemc.pokemoncobbled.common.util.ifDedicatedServer
import com.cablemc.pokemoncobbled.common.util.ifServer
import com.google.gson.GsonBuilder
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.LifecycleEvent.SERVER_STARTED
import dev.architectury.event.events.common.PlayerEvent.PLAYER_JOIN
import dev.architectury.event.events.common.TickEvent.SERVER_POST
import net.minecraft.client.Minecraft
import net.minecraft.commands.synchronization.ArgumentTypes
import net.minecraft.commands.synchronization.EmptyArgumentSerializer
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelResource
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

object PokemonCobbled {
    const val MODID = "pokemoncobbled"
    const val VERSION = "0.0.1"
    val LOGGER = LogManager.getLogger()

    lateinit var implementation: PokemonCobbledModImplementation
    lateinit var showdown: ShowdownConnection
    var captureCalculator: CaptureCalculator = Gen7CaptureCalculator()
    var isDedicatedServer = false
    var showdownThread = ShowdownThread()
    var config = CobbledConfig()
    var prospector: SpawningProspector = CobbledSpawningProspector
    var areaContextResolver: AreaContextResolver = object : AreaContextResolver {}
    val spawnerManagers = mutableListOf<SpawnerManager>(CobbledWorldSpawnerManager)
    var storage = PokemonStoreManager()

    fun preinitialize(implementation: PokemonCobbledModImplementation) {
        this.loadConfig()
        this.implementation = implementation
        CobbledEntities.register()
        CobbledItems.register()
        CobbledSounds.register()
        CobbledNetwork.register()
        CobbledKeybinds.register()

        ShoulderEffectRegistry.register()
        PLAYER_JOIN.register { storage.onPlayerLogin(it) }
        EntityDataSerializers.registerSerializer(Vec3DataSerializer)
        //Command Arguments
        ArgumentTypes.register("pokemoncobbled:pokemon", PokemonArgumentType::class.java, EmptyArgumentSerializer(PokemonArgumentType::pokemon))
    }

    fun initialize() {
        //showdownThread.start()

        // Touching this object loads them and the stats. Probably better to use lateinit and a dedicated .register for this and stats
        LOGGER.info("Loaded ${PokemonSpecies.count()} Pokémon species.")

        // Same as PokemonSpecies
        LOGGER.info("Loaded ${Moves.count()} Moves.")

        CommandRegistrationEvent.EVENT.register(CobbledCommands::register)

        ifDedicatedServer { isDedicatedServer = true }
        ifServer { SERVER_POST.register { ScheduledTaskTracker.update() } }
        ifClient { ClientGuiEvent.RENDER_HUD.register(ClientGuiEvent.RenderHud { _, _ -> ScheduledTaskTracker.update() }) }

        SERVER_STARTED.register {
            // TODO config options for default storage
            val pokemonStoreRoot = it.getWorldPath(LevelResource.PLAYER_DATA_DIR).parent.resolve("pokemon").toFile()
            storage.registerFactory(
                priority = Priority.LOWEST,
                factory = FileBackedPokemonStoreFactory(
                    adapter = NBTStoreAdapter(pokemonStoreRoot.absolutePath, useNestedFolders = true, folderPerClass = true),
                    createIfMissing = true
                )
            )
        }

        SpawningContextCalculator.register(GroundedSpawningContextCalculator)
        SpawningContextCalculator.register(SeafloorSpawningContextCalculator)
        SpawningContextCalculator.register(LavafloorSpawningContextCalculator)
        SpawningContextCalculator.register(UnderwaterSpawningContextCalculator)
        SpawningContextCalculator.register(UnderlavaSpawningContextCalculator)

        SpawningCondition.register(BasicSpawningCondition.NAME, BasicSpawningCondition::class.java)
        SpawningCondition.register(AreaSpawningCondition.NAME, AreaSpawningCondition::class.java)
        SpawningCondition.register(SubmergedSpawningCondition.NAME, SubmergedSpawningCondition::class.java)
        SpawningCondition.register(GroundedSpawningCondition.NAME, GroundedSpawningCondition::class.java)

        SpawningContext.register(name = "grounded", clazz = GroundedSpawningContext::class.java, defaultCondition = GroundedSpawningCondition.NAME)
        SpawningContext.register(name = "seafloor", clazz = SeafloorSpawningContext::class.java, defaultCondition = GroundedSpawningCondition.NAME)
        SpawningContext.register(name = "lavafloor", clazz = LavafloorSpawningContext::class.java, defaultCondition = GroundedSpawningCondition.NAME)
        SpawningContext.register(name = "underwater", clazz = UnderwaterSpawningContext::class.java, defaultCondition = SubmergedSpawningCondition.NAME)
        SpawningContext.register(name = "underlava", clazz = UnderlavaSpawningContext::class.java, defaultCondition = SubmergedSpawningCondition.NAME)

        SpawnDetail.registerSpawnType(name = PokemonSpawnDetail.TYPE, PokemonSpawnDetail::class.java)

        SERVER_STARTED.register { spawnerManagers.forEach { it.onServerStarted() } }
        SERVER_POST.register { spawnerManagers.forEach { it.onServerTick() } }


    }

    fun getLevel(dimension: ResourceKey<Level>): Level? {
        return if (isDedicatedServer) {
            getServer()?.getLevel(dimension)
        } else {
            val mc = Minecraft.getInstance()
            if (mc.singleplayerServer != null) {
                mc.singleplayerServer!!.getLevel(dimension)
            } else if (mc.level?.dimension() == dimension) {
                mc.level
            } else {
                null
            }
        }
    }

    fun loadConfig() {
        val configFile = File("config/$MODID.json")
        configFile.mkdirs()
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        LOGGER.info(configFile.absolutePath)

        // Check config existence and load if it exists, otherwise create default.
        if (configFile.exists()) {
            try {
                val fileReader = FileReader(configFile)
                this.config = gson.fromJson(fileReader, CobbledConfig::class.java)
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
    }

    fun saveConfig() {
        try {
            val configFile = File("config/$MODID.json")
            val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()
            val fileWriter = FileWriter(configFile)

            // Put the config to json then flush the writer to commence writing.
            gson.toJson(this.config, fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } catch (exception: Exception) {
            LOGGER.error("Failed to save the config! Please consult the following stack trace:")
            exception.printStackTrace()
        }
    }
}
