package com.cablemc.pokemoncobbled.common.client

import com.cablemc.pokemoncobbled.common.CobbledEntities
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.client.gui.PartyOverlay
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketRegistrar
import com.cablemc.pokemoncobbled.common.client.render.layer.PokemonOnShoulderLayer
import com.cablemc.pokemoncobbled.common.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.common.client.storage.ClientStorageManager
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_JOIN
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_QUIT
import dev.architectury.registry.level.entity.EntityRendererRegistry
import net.minecraft.client.KeyMapping
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.world.entity.player.Player

object PokemonCobbledClient : PokemonCobbledClientImplementation {
    val storage = ClientStorageManager()

    fun registerKeyBinds() {
//        registerKeyBind(PartySendBinding)
//        registerKeyBind(DownShiftPartyBinding)
//        registerKeyBind(UpShiftPartyBinding)
//        registerKeyBind(SummaryBinding)
    }

    fun registerKeyBind(binding: KeyMapping) {
//        ClientRegistry.registerKeyBinding(binding)
//        MinecraftForge.EVENT_BUS.register(binding)
    }

    fun registerRenderers() {
        EntityRendererRegistry.register({ CobbledEntities.POKEMON_TYPE }, { PokemonRenderer(it) })
        EntityRendererRegistry.register({ CobbledEntities.EMPTY_POKEBALL_TYPE }, { PokeBallRenderer(it) })
    }

    override fun initialize() {
        CLIENT_PLAYER_JOIN.register {
            storage.onLogin()
        }

        CLIENT_PLAYER_QUIT.register {
            // TODO why removed?
            //ClientSchedulingListener.onLogout()
        }

        val overlay = PartyOverlay()
        ClientGuiEvent.RENDER_HUD.register(overlay::onRenderGameOverlay)
        ClientPacketRegistrar.register()
        ClientPacketRegistrar.registerHandlers()
        registerKeyBinds()
    }

    fun onAddLayer(skinMap: Map<String, EntityRenderer<out Player>>?) {
        var renderer: LivingEntityRenderer<Player, PlayerModel<Player>>? = skinMap?.get("default") as LivingEntityRenderer<Player, PlayerModel<Player>>
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
        renderer = skinMap?.get("slim") as LivingEntityRenderer<Player, PlayerModel<Player>>
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
    }
}