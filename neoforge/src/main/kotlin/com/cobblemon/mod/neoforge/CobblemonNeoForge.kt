/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.neoforge

import com.cobblemon.mod.common.*
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.net.serializers.IdentifierDataSerializer
import com.cobblemon.mod.common.api.net.serializers.PoseTypeDataSerializer
import com.cobblemon.mod.common.api.net.serializers.StringSetDataSerializer
import com.cobblemon.mod.common.api.net.serializers.Vec3DataSerializer
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.loot.LootInjector
import com.cobblemon.mod.common.particle.CobblemonParticles
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.didSleep
import com.cobblemon.mod.common.util.endsWith
import com.cobblemon.mod.common.world.CobblemonStructures
import com.cobblemon.mod.common.world.feature.CobblemonFeatures
import com.cobblemon.mod.common.world.placementmodifier.CobblemonPlacementModifierTypes
import com.cobblemon.mod.common.world.predicate.CobblemonBlockPredicates
import com.cobblemon.mod.common.world.structureprocessors.CobblemonProcessorTypes
import com.cobblemon.mod.common.world.structureprocessors.CobblemonStructureProcessorListOverrides
import com.cobblemon.mod.neoforge.brewing.CobblemonNeoForgeBrewingRegistry
import com.cobblemon.mod.neoforge.client.CobblemonNeoForgeClient
import com.cobblemon.mod.neoforge.event.NeoForgePlatformEventHandler
import com.cobblemon.mod.neoforge.net.CobblemonNeoForgeNetworkManager
import com.cobblemon.mod.neoforge.net.NeoForgePacketInfo
import com.cobblemon.mod.neoforge.permission.ForgePermissionValidator
import com.cobblemon.mod.neoforge.worldgen.CobblemonBiomeModifiers
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.advancement.criterion.Criterion
import net.minecraft.block.ComposterBlock
import net.minecraft.command.argument.ArgumentTypes
import net.minecraft.command.argument.serialize.ArgumentSerializer
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.item.*
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.resource.*
import net.minecraft.resource.ResourcePackProfile.PackFactory
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.GameRules
import net.minecraft.world.biome.Biome
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.PlacedFeature
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.InterModComms
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.NeoForgeMod
import net.neoforged.neoforge.common.ToolActions
import net.neoforged.neoforge.event.AddPackFindersEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.LootTableLoadEvent
import net.neoforged.neoforge.event.OnDatapackSyncEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import net.neoforged.neoforge.event.village.VillagerTradesEvent
import net.neoforged.neoforge.event.village.WandererTradesEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.io.File
import java.util.*
import java.util.concurrent.ExecutionException
import kotlin.reflect.KClass

@Mod(Cobblemon.MODID)
class CobblemonNeoForge : CobblemonImplementation {
    override val modAPI = ModAPI.NEOFORGE
    private val hasBeenSynced = hashSetOf<UUID>()

    private val commandArgumentTypes = DeferredRegister.create(RegistryKeys.COMMAND_ARGUMENT_TYPE, Cobblemon.MODID)
    private val reloadableResources = arrayListOf<ResourceReloader>()
    private val queuedWork = arrayListOf<() -> Unit>()
    private val queuedBuiltinResourcePacks = arrayListOf<Triple<Identifier, Text, ResourcePackActivationBehaviour>>()

    override val networkManager = CobblemonNeoForgeNetworkManager

