package com.cablemc.pokemoncobbled.forge.mod.client

import com.cablemc.pokemoncobbled.common.CobbledEntities.EMPTY_POKEBALL_TYPE
import com.cablemc.pokemoncobbled.common.CobbledEntities.POKEMON_TYPE
import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.networking.forge.NetworkManagerImpl
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import java.util.function.Supplier

@EventBusSubscriber(
    modid = PokemonCobbled.MODID,
    bus = EventBusSubscriber.Bus.MOD,
    value = [Dist.CLIENT]
)
object PokemonCobbledForgeClient : PokemonCobbledClientImplementation {
    @JvmStatic
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        NetworkManagerImpl.canServerReceive(cobbledResource("dummy"))
        MinecraftForge.EVENT_BUS.register(this)
        event.enqueueWork {
            PokemonCobbledClient.initialize(this)
            EntityRenderers.register(POKEMON_TYPE) { PokemonCobbledClient.registerPokemonRenderer(it) }
            EntityRenderers.register(EMPTY_POKEBALL_TYPE) { PokemonCobbledClient.registerPokeBallRenderer(it) }
            CobbledNetwork.register()
        }
    }

    @SubscribeEvent
    fun onBake(event: ModelBakeEvent) {
        PokemonCobbledClient.reloadCodedAssets()
    }

    override fun registerLayer(layerLocation: ModelLayerLocation, supplier: Supplier<LayerDefinition>) {
        LOGGER.info("Layer registration: $layerLocation")
        ForgeHooksClient.registerLayerDefinition(layerLocation, supplier)
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