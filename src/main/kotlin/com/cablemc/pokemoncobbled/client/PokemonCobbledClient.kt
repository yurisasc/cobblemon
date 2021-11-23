package com.cablemc.pokemoncobbled.client

import com.cablemc.pokemoncobbled.client.keybinding.PartySendBinding
import com.cablemc.pokemoncobbled.client.render.blockbench.EeveeModel
import com.cablemc.pokemoncobbled.client.render.pokemon.EeveeRenderer
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.fmlclient.registry.ClientRegistry
import net.minecraftforge.fmllegacy.RegistryObject

object PokemonCobbledClient {
    fun registerKeybinds() {
        ClientRegistry.registerKeyBinding(PartySendBinding)
    }

    val pokemonRendererProvider = EntityRendererProvider { EeveeRenderer(it) }

    fun registerRenderers() {
        registerEntityRenderer(PokemonCobbledMod.entityRegistry.POKEMON) { pokemonRendererProvider.create(it) }
    }

    fun registerLayerDefinitions() {
        ForgeHooksClient.registerLayerDefinition(EeveeModel.LAYER_LOCATION, EeveeModel::createBodyLayer)
    }

    fun initialize() {
        registerKeybinds()
        registerRenderers()
        registerLayerDefinitions()
    }

    private fun <T : Entity> registerEntityRenderer(
        registry: RegistryObject<EntityType<T>>,
        renderer: (EntityRendererProvider.Context) -> EntityRenderer<T>
    ) {
        EntityRenderers.register(registry.get(), renderer)
    }
}