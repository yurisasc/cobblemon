/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.forge

import com.cobblemon.mod.common.*
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.forge.net.CobblemonForgeNetworkDelegate
import com.cobblemon.mod.forge.permission.ForgePermissionValidator
import dev.architectury.platform.forge.EventBuses
import net.minecraftforge.common.ForgeMod
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.ToolActions
import net.minecraftforge.event.CreativeModeTabEvent
import net.minecraftforge.event.OnDatapackSyncEvent
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.level.BlockEvent
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent
import java.util.*

@Mod(Cobblemon.MODID)
class CobblemonForge : CobblemonImplementation {

    init {
        this.registerPermissionValidator()
        this.registerSoundEvents()
        this.registerBlocks()
        this.registerItems()
        this.registerEntityTypes()
        this.registerEntityAttributes()
        this.registerBlockEntityTypes()
        with(FMLJavaModLoadingContext.get().modEventBus) {
            EventBuses.registerModEventBus(Cobblemon.MODID, this)
            addListener(this@CobblemonForge::initialize)
            addListener(this@CobblemonForge::serverInit)
            CobblemonNetwork.networkDelegate = CobblemonForgeNetworkDelegate

            Cobblemon.preinitialize(this@CobblemonForge)

            // TODO: Make listener for BiomeLoadingEvent to register feature to biomes
        }
        with(MinecraftForge.EVENT_BUS) {
            addListener(this@CobblemonForge::onDataPackSync)
            addListener(this@CobblemonForge::onLogin)
            addListener(this@CobblemonForge::onLogout)
            addListener(this@CobblemonForge::handleBlockStripping)
        }

    }

    fun serverInit(event: FMLDedicatedServerSetupEvent) {
    }

    fun initialize(event: FMLCommonSetupEvent) {
        Cobblemon.LOGGER.info("Initializing...")
        Cobblemon.initialize()
    }

    private val hasBeenSynced = hashSetOf<UUID>()

    fun onDataPackSync(event: OnDatapackSyncEvent) {
        Cobblemon.dataProvider.sync(event.player ?: return)
    }

    fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        this.hasBeenSynced.add(event.entity.uuid)
    }

    fun onLogout(event: PlayerEvent.PlayerLoggedOutEvent) {
        this.hasBeenSynced.remove(event.entity.uuid)
    }

    override fun isModInstalled(id: String) = ModList.get().isLoaded(id)

    override fun registerPermissionValidator() {
        Cobblemon.permissionValidator = ForgePermissionValidator
    }

    override fun registerSoundEvents() {
        FMLJavaModLoadingContext.get().modEventBus.addListener<RegisterEvent> { event ->
            event.register(CobblemonSounds.registryKey) { helper ->
                CobblemonSounds.register { identifier, sounds -> helper.register(identifier, sounds) }
            }
        }
    }

    override fun registerBlocks() {
        FMLJavaModLoadingContext.get().modEventBus.addListener<RegisterEvent> { event ->
            event.register(CobblemonBlocks.registryKey) { helper ->
                CobblemonBlocks.register { identifier, block -> helper.register(identifier, block) }
            }
        }
    }

    private fun handleBlockStripping(e: BlockEvent.BlockToolModificationEvent) {
        if (e.toolAction == ToolActions.AXE_STRIP) {
            val start = e.state.block
            val result = CobblemonBlocks.strippedBlocks()[start] ?: return
            e.finalState = result.getStateWithProperties(e.state)
        }
    }

    override fun registerItems() {
        with(FMLJavaModLoadingContext.get().modEventBus) {
            addListener<RegisterEvent> { event ->
                event.register(CobblemonItems.registryKey) { helper ->
                    CobblemonItems.register { identifier, item -> helper.register(identifier, item) }
                }
            }
            // ToDo sort out ordering being insertion
            addListener<CreativeModeTabEvent.Register> { event ->
                CobblemonItemGroups.register { provider ->
                    Cobblemon.LOGGER.info("Registered {} tab", provider.identifier.toString())
                    event.registerCreativeModeTab(provider.identifier) { builder ->
                        builder.displayName(provider.displayName)
                        builder.icon(provider.icon)
                    }
                }
            }
            addListener<CreativeModeTabEvent.BuildContents> { event ->
                CobblemonItems.registerToItemGroups { group, item ->
                    if (event.tab == group) {
                        event.add(item)
                    }
                }
            }
        }
    }

    override fun registerEntityTypes() {
        FMLJavaModLoadingContext.get().modEventBus.addListener<RegisterEvent> { event ->
            event.register(CobblemonEntities.registryKey) { helper ->
                CobblemonEntities.register { identifier, type -> helper.register(identifier, type) }
            }
        }
    }

    override fun registerEntityAttributes() {
        FMLJavaModLoadingContext.get().modEventBus.addListener<EntityAttributeCreationEvent> { event ->
            event.put(
                CobblemonEntities.POKEMON,
                PokemonEntity.createAttributes()
                    .add(ForgeMod.ENTITY_GRAVITY.get())
                    .add(ForgeMod.NAMETAG_DISTANCE.get())
                    .add(ForgeMod.SWIM_SPEED.get())
                    .build()
            )
        }
    }

    override fun registerBlockEntityTypes() {
        FMLJavaModLoadingContext.get().modEventBus.addListener<RegisterEvent> { event ->
            event.register(CobblemonBlockEntities.registryKey) { helper ->
                CobblemonBlockEntities.register { identifier, type -> helper.register(identifier, type) }
            }
        }
    }
}