    init {
        with(MOD_BUS) {
            this@CobblemonNeoForge.commandArgumentTypes.register(this)
            addListener(this@CobblemonNeoForge::initialize)
            addListener(this@CobblemonNeoForge::serverInit)
            Cobblemon.preInitialize(this@CobblemonNeoForge)
            addListener(CobblemonBiomeModifiers::register)
            addListener(this@CobblemonNeoForge::on)
            addListener(this@CobblemonNeoForge::onAddPackFindersEvent)
            addListener(networkManager::registerMessages)
        }
        with(NeoForge.EVENT_BUS) {
            addListener(this@CobblemonNeoForge::onDataPackSync)
            addListener(this@CobblemonNeoForge::onLogin)
            addListener(this@CobblemonNeoForge::onLogout)
            addListener(this@CobblemonNeoForge::wakeUp)
            addListener(this@CobblemonNeoForge::handleBlockStripping)
            addListener(this@CobblemonNeoForge::registerCommands)
            addListener(this@CobblemonNeoForge::onReload)
            addListener(this@CobblemonNeoForge::addCobblemonStructures)
            addListener(::onVillagerTradesRegistry)
            addListener(::onWanderingTraderRegistry)
            addListener(::onLootTableLoad)
            addListener(::onRegisterBrewingRecipes)
        }
        NeoForgePlatformEventHandler.register()
        if (FMLEnvironment.dist == Dist.CLIENT) {
            CobblemonNeoForgeClient.init()
        }
    }

    fun addCobblemonStructures(event: ServerAboutToStartEvent) {
        CobblemonStructures.registerJigsaws(event.server)
        CobblemonStructureProcessorListOverrides.register(event.server)
    }

    fun wakeUp(event: PlayerWakeUpEvent) {
        val playerEntity = event.entity as? ServerPlayerEntity ?: return
        playerEntity.didSleep()
    }

    @Suppress("UNUSED_PARAMETER")
    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        Cobblemon.LOGGER.info("Initializing...")
        event.enqueueWork {
            this.queuedWork.forEach { it.invoke() }
            this.attemptModCompat()
        }
        Cobblemon.initialize()
    }

    fun on(event: RegisterEvent) {

        event.register(RegistryKeys.BLOCK_PREDICATE_TYPE) {
            CobblemonBlockPredicates.touch()
        }
        event.register(RegistryKeys.PLACEMENT_MODIFIER_TYPE) {
            CobblemonPlacementModifierTypes.touch()
        }

        event.register(RegistryKeys.STRUCTURE_PROCESSOR) {
            CobblemonProcessorTypes.touch()
        }

        event.register(RegistryKeys.ACTIVITY) { registry ->
            CobblemonActivities.activities.forEach {
                registry.register(cobblemonResource(it.id), it)
            }
        }
    }

    fun onDataPackSync(event: OnDatapackSyncEvent) {
        Cobblemon.dataProvider.sync(event.player ?: return)
    }

    fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        this.hasBeenSynced.add(event.entity.uuid)
    }

    fun onLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        this.hasBeenSynced.remove(event.entity.uuid)
    }

    override fun isModInstalled(id: String) = ModList.get().isLoaded(id)

    override fun environment(): Environment {
        return if (FMLEnvironment.dist.isClient) Environment.CLIENT else Environment.SERVER
    }

    override fun registerPermissionValidator() {
        Cobblemon.permissionValidator = ForgePermissionValidator
    }

    override fun registerSoundEvents() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonSounds.registryKey) { helper ->
                CobblemonSounds.register { identifier, sounds -> helper.register(identifier, sounds) }
            }
        }
    }

    override fun registerDataComponents() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonItemComponents.registryKey) { helper ->
                CobblemonItemComponents.register { identifier, dataComponentType ->  helper.register(identifier, dataComponentType)}
            }
        }
    }

    override fun registerEntityDataSerializers() {
        MOD_BUS.addListener<RegisterEvent> {
            val registry = NeoForgeRegistries.ENTITY_DATA_SERIALIZERS
            Registry.register(registry, Vec3DataSerializer.ID, Vec3DataSerializer)
            Registry.register(registry, StringSetDataSerializer.ID, StringSetDataSerializer)
            Registry.register(registry, PoseTypeDataSerializer.ID, PoseTypeDataSerializer)
            Registry.register(registry, IdentifierDataSerializer.ID, IdentifierDataSerializer)
        }
    }

    override fun registerBlocks() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonBlocks.registryKey) { helper ->
                CobblemonBlocks.register { identifier, block -> helper.register(identifier, block) }
            }
        }
    }

    override fun registerParticles() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonParticles.registryKey) { helper ->
                CobblemonParticles.register { identifier, particleType -> helper.register(identifier, particleType) }
            }
        }
    }

    private fun handleBlockStripping(e: BlockEvent.BlockToolModificationEvent) {
        if (e.toolAction == ToolActions.AXE_STRIP) {
            val start = e.state.block
            val result = CobblemonBlocks.strippedBlocks()[start] ?: return
            e.setFinalState(result.getStateWithProperties(e.state))
        }
    }

    override fun registerItems() {
        with(MOD_BUS) {
            addListener<RegisterEvent> { event ->
                event.register(CobblemonItems.registryKey) { helper ->
                    CobblemonItems.register { identifier, item -> helper.register(identifier, item) }
                }
            }
            addListener<RegisterEvent> { event ->
                event.register(RegistryKeys.ITEM_GROUP) { helper ->
                    CobblemonItemGroups.register { holder ->
                        val itemGroup = ItemGroup.builder()
                            .displayName(holder.displayName)
                            .icon(holder.displayIconProvider)
                            .entries(holder.entryCollector)
                            .build()
                        helper.register(holder.key, itemGroup)
                        itemGroup
                    }
                }
            }
        }
    }

    override fun registerEntityTypes() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonEntities.registryKey) { helper ->
                CobblemonEntities.register { identifier, type -> helper.register(identifier, type) }
            }
        }
    }

    override fun registerEntityAttributes() {
        MOD_BUS.addListener<EntityAttributeCreationEvent> { event ->
            CobblemonEntities.registerAttributes { entityType, builder ->
                builder.add(NeoForgeMod.NAMETAG_DISTANCE)
                    .add(NeoForgeMod.SWIM_SPEED)
                    //.add(ForgeMod.ENTITY_GRAVITY)
                event.put(entityType, builder.build())
            }
        }
    }

    override fun registerBlockEntityTypes() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonBlockEntities.registryKey) { helper ->
                CobblemonBlockEntities.register { identifier, type -> helper.register(identifier, type) }
            }
        }
    }

    override fun registerWorldGenFeatures() {
        MOD_BUS.addListener<RegisterEvent> { event ->
            event.register(CobblemonFeatures.registryKey) { helper ->
                CobblemonFeatures.register { identifier, feature -> helper.register(identifier, feature) }
            }
        }
    }

    override fun addFeatureToWorldGen(feature: RegistryKey<PlacedFeature>, step: GenerationStep.Feature, validTag: TagKey<Biome>?) {
        CobblemonBiomeModifiers.add(feature, step, validTag)
    }

    override fun <A : ArgumentType<*>, T : ArgumentSerializer.ArgumentTypeProperties<A>> registerCommandArgument(identifier: Identifier, argumentClass: KClass<A>, serializer: ArgumentSerializer<A, T>) {

        //This is technically a supplier not a function (it is unused), but we need to explicitly say whether its a supplier or a function
        //Idk how to explicitly say its a supplier, so lets just make it a function by specifying a param
        this.commandArgumentTypes.register(identifier.path) { it ->
            ArgumentTypes.registerByClass(argumentClass.java, serializer)
        }
    }

    private fun registerCommands(e: RegisterCommandsEvent) {
        CobblemonCommands.register(e.dispatcher, e.buildContext, e.commandSelection)
    }

    override fun <T : GameRules.Rule<T>> registerGameRule(name: String, category: GameRules.Category, type: GameRules.Type<T>): GameRules.Key<T> = GameRules.register(name, category, type)

    override fun <T : Criterion<*>> registerCriteria(id: String, criteria: T): T = Criteria.register(id, criteria)

    override fun registerResourceReloader(identifier: Identifier, reloader: ResourceReloader, type: ResourceType, dependencies: Collection<Identifier>) {
        if (type == ResourceType.SERVER_DATA) {
            this.reloadableResources += reloader
        }
        else {
            CobblemonNeoForgeClient.registerResourceReloader(reloader)
        }
    }

    private fun onReload(e: AddReloadListenerEvent) {
        this.reloadableResources.forEach(e::addListener)
    }

    override fun server(): MinecraftServer? = ServerLifecycleHooks.getCurrentServer()

    override fun <T> reloadJsonRegistry(registry: JsonDataRegistry<T>, manager: ResourceManager): HashMap<Identifier, T> {
        val data = hashMapOf<Identifier, T>()

        manager.findResources(registry.resourcePath) { path -> path.endsWith(JsonDataRegistry.JSON_EXTENSION) }.forEach { (identifier, resource) ->
            if (identifier.namespace == "pixelmon") {
                return@forEach
            }

            resource.inputStream.use { stream ->
                stream.bufferedReader().use { reader ->
                    val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                    try {
                        data[resolvedIdentifier] = registry.gson.fromJson(reader, registry.typeToken.type)
                    } catch (exception: Exception) {
                        throw ExecutionException("Error loading JSON for data: $identifier", exception)
                    }
                }
            }
        }
        return data
    }

    override fun registerCompostable(item: ItemConvertible, chance: Float) {
        this.queuedWork += {
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, chance)
        }
    }

    override fun registerBuiltinResourcePack(id: Identifier, title: Text, activationBehaviour: ResourcePackActivationBehaviour) {
        this.queuedBuiltinResourcePacks += Triple(id, title, activationBehaviour)
    }

    //TODO: I dont really know wtf is happening here, someone needs to check
    fun onAddPackFindersEvent(event: AddPackFindersEvent) {
        if (event.packType != ResourceType.CLIENT_RESOURCES) {
            return
        }
        val modFile = ModList.get().getModFileById(Cobblemon.MODID).file
        this.queuedBuiltinResourcePacks.forEach { (id, title, activationBehaviour) ->
            // Fabric expects resourcepacks as the root so we do too here
            val path = modFile.findResource("resourcepacks/${id.path}")
            //val factory = PackFactory { name -> PathPackResources(name, true, path) }

            //TODO(Deltric)
            val factory = object : PackFactory {
                override fun open(info: ResourcePackInfo): ResourcePack {
                    // Implement the logic here
                    return DirectoryResourcePack(info, path)
                }

                override fun openWithOverlays(
                    info: ResourcePackInfo?,
                    metadata: ResourcePackProfile.Metadata?
                ): ResourcePack {
                    return DirectoryResourcePack(info, path)

                }
            }

            val profile = ResourcePackProfile.create(
                ResourcePackInfo(
                    id.toString(),
                    title,
                    ResourcePackSource.BUILTIN,
                    null
                ),
                factory,
                ResourceType.CLIENT_RESOURCES,
                ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, true)
            )
            event.addRepositorySource { consumer -> consumer.accept(profile) }
        }
    }

    private fun onVillagerTradesRegistry(e: VillagerTradesEvent) {
        CobblemonTradeOffers.tradeOffersFor(e.type).forEach { tradeOffer ->
            // Will never be null between 1 n 5
            e.trades[tradeOffer.requiredLevel]?.addAll(tradeOffer.tradeOffers)
        }
    }

    private fun onWanderingTraderRegistry(e: WandererTradesEvent) {
        CobblemonTradeOffers.resolveWanderingTradeOffers().forEach { tradeOffer ->
            if (tradeOffer.isRareTrade) e.rareTrades.addAll(tradeOffer.tradeOffers) else e.genericTrades.addAll(tradeOffer.tradeOffers)
        }
    }

    private fun onLootTableLoad(e: LootTableLoadEvent) {
        LootInjector.attemptInjection(e.name) { builder -> e.table.addPool(builder.build()) }
    }

    private fun onRegisterBrewingRecipes(e: RegisterBrewingRecipesEvent) {
        CobblemonNeoForgeBrewingRegistry.register(e)
    }

    private fun attemptModCompat() {
        // CarryOn has a tag key for this but for some reason Forge version just doesn't work instead we do this :)
        // See https://github.com/Tschipp/CarryOn/wiki/IMC-support-for-Modders
        if (this.isModInstalled("carryon")) {
            InterModComms.sendTo("carryon", "blacklistEntity") { CobblemonEntities.POKEMON_KEY.toString() }
            InterModComms.sendTo("carryon", "blacklistEntity") { CobblemonEntities.EMPTY_POKEBALL_KEY.toString() }
        }
    }

}