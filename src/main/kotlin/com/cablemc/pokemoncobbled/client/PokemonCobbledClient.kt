package com.cablemc.pokemoncobbled.client

import com.cablemc.pokemoncobbled.client.keybinding.PartySendBinding
import com.cablemc.pokemoncobbled.client.render.layer.PokemonOnShoulderLayer
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.client.Minecraft
import net.minecraft.client.model.PlayerModel
import com.cablemc.pokemoncobbled.common.entity.EntityRegistry
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fmlclient.registry.ClientRegistry
import net.minecraftforge.fmllegacy.RegistryObject

object PokemonCobbledClient {
    fun registerKeybinds() {
        ClientRegistry.registerKeyBinding(PartySendBinding)
    }

    fun registerRenderers() {
        registerEntityRenderer(EntityRegistry.POKEMON) { PokemonRenderer(it) }
        registerEntityRenderer(EntityRegistry.EMPTY_POKEBALL) { PokeBallRenderer(it) }
        registerEntityRenderer(EntityRegistry.OCCUPIED_POKEBALL) { PokeBallRenderer(it) }
    }

    fun initialize() {
        registerKeybinds()
        registerRenderers()
        PokemonModelRepository.initializeModelLayers()
        PokeBallModelRepository.initializeModelLayers()
    }

    fun onAddLayer(event : EntityRenderersEvent.AddLayers) {
        var renderer: LivingEntityRenderer<Player, PlayerModel<Player>>? = event.getSkin("default")
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
        renderer = event.getSkin("slim")
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
    }

    private fun <T : Entity> registerEntityRenderer(
        registry: RegistryObject<EntityType<T>>,
        renderer: (EntityRendererProvider.Context) -> EntityRenderer<T>
    ) {
        EntityRenderers.register(registry.get(), renderer)
    }
}