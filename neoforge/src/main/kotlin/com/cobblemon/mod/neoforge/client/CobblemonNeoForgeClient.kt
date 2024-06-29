/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.neoforge.client

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonClientImplementation
import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.client.render.atlas.CobblemonAtlases
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.CobblemonClient.reloadCodedAssets
import com.cobblemon.mod.common.client.keybind.CobblemonKeyBinds
import com.cobblemon.mod.common.client.render.item.CobblemonModelPredicateRegistry
import com.cobblemon.mod.common.compat.LambDynamicLightsCompat
import com.cobblemon.mod.common.client.render.shader.CobblemonShaders
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.particle.CobblemonParticles
import com.cobblemon.mod.common.particle.SnowstormParticleType
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.particle.SpriteProvider
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories
import net.minecraft.client.render.block.entity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.render.entity.model.ModelLayerLocation
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.world.item.ItemStack
import net.minecraft.particle.ParticleEffect
import net.minecraft.core.particles.ParticleType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ReloadableResourceManager
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.ClientHooks
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.common.util.MutableHashedLinkedMap
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import net.minecraft.client.particle.ParticleProvider

object CobblemonNeoForgeClient : CobblemonClientImplementation {

    fun init() {
        with(MOD_BUS) {
            addListener(::onClientSetup)
            addListener(::onKeyMappingRegister)
            addListener(::onRegisterParticleProviders)
            addListener(::register3dPokeballModels)
            addListener(::onBuildContents)
            addListener(::onRegisterReloadListener)
            addListener(::onShaderRegistration)
        }
        NeoForge.EVENT_BUS.addListener(this::onRenderGuiOverlayEvent)
    }

    private fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            CobblemonClient.initialize(this)
            this.attemptModCompat()
            CobblemonModelPredicateRegistry.registerPredicates()
        }
        NeoForgeClientPlatformEventHandler.register()
    }

    private fun onRegisterReloadListener(event: RegisterClientReloadListenersEvent) {
        event.registerReloadListener { synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor ->
            val atlasFutures = mutableListOf<CompletableFuture<Void>>()
            CobblemonAtlases.atlases.forEach {
                atlasFutures.add(
                    it.reload(
                        synchronizer,
                        manager,
                        prepareProfiler,
                        applyProfiler,
                        prepareExecutor,
                        applyExecutor
                    )
                )
            }
            val result = CompletableFuture.allOf(*atlasFutures.toTypedArray()).thenRun {
                reloadCodedAssets(manager!!)
            }
            result
        }

    }

    private fun onShaderRegistration(event: RegisterShadersEvent) {
        event.registerShader(ShaderProgram(event.resourceProvider, cobblemonResource("particle_add"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT)) {
            CobblemonShaders.PARTICLE_BLEND = it
        }
        event.registerShader(ShaderProgram(event.resourceProvider, cobblemonResource("particle_cutout"), VertexFormats.POSITION_COLOR_TEXTURE_LIGHT)) {
            CobblemonShaders.PARTICLE_CUTOUT = it
        }
    }

    @Suppress("UnstableApiUsage")
    override fun registerLayer(modelLayer: ModelLayerLocation, supplier: Supplier<TexturedModelData>) {
        ClientHooks.registerLayerDefinition(modelLayer, supplier)
    }

    override fun <T : ParticleEffect> registerParticleFactory(type: ParticleType<T>, factory: ParticleProvider<T>) {
        throw UnsupportedOperationException("Forge can't store these early, use CobblemonForgeClient#onRegisterParticleProviders")
    }

    @Suppress("DEPRECATION")
    override fun registerBlockRenderType(layer: RenderType, vararg blocks: Block) {
        blocks.forEach { block ->
            RenderLayers.setRenderLayer(block, layer)
        }
    }

    @Suppress("DEPRECATION")
    override fun registerItemColors(provider: ItemColorProvider, vararg items: Item) {
        Minecraft.getInstance().itemColors.register(provider, *items)
    }

    @Suppress("DEPRECATION")
    override fun registerBlockColors(provider: BlockColorProvider, vararg blocks: Block) {
        Minecraft.getInstance().blockColors.registerColorProvider(provider, *blocks)
    }

    override fun <T : BlockEntity> registerBlockEntityRenderer(type: BlockEntityType<out T>, factory: BlockEntityRendererProvider<T>) {
        BlockEntityRendererFactories.register(type, factory)
    }

    override fun <T : Entity> registerEntityRenderer(type: EntityType<out T>, factory: EntityRendererProvider<T>) {
        EntityRenderers.register(type, factory)
    }

    private fun register3dPokeballModels(event: ModelEvent.RegisterAdditional) {
        PokeBalls.all().forEach { pokeball ->
            event.register(
                ModelResourceLocation(
                    pokeball.model3d,
                    "inventory"
                )
            )
        }
    }

    private fun onKeyMappingRegister(event: RegisterKeyMappingsEvent) {
        CobblemonKeyBinds.register(event::register)
    }

    private fun onRegisterParticleProviders(event: RegisterParticleProvidersEvent) {
        event.registerSpriteSet(CobblemonParticles.SNOWSTORM_PARTICLE_TYPE, SnowstormParticleType::Factory)
    }

    var lastUpdateTime: Long? = null

    private fun onRenderGuiOverlayEvent(event: RenderGuiLayerEvent.Pre) {
        if (event.name == VanillaGuiLayers.CHAT) {
            val lastUpdateTime = lastUpdateTime
            if (lastUpdateTime != null) {
                // "Why don't you just use the event.partialDeltaTicks"
                // Well JAMES it's because for some reason the value is like 2.8x too big. Forge bug? Weird event structure? Don't know don't care
                CobblemonClient.beforeChatRender(event.guiGraphics, (System.currentTimeMillis() - lastUpdateTime) / 1000F * 20F)
            }
            this.lastUpdateTime = System.currentTimeMillis()
        }
    }

    internal fun registerResourceReloader(reloader: PreparableReloadListener) {
        (Minecraft.getInstance().resourceManager as ReloadableResourceManager).registerReloadListener(reloader)
    }

    private fun onBuildContents(e: BuildCreativeModeTabContentsEvent) {
        val forgeInject = ForgeItemGroupInject(e.entries)
        CobblemonItemGroups.inject(e.tabKey, forgeInject)
    }

    private fun attemptModCompat() {
        // They have no Maven nor are they published on Modrinth :(
        // Good thing is they are a copy pasta adapted to Forge :D
        if (Cobblemon.implementation.isModInstalled("dynamiclightsreforged")) {
            LambDynamicLightsCompat.hookCompat()
            Cobblemon.LOGGER.info("Dynamic Lights Reforged compatibility enabled")
        }
    }

    private class ForgeItemGroupInject(private val entries: MutableHashedLinkedMap<ItemStack, ItemGroup.StackVisibility>) : CobblemonItemGroups.Injector {

        override fun putFirst(item: ItemConvertible) {
            this.entries.putFirst(ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS)
        }

        override fun putBefore(item: ItemConvertible, target: ItemConvertible) {
            this.entries.putBefore(
                ItemStack(target),
                ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS)
        }

        override fun putAfter(item: ItemConvertible, target: ItemConvertible) {
            this.entries.putAfter(
                ItemStack(target),
                ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS)
        }

        override fun putLast(item: ItemConvertible) {
            this.entries.put(ItemStack(item), ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS)
        }

    }

}