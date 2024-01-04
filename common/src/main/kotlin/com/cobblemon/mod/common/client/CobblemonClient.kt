/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.berry.Berries
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.CobblemonClientImplementation
import com.cobblemon.mod.common.CobblemonEntities
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.scheduling.ClientTaskTracker
import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.client.battle.ClientBattle
import com.cobblemon.mod.common.client.gui.PartyOverlay
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay
import com.cobblemon.mod.common.client.particle.BedrockParticleEffectRepository
import com.cobblemon.mod.common.client.render.block.BerryBlockRenderer
import com.cobblemon.mod.common.client.render.block.FossilCompartmentRenderer
import com.cobblemon.mod.common.client.render.block.FossilTubeRenderer
import com.cobblemon.mod.common.client.render.block.HealingMachineRenderer
import com.cobblemon.mod.common.client.render.boat.CobblemonBoatRenderer
import com.cobblemon.mod.common.client.render.item.CobblemonBuiltinItemRendererRegistry
import com.cobblemon.mod.common.client.render.item.PokemonItemRenderer
import com.cobblemon.mod.common.client.render.layer.PokemonOnShoulderRenderer
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokemonModelRepository
import com.cobblemon.mod.common.client.render.pokeball.PokeBallRenderer
import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer
import com.cobblemon.mod.common.client.sound.battle.BattleMusicController
import com.cobblemon.mod.common.client.starter.ClientPlayerData
import com.cobblemon.mod.common.client.storage.ClientStorageManager
import com.cobblemon.mod.common.client.trade.ClientTrade
import com.cobblemon.mod.common.data.CobblemonDataProvider
import com.cobblemon.mod.common.entity.boat.CobblemonBoatType
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.client.render.models.blockbench.repository.FossilModelRepository
import com.cobblemon.mod.common.platform.events.PlatformEvents
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.asTranslated
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.model.BoatEntityModel
import net.minecraft.client.render.entity.model.ChestBoatEntityModel
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Language

object CobblemonClient {

    lateinit var implementation: CobblemonClientImplementation
    val storage = ClientStorageManager()
    var trade: ClientTrade? = null
    var battle: ClientBattle? = null
    var clientPlayerData = ClientPlayerData()
    /** If true then we won't bother them anymore about choosing a starter even if it's a thing they can do. */
    var checkedStarterScreen = false
    var requests = ClientPlayerActionRequests()


    val overlay: PartyOverlay by lazy { PartyOverlay() }
    val battleOverlay: BattleOverlay by lazy { BattleOverlay() }

    fun onLogin() {
        clientPlayerData = ClientPlayerData()
        requests = ClientPlayerActionRequests()
        storage.onLogin()
        CobblemonDataProvider.canReload = false
    }

    fun onLogout() {
        storage.onLogout()
        battle = null
        battleOverlay.onLogout()
        ClientTaskTracker.clear()
        checkedStarterScreen = false
        CobblemonDataProvider.canReload = true
    }

    fun initialize(implementation: CobblemonClientImplementation) {
        LOGGER.info("Initializing Cobblemon client")
        this.implementation = implementation

        PlatformEvents.CLIENT_PLAYER_LOGIN.subscribe { onLogin() }
        PlatformEvents.CLIENT_PLAYER_LOGOUT.subscribe { onLogout() }

        this.registerBlockEntityRenderers()
        registerBlockRenderTypes()
        //registerColors()
        registerFlywheelRenderers()
        this.registerEntityRenderers()
        Berries.observable.subscribe {
            BerryModelRepository.patchModels()
        }

        LOGGER.info("Registering custom BuiltinItemRenderers")
        CobblemonBuiltinItemRendererRegistry.register(CobblemonItems.POKEMON_MODEL, PokemonItemRenderer())

        PlatformEvents.CLIENT_ITEM_TOOLTIP.subscribe { event ->
            val stack = event.stack
            val lines = event.lines
            val size = lines.size
            @Suppress("DEPRECATION")
            if (stack.item.registryEntry.key.isPresent && stack.item.registryEntry.key.get().value.namespace == Cobblemon.MODID) {
                if (stack.nbt?.getBoolean(DataKeys.HIDE_TOOLTIP) == true) {
                    return@subscribe
                }
                val language = Language.getInstance()
                val key = this.baseLangKeyForItem(stack)
                val offset = if (size > 1) 1 else 0
                if (language.hasTranslation(key)) {
                    lines.add(size - offset, key.asTranslated().gray())
                }
                var i = 1
                var listKey = "${key}_$i"
                while(language.hasTranslation(listKey)) {
                    lines.add(size - offset, listKey.asTranslated().gray())
                    listKey = "${key}_${++i}"
                }
            }
        }
    }

