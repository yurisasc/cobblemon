package com.cablemc.pokemoncobbled.forge.mod.client

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.net.serverhandling.ServerPacketRegistrar
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(
    modid = PokemonCobbled.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object PokemonCobbledForgeClient {
    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        PokemonCobbledClient.initialize()
        MinecraftForge.EVENT_BUS.register(this)
        event.enqueueWork {
            ServerPacketRegistrar.registerHandlers()
            CobbledNetwork.register()
        }
    }

    @SubscribeEvent
    fun onBake(event: ModelBakeEvent) {
        PokemonCobbledClient.reloadCodedAssets()
    }

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return
        PokemonCobbledClient.overlay.onRenderGameOverlay(
            poseStack = event.matrixStack,
            partialDeltaTicks = event.partialTicks
        )
    }
}