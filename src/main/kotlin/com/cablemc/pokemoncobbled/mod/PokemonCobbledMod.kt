package com.cablemc.pokemoncobbled.mod

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.CommandRegistrar
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.event.pokemon.FriendshipUpdateEvent
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators.Gen7CaptureCalculator
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskListener
import com.cablemc.pokemoncobbled.common.api.spawning.condition.AreaSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.BasicSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.GroundedSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SubmergedSpawningCondition
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
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.storage.adapter.NBTStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.factory.FileBackedPokemonStoreFactory
import com.cablemc.pokemoncobbled.common.battles.ShowdownThread
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.common.command.argument.PokemonArgumentType
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.item.ItemRegistry
import com.cablemc.pokemoncobbled.common.net.PokemonCobbledNetwork
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import com.cablemc.pokemoncobbled.common.sound.SoundRegistry
import com.cablemc.pokemoncobbled.common.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.ifServer
import com.cablemc.pokemoncobbled.mod.config.CobbledConfig
import net.minecraft.client.Minecraft
import net.minecraft.commands.synchronization.ArgumentTypes
import net.minecraft.commands.synchronization.EmptyArgumentSerializer
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.LevelResource
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import org.apache.logging.log4j.LogManager
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(PokemonCobbled.MODID)
object PokemonCobbledMod {
    val LOGGER = LogManager.getLogger()
    val EVENT_BUS = BusBuilder.builder().build()
    lateinit var showdown: ShowdownConnection //TODO: Move to more appropriate place
    var captureCalculator: CaptureCalculator = Gen7CaptureCalculator()
    var isDedicatedServer = false
    var showdownThread: ShowdownThread = ShowdownThread()

    init {
        with(MOD_CONTEXT.getKEventBus()) {
            addListener(this@PokemonCobbledMod::initialize)
            addListener(this@PokemonCobbledMod::on)
            addListener(this@PokemonCobbledMod::onBake)
            addListener(PokemonCobbledClient::onAddLayer)
            EntityRegistry.register(this)
            ItemRegistry.register(this)
            SoundRegistry.register(this)
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CobbledConfig.spec)
    }

    fun initialize(event: FMLCommonSetupEvent) {
        LOGGER.info("Initializing...")

//        showdownThread.start()

        // Touching this object loads them and the stats. Probably better to use lateinit and a dedicated .register for this and stats
        LOGGER.info("Loaded ${PokemonSpecies.count()} Pokémon species.")

        // Same as PokemonSpecies
        LOGGER.info("Loaded ${Moves.count()} Moves.")

        event.enqueueWork {
            DistExecutor.safeRunWhenOn(Dist.CLIENT) { DistExecutor.SafeRunnable { PokemonCobbledClient.initialize() } }
            ifServer {
                isDedicatedServer = true
            }
            EVENT_BUS.register(ServerPacketRegistrar)
            ServerPacketRegistrar.registerHandlers()
            PokemonCobbledNetwork.register()
        }

        MinecraftForge.EVENT_BUS.register(CommandRegistrar)
        MinecraftForge.EVENT_BUS.register(PokemonCobbled.storage)
        MinecraftForge.EVENT_BUS.register(ScheduledTaskListener)
        MinecraftForge.EVENT_BUS.register(this)

        //API Events
        EVENT_BUS.register(FriendshipUpdateEvent::class)

        //Command Arguments
        ArgumentTypes.register("pokemoncobbled:pokemon", PokemonArgumentType::class.java, EmptyArgumentSerializer(PokemonArgumentType::pokemon))

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

        // debug purposes
        PokemonCobbled.spawnerManagers.forEach { MinecraftForge.EVENT_BUS.register(it) }
    }

    fun onBake(event: ModelBakeEvent) {
        BedrockAnimationRepository.clear()
        PokemonModelRepository.init()
        PokeBallModelRepository.init()
        PokemonCobbledClient.registerRenderers()
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        // TODO config options for default storage
        val pokemonStoreRoot = event.server.getWorldPath(LevelResource.PLAYER_DATA_DIR).parent.resolve("pokemon").toFile()
        PokemonCobbled.storage.registerFactory(
            priority = EventPriority.LOWEST,
            factory = FileBackedPokemonStoreFactory(
                adapter = NBTStoreAdapter(pokemonStoreRoot.absolutePath, useNestedFolders = true, folderPerClass = true),
                createIfMissing = true
            )
        )
    }

    fun on(event: EntityAttributeCreationEvent) {
        EntityRegistry.registerAttributes(event)
    }

    fun getLevel(dimension: ResourceKey<Level>): Level? {
        return if (isDedicatedServer) {
            getServer().getLevel(dimension)
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
}