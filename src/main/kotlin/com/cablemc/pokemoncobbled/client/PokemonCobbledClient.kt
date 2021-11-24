package com.cablemc.pokemoncobbled.client

import com.cablemc.pokemoncobbled.client.keybinding.PartySendBinding
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraftforge.fmlclient.registry.ClientRegistry
import net.minecraftforge.fmllegacy.RegistryObject

object PokemonCobbledClient {
    fun registerKeybinds() {
        ClientRegistry.registerKeyBinding(PartySendBinding)
    }

    val pokemonRendererProvider = EntityRendererProvider { PokemonRenderer(it) }

    fun registerRenderers() {
        registerEntityRenderer(PokemonCobbledMod.entityRegistry.POKEMON) { pokemonRendererProvider.create(it) }
    }

    fun initialize() {
        registerKeybinds()
        registerRenderers()
        PokemonModelRepository.initializeModelLayers()
    }

    private fun <T : Entity> registerEntityRenderer(
        registry: RegistryObject<EntityType<T>>,
        renderer: (EntityRendererProvider.Context) -> EntityRenderer<T>
    ) {
        EntityRenderers.register(registry.get(), renderer)
    }
}