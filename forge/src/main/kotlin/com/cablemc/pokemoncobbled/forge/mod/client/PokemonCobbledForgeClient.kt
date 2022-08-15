package com.cablemc.pokemoncobbled.forge.mod.client

import com.cablemc.pokemoncobbled.common.CobbledEntities.EMPTY_POKEBALL_TYPE
import com.cablemc.pokemoncobbled.common.CobbledEntities.POKEMON_TYPE
import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.EntityRenderers
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourceReloader
import net.minecraft.util.profiler.Profiler
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(
    modid = PokemonCobbled.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object PokemonCobbledForgeClient : PokemonCobbledClientImplementation {
    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
//        NetworkManagerImpl.canServerReceive(cobbledResource("dummy"))
        MinecraftForge.EVENT_BUS.register(this)
        event.enqueueWork {
            PokemonCobbledClient.initialize(this)
            EntityRenderers.register(POKEMON_TYPE) { PokemonCobbledClient.registerPokemonRenderer(it) }
            EntityRenderers.register(EMPTY_POKEBALL_TYPE) { PokemonCobbledClient.registerPokeBallRenderer(it) }
            CobbledNetwork.register()
        }
    }

    override fun registerLayer(modelLayer: EntityModelLayer, supplier: Supplier<TexturedModelData>) {
        ForgeHooksClient.registerLayerDefinition(modelLayer, supplier)
    }

    @SubscribeEvent
    fun onAddReloadHandler(event: AddReloadListenerEvent) {
        event.addListener(object : ResourceReloader {
            override fun reload(
                synchronizer: ResourceReloader.Synchronizer?,
                manager: ResourceManager?,
                prepareProfiler: Profiler?,
                applyProfiler: Profiler?,
                prepareExecutor: Executor?,
                applyExecutor: Executor?
            ): CompletableFuture<Void> {
                TODO("Not yet implemented")
            }

        })
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGuiOverlayEvent.Pre) {
        println(event.overlay.id)
        //if (event.type != RenderGuiOverlayEvent.ElementType.ALL) return
        PokemonCobbledClient.beforeChatRender(event.poseStack, event.partialTick)
    }
}