    fun registerFlywheelRenderers() {
//        InstancedRenderRegistry
//            .configure(CobblemonBlockEntities.BERRY)
//            .alwaysSkipRender()
//            .factory(::BerryEntityInstance)
//            .apply()
    }

    /*
    fun registerColors() {
        this.implementation.registerBlockColors(BlockColorProvider { _, _, _, _ ->
            return@BlockColorProvider 0xE0A33A
        }, CobblemonBlocks.APRICORN_LEAVES)
        this.implementation.registerItemColors(ItemColorProvider { _, _ ->
            return@ItemColorProvider 0xE0A33A
        }, CobblemonItems.APRICORN_LEAVES)
    }
    */

    private fun registerBlockRenderTypes() {

        this.implementation.registerBlockRenderType(RenderLayer.getCutoutMipped(), CobblemonBlocks.APRICORN_LEAVES)

        this.implementation.registerBlockRenderType(
            RenderLayer.getCutout(),
            CobblemonBlocks.FOSSIL_COMPARTMENT,
            CobblemonBlocks.APRICORN_DOOR,
            CobblemonBlocks.APRICORN_TRAPDOOR,
            CobblemonBlocks.APRICORN_SIGN,
            CobblemonBlocks.APRICORN_WALL_SIGN,
            CobblemonBlocks.APRICORN_HANGING_SIGN,
            CobblemonBlocks.APRICORN_WALL_HANGING_SIGN,
            CobblemonBlocks.BLACK_APRICORN_SAPLING,
            CobblemonBlocks.BLUE_APRICORN_SAPLING,
            CobblemonBlocks.GREEN_APRICORN_SAPLING,
            CobblemonBlocks.PINK_APRICORN_SAPLING,
            CobblemonBlocks.RED_APRICORN_SAPLING,
            CobblemonBlocks.WHITE_APRICORN_SAPLING,
            CobblemonBlocks.YELLOW_APRICORN_SAPLING,
            CobblemonBlocks.BLACK_APRICORN,
            CobblemonBlocks.BLUE_APRICORN,
            CobblemonBlocks.GREEN_APRICORN,
            CobblemonBlocks.PINK_APRICORN,
            CobblemonBlocks.RED_APRICORN,
            CobblemonBlocks.WHITE_APRICORN,
            CobblemonBlocks.YELLOW_APRICORN,
            CobblemonBlocks.HEALING_MACHINE,
            CobblemonBlocks.MEDICINAL_LEEK,
            CobblemonBlocks.HEALING_MACHINE,
            CobblemonBlocks.RED_MINT,
            CobblemonBlocks.BLUE_MINT,
            CobblemonBlocks.CYAN_MINT,
            CobblemonBlocks.PINK_MINT,
            CobblemonBlocks.GREEN_MINT,
            CobblemonBlocks.WHITE_MINT,
            CobblemonBlocks.PASTURE,
            CobblemonBlocks.ENERGY_ROOT,
            CobblemonBlocks.BIG_ROOT,
            CobblemonBlocks.REVIVAL_HERB,
            CobblemonBlocks.VIVICHOKE_SEEDS,
            CobblemonBlocks.PEP_UP_FLOWER,
            CobblemonBlocks.POTTED_PEP_UP_FLOWER,
            CobblemonBlocks.REVIVAL_HERB,
            *CobblemonBlocks.berries().values.toTypedArray(),
            CobblemonBlocks.POTTED_PEP_UP_FLOWER,
            CobblemonBlocks.FOSSIL_TUBE,
            CobblemonBlocks.SMALL_BUDDING_TUMBLESTONE,
            CobblemonBlocks.MEDIUM_BUDDING_TUMBLESTONE,
            CobblemonBlocks.LARGE_BUDDING_TUMBLESTONE,
            CobblemonBlocks.TUMBLESTONE_CLUSTER,
            CobblemonBlocks.SMALL_BUDDING_BLACK_TUMBLESTONE,
            CobblemonBlocks.MEDIUM_BUDDING_BLACK_TUMBLESTONE,
            CobblemonBlocks.LARGE_BUDDING_BLACK_TUMBLESTONE,
            CobblemonBlocks.BLACK_TUMBLESTONE_CLUSTER,
            CobblemonBlocks.SMALL_BUDDING_SKY_TUMBLESTONE,
            CobblemonBlocks.MEDIUM_BUDDING_SKY_TUMBLESTONE,
            CobblemonBlocks.LARGE_BUDDING_SKY_TUMBLESTONE,
            CobblemonBlocks.SKY_TUMBLESTONE_CLUSTER,
        )

        this.createBoatModelLayers()
    }

    fun beforeChatRender(context: DrawContext, partialDeltaTicks: Float) {
//        ClientTaskTracker.update(partialDeltaTicks / 20f)
        if (battle == null) {
            overlay.render(context, partialDeltaTicks)
        } else {
            battleOverlay.render(context, partialDeltaTicks)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun onAddLayer(skinMap: Map<String, EntityRenderer<out PlayerEntity>>?) {
        var renderer: LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>>? = skinMap?.get("default") as LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>>
        renderer?.addFeature(PokemonOnShoulderRenderer(renderer))
        renderer = skinMap["slim"] as LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>>?
        renderer?.addFeature(PokemonOnShoulderRenderer(renderer))
    }

    private fun registerBlockEntityRenderers() {
        this.implementation.registerBlockEntityRenderer(CobblemonBlockEntities.HEALING_MACHINE, ::HealingMachineRenderer)
        this.implementation.registerBlockEntityRenderer(CobblemonBlockEntities.BERRY, ::BerryBlockRenderer)
        this.implementation.registerBlockEntityRenderer(CobblemonBlockEntities.SIGN, ::SignBlockEntityRenderer)
        this.implementation.registerBlockEntityRenderer(CobblemonBlockEntities.HANGING_SIGN, ::HangingSignBlockEntityRenderer)
        this.implementation.registerBlockEntityRenderer(CobblemonBlockEntities.FOSSIL_COMPARTMENT, ::FossilCompartmentRenderer)
        this.implementation.registerBlockEntityRenderer(CobblemonBlockEntities.FOSSIL_TUBE, ::FossilTubeRenderer)
    }

    private fun registerEntityRenderers() {
        LOGGER.info("Registering Pokémon renderer")
        this.implementation.registerEntityRenderer(CobblemonEntities.POKEMON, ::PokemonRenderer)
        LOGGER.info("Registering PokéBall renderer")
        this.implementation.registerEntityRenderer(CobblemonEntities.EMPTY_POKEBALL, ::PokeBallRenderer)
        LOGGER.info("Registering Boat renderer")
        this.implementation.registerEntityRenderer(CobblemonEntities.BOAT) { ctx -> CobblemonBoatRenderer(ctx, false) }
        LOGGER.info("Registering Boat with Chest renderer")
        this.implementation.registerEntityRenderer(CobblemonEntities.CHEST_BOAT) { ctx -> CobblemonBoatRenderer(ctx, true) }
    }

    fun reloadCodedAssets(resourceManager: ResourceManager) {
        LOGGER.info("Loading assets...")
        BedrockParticleEffectRepository.loadEffects(resourceManager)
        BedrockAnimationRepository.loadAnimations(
            resourceManager = resourceManager,
            directories = PokemonModelRepository.animationDirectories + PokeBallModelRepository.animationDirectories + FossilModelRepository.animationDirectories
        )
        PokemonModelRepository.reload(resourceManager)
        PokeBallModelRepository.reload(resourceManager)
        BerryModelRepository.reload(resourceManager)
        FossilModelRepository.reload(resourceManager)
        LOGGER.info("Loaded assets")
    }

    fun endBattle() {
        battle = null
        battleOverlay.lastKnownBattle = null
        BattleMusicController.endMusic()
    }

    private fun baseLangKeyForItem(stack: ItemStack): String {
        if (stack.item is PokeBallItem) {
            val asPokeball = stack.item as PokeBallItem
            return "item.${asPokeball.pokeBall.name.namespace}.${asPokeball.pokeBall.name.path}.tooltip"
        }
        return "${stack.translationKey}.tooltip"
    }

    private fun createBoatModelLayers() {
        CobblemonBoatType.values().forEach { type ->
            this.implementation.registerLayer(CobblemonBoatRenderer.createBoatModelLayer(type, false), BoatEntityModel::getTexturedModelData)
            this.implementation.registerLayer(CobblemonBoatRenderer.createBoatModelLayer(type, true), ChestBoatEntityModel::getTexturedModelData)
        }
    }

}
