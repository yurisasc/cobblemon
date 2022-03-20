package com.cablemc.pokemoncobbled.common.client

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.CobbledEntities.EMPTY_POKEBALL_TYPE
import com.cablemc.pokemoncobbled.common.CobbledEntities.POKEMON_TYPE
import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.client.gui.PartyOverlay
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketRegistrar
import com.cablemc.pokemoncobbled.common.client.render.layer.PokemonOnShoulderLayer
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.common.client.storage.ClientStorageManager
import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_JOIN
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_QUIT
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.color.block.BlockColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.world.entity.player.Player

object PokemonCobbledClient {
    val storage = ClientStorageManager()

    lateinit var overlay: PartyOverlay

    // Remove once we have a late init of some sort
    private var registeredColors = false

    fun initialize() {
        CLIENT_PLAYER_JOIN.register { storage.onLogin() }
        CLIENT_PLAYER_QUIT.register { ScheduledTaskTracker.clear() }

        overlay = PartyOverlay()
        ClientPacketRegistrar.registerHandlers()

        PokemonModelRepository.init()
        PokeBallModelRepository.init()
        registerRenderers()
        registerBlockRenderTypes()
    }

    private fun registerBlockRenderTypes() {
        RenderTypeRegistry.register(RenderType.cutout(), CobbledBlocks.BLACK_APRICORN_SAPLING.get(),
            CobbledBlocks.BLUE_APRICORN_SAPLING.get(),
            CobbledBlocks.GREEN_APRICORN_SAPLING.get(),
            CobbledBlocks.PINK_APRICORN_SAPLING.get(),
            CobbledBlocks.RED_APRICORN_SAPLING.get(),
            CobbledBlocks.WHITE_APRICORN_SAPLING.get(),
            CobbledBlocks.YELLOW_APRICORN_SAPLING.get(),
            CobbledBlocks.BLACK_APRICORN.get())
    }

    fun beforeChatRender(poseStack: PoseStack, partialDeltaTicks: Float) {
        overlay.onRenderGameOverlay(poseStack = poseStack, partialDeltaTicks = partialDeltaTicks)
    }

    fun onAddLayer(skinMap: Map<String, EntityRenderer<out Player>>?) {
        var renderer: LivingEntityRenderer<Player, PlayerModel<Player>>? = skinMap?.get("default") as LivingEntityRenderer<Player, PlayerModel<Player>>
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
        renderer = skinMap.get("slim") as LivingEntityRenderer<Player, PlayerModel<Player>>?
        renderer?.addLayer(PokemonOnShoulderLayer(renderer))
    }

    fun registerRenderers() {
        EntityRenderers.register(POKEMON_TYPE) {
            LOGGER.info("Registering Pokémon renderer")
            PokemonModelRepository.initializeModels(it)
            PokemonRenderer(it)
        }
        EntityRenderers.register(EMPTY_POKEBALL_TYPE) {
            LOGGER.info("Registering PokéBall renderer")
            PokeBallModelRepository.initializeModels(it)
            PokeBallRenderer(it)
        }
    }

    fun reloadCodedAssets() {
        if(!registeredColors) {
            Minecraft.getInstance().blockColors.register(BlockColor { blockState, blockAndTintGetter, blockPos, i ->
                return@BlockColor 0x71c219;
            }, CobbledBlocks.APRICORN_LEAVES.get())

            Minecraft.getInstance().itemColors.register(ItemColor { itemStack, i ->
                return@ItemColor 0x71c219;
            }, CobbledItems.APRICORN_LEAVES.get())

            registeredColors = true
        }
        BedrockAnimationRepository.clear()
        PokemonModelRepository.reload()
        PokeBallModelRepository.reload()
    }
}