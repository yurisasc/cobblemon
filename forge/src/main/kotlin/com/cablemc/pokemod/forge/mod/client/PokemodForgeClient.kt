/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.forge.mod.client

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.PokemodClientImplementation
import com.cablemc.pokemod.common.PokemodEntities.EMPTY_POKEBALL_TYPE
import com.cablemc.pokemod.common.PokemodEntities.POKEMON_TYPE
import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import java.util.function.Supplier
import net.minecraft.client.MinecraftClient
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.EntityRenderers
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.resource.ReloadableResourceManagerImpl
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.SynchronousResourceReloader
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(
    modid = Pokemod.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object PokemodForgeClient : PokemodClientImplementation {
    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        (MinecraftClient.getInstance().resourceManager as ReloadableResourceManagerImpl)
            .registerReloader(object : SynchronousResourceReloader {
                override fun reload(resourceManager: ResourceManager) {
                    PokemodClient.reloadCodedAssets(resourceManager)
                }
            })
        PokemodClient.reloadCodedAssets(MinecraftClient.getInstance().resourceManager)
        MinecraftForge.EVENT_BUS.register(this)
        event.enqueueWork {
            PokemodClient.initialize(this)
            EntityRenderers.register(POKEMON_TYPE) { PokemodClient.registerPokemonRenderer(it) }
            EntityRenderers.register(EMPTY_POKEBALL_TYPE) { PokemodClient.registerPokeBallRenderer(it) }
            PokemodNetwork.register()
        }
    }

    override fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        ForgeHooksClient.registerLayerDefinition(modelLayer, supplier)
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGuiOverlayEvent.Pre) {
        if (event.overlay.id == VanillaGuiOverlay.CHAT_PANEL.id()) {
            PokemodClient.beforeChatRender(event.poseStack, event.partialTick)
        }
    }
}