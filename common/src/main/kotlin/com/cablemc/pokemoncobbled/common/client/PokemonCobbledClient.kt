package com.cablemc.pokemoncobbled.common.client

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbledClientImplementation
import com.cablemc.pokemoncobbled.common.api.scheduling.ScheduledTaskTracker
import com.cablemc.pokemoncobbled.common.client.gui.PartyOverlay
import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeybinds
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketRegistrar
import com.cablemc.pokemoncobbled.common.client.render.block.HealingMachineRenderer
import com.cablemc.pokemoncobbled.common.client.render.layer.PokemonOnShoulderLayer
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cablemc.pokemoncobbled.common.client.render.pokeball.PokeBallRenderer
import com.cablemc.pokemoncobbled.common.client.render.pokemon.PokemonRenderer
import com.cablemc.pokemoncobbled.common.client.storage.ClientStorageManager
import com.mojang.blaze3d.vertex.PoseStack
import dev.architectury.event.events.client.ClientGuiEvent
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_JOIN
import dev.architectury.event.events.client.ClientPlayerEvent.CLIENT_PLAYER_QUIT
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import dev.architectury.registry.client.rendering.ColorHandlerRegistry
import dev.architectury.registry.client.rendering.RenderTypeRegistry
import net.minecraft.client.color.block.BlockColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.client.model.PlayerModel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.world.entity.player.Player

object PokemonCobbledClient {
    lateinit var implementation: PokemonCobbledClientImplementation
    val storage = ClientStorageManager()

    lateinit var overlay: PartyOverlay

    fun initialize(implementation: PokemonCobbledClientImplementation) {
        LOGGER.info("Initializing Pokémon Cobbled client")
        this.implementation = implementation

        CLIENT_PLAYER_JOIN.register { storage.onLogin() }
        CLIENT_PLAYER_QUIT.register { ScheduledTaskTracker.clear() }

        overlay = PartyOverlay()
        ClientPacketRegistrar.registerHandlers()
        CobbledKeybinds.register()

        ClientGuiEvent.RENDER_HUD.register(ClientGuiEvent.RenderHud { _, _ -> ScheduledTaskTracker.update() })

        LOGGER.info("Initializing Pokémon models")
        PokemonModelRepository.init()
        LOGGER.info("Initializing PokéBall models")
        PokeBallModelRepository.init()

        BlockEntityRendererRegistry.register(CobbledBlockEntities.HEALING_MACHINE.get(), ::HealingMachineRenderer)

        registerBlockRenderTypes()
        registerColors()
    }

    fun registerColors() {
        ColorHandlerRegistry.registerBlockColors(BlockColor { blockState, blockAndTintGetter, blockPos, i ->
            return@BlockColor 0x71c219;
        }, CobbledBlocks.APRICORN_LEAVES.get())

        ColorHandlerRegistry.registerItemColors(ItemColor { itemStack, i ->
            return@ItemColor 0x71c219;
        }, CobbledItems.APRICORN_LEAVES.get())
    }

    private fun registerBlockRenderTypes() {
        RenderTypeRegistry.register(RenderType.cutout(),
            CobbledBlocks.APRICORN_DOOR.get(),
            CobbledBlocks.APRICORN_TRAPDOOR.get(),
            CobbledBlocks.BLACK_APRICORN_SAPLING.get(),
            CobbledBlocks.BLUE_APRICORN_SAPLING.get(),
            CobbledBlocks.GREEN_APRICORN_SAPLING.get(),
            CobbledBlocks.PINK_APRICORN_SAPLING.get(),
            CobbledBlocks.RED_APRICORN_SAPLING.get(),
            CobbledBlocks.WHITE_APRICORN_SAPLING.get(),
            CobbledBlocks.YELLOW_APRICORN_SAPLING.get(),
            CobbledBlocks.BLACK_APRICORN.get(),
            CobbledBlocks.BLUE_APRICORN.get(),
            CobbledBlocks.GREEN_APRICORN.get(),
            CobbledBlocks.PINK_APRICORN.get(),
            CobbledBlocks.RED_APRICORN.get(),
            CobbledBlocks.WHITE_APRICORN.get(),
            CobbledBlocks.YELLOW_APRICORN.get(),
            CobbledBlocks.HEALING_MACHINE.get())
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

    fun registerPokemonRenderer(context: EntityRendererProvider.Context): PokemonRenderer {
        LOGGER.info("Registering Pokémon renderer")
        PokemonModelRepository.initializeModels(context)
        return PokemonRenderer(context)
    }

    fun registerPokeBallRenderer(context: EntityRendererProvider.Context): PokeBallRenderer {
        LOGGER.info("Registering PokéBall renderer")
        PokeBallModelRepository.initializeModels(context)
        return PokeBallRenderer(context)
    }

    fun reloadCodedAssets() {
        BedrockAnimationRepository.clear()
        PokemonModelRepository.reload()
        PokeBallModelRepository.reload()
    }
}