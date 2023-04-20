/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.entity

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.api.scheduling.afterOnMain
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonServerDelegate
import com.cobblemon.mod.common.net.serverhandling.storage.SendOutPokemonHandler
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.DataKeys
import com.cobblemon.mod.common.util.getBlockStates
import com.cobblemon.mod.common.util.getBlockStatesWithPos
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.toVec3d
import java.util.UUID
import kotlin.math.ceil
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.EntityPose
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.ArrayVoxelShape
import net.minecraft.world.World

// TODO PASTURE rename pokemon tether block to pasture block, here and elsewhere
class PokemonTetherBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(CobblemonBlockEntities.POKEMON_TETHER, pos, state) {
    open class Tethering(val pos: BlockPos, val playerId: UUID, val tetheringId: UUID, val pokemonId: UUID, val pcId: UUID, val entityId: Int) {
        fun getPokemon() = Cobblemon.storage.getPC(pcId)[pokemonId]
        open fun getMaxRoamDistance() = 64.0
        // TODO PASTURE rework to be min max pos check rather than roam distance
        open fun canRoamTo(pos: BlockPos) = pos.isWithinDistance(this.pos, getMaxRoamDistance())
    }

    companion object {
        internal val TICKER = BlockEntityTicker<PokemonTetherBlockEntity> { world, _, _, blockEntity ->
            blockEntity.ticksUntilCheck--
            if (blockEntity.ticksUntilCheck <= 0) {
                blockEntity.checkPokemon()
            }
        }
    }

    // TODO PASTURE update the model probably, copy from PC stuff

    var ticksUntilCheck = Cobblemon.config.pastureBlockUpdateTicks
    val tetheredPokemon = mutableListOf<Tethering>()

    fun getMaxTethered() = Cobblemon.config.defaultPasturedPokemonLimit

    fun tether(player: ServerPlayerEntity, pokemon: Pokemon, directionToBehind: Direction): Boolean {
        val world = world ?: return false
        val entity = PokemonEntity(world, pokemon = pokemon)
        entity.calculateDimensions()
        val width = entity.boundingBox.xLength

        val idealPlace = pos.add(directionToBehind.vector.multiply(ceil(width).toInt() + 1))
        var box = entity.getDimensions(EntityPose.STANDING).getBoxAt(idealPlace.toCenterPos().subtract(0.0, 0.5, 0.0))

        for (i in 0..5) {
            box = box.offset(directionToBehind.vector.x.toDouble(), 0.0, directionToBehind.vector.z.toDouble())
            val fixedPosition = makeSuitableY(world, idealPlace.add(directionToBehind.vector), entity, box)
            if (fixedPosition != null) {
                entity.setPosition(fixedPosition.toCenterPos().subtract(0.0, 0.5, 0.0))
                val pc = Cobblemon.storage.getPC(player.uuid)
                entity.beamModeEmitter.set(1)
                afterOnMain(seconds = SendOutPokemonHandler.SEND_OUT_DURATION) {
                    entity.beamModeEmitter.set(0)
                }
                if (world.spawnEntity(entity)) {
                    val tethering = Tethering(
                        pos = fixedPosition,
                        playerId = player.uuid,
                        tetheringId = UUID.randomUUID(),
                        pokemonId = pokemon.uuid,
                        pcId = pc.uuid,
                        entityId = entity.id
                    )
                    pokemon.tetheringId = tethering.tetheringId
                    tetheredPokemon.add(tethering)
                    entity.tethering = tethering
                    markDirty()
                    return true
                } else {
                    Cobblemon.LOGGER.warn("Couldn't spawn pastured Pokémon for some reason")
                }
                break
            }
        }

        return false
    }

    override fun markRemoved() {
        super.markRemoved()
        releaseAllPokemon()
    }


    fun isSafeFloor(world: World, pos: BlockPos, entity: PokemonEntity): Boolean {
        val state = world.getBlockState(pos)
        return if (state.isAir) {
            false
        } else if (state.isSolidBlock(world, pos)) {
            true
        } else if ((entity.behaviour.moving.swim.canWalkOnWater || entity.behaviour.moving.swim.canSwimInWater) && state.fluidState.isIn(FluidTags.WATER)) {
            true
        } else if ((entity.behaviour.moving.swim.canWalkOnLava || entity.behaviour.moving.swim.canSwimInLava) && state.fluidState.isIn(FluidTags.LAVA)) {
            true
        } else {
            false
        }
    }

    // TODO PASTURE place the tether block like this: https://gyazo.com/7c163bccfde238688e9a2c600c27aace
    // You'll find you can't place pokemon into the tether. It's because of this function somehow
    fun makeSuitableY(world: World, pos: BlockPos, entity: PokemonEntity, box: Box): BlockPos? {
        if (world.canCollide(entity, box)) {
            for (i in 1..3) {
                val newBox = box.offset(0.5, i.toDouble(), 0.5)
                if (!world.canCollide(entity, newBox) && isSafeFloor(world, pos.add(0, i, 0), entity)) {
                    return pos.add(0, i + 1, 0)
                }
            }
        } else {
            for (i in 1..3) {
                val newBox = box.offset(0.5, -i.toDouble(), 0.5)
                if (!world.canCollide(entity, newBox) && isSafeFloor(world, pos.add(0, -i, 0), entity)) {
                    return pos.add(0, -i + 1, 0)
                }
            }
        }

        return null
    }

    fun checkPokemon() {
        val deadLinks = mutableListOf<UUID>()
        tetheredPokemon.forEach {
            val pokemon = it.getPokemon()
            if (pokemon == null) {
                deadLinks.add(it.pokemonId)
            } else if (pokemon.tetheringId == null || pokemon.tetheringId != it.tetheringId) {
                deadLinks.add(it.pokemonId)
            }
        }
        deadLinks.forEach(::releasePokemon)
        ticksUntilCheck = Cobblemon.config.pastureBlockUpdateTicks
        markDirty()
    }

    fun releaseAllPokemon() {
        tetheredPokemon.toList().forEach { releasePokemon(it.pokemonId) }
        markDirty()
    }

    fun releasePokemon(pokemonId: UUID) {
        val tethering = tetheredPokemon.find { it.pokemonId == pokemonId } ?: return
        tethering.getPokemon()?.tetheringId = null
        tetheredPokemon.remove(tethering)
        markDirty()
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        val list = nbt.getList(DataKeys.TETHER_POKEMON, NbtCompound.COMPOUND_TYPE.toInt())
        for (tetheringNBT in list) {
            tetheringNBT as NbtCompound
            val tetheringId = tetheringNBT.getUuid(DataKeys.TETHERING_ID)
            val pokemonId = tetheringNBT.getUuid(DataKeys.POKEMON_UUID)
            val pcId = tetheringNBT.getUuid(DataKeys.PC_ID)
            val playerId = tetheringNBT.getUuid(DataKeys.TETHERING_PLAYER_ID)
            val entityId = tetheringNBT.getInt(DataKeys.TETHERING_ENTITY_ID)
            tetheredPokemon.add(Tethering(pos = pos, playerId = playerId, tetheringId = tetheringId, pokemonId = pokemonId, pcId = pcId, entityId = entityId))
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        val list = NbtList()
        for (tethering in tetheredPokemon) {
            val tetheringNBT = NbtCompound()
            tetheringNBT.putUuid(DataKeys.TETHERING_ID, tethering.tetheringId)
            tetheringNBT.putUuid(DataKeys.TETHERING_PLAYER_ID, tethering.playerId)
            tetheringNBT.putUuid(DataKeys.POKEMON_UUID, tethering.pokemonId)
            tetheringNBT.putUuid(DataKeys.PC_ID, tethering.pcId)
            tetheringNBT.putInt(DataKeys.TETHERING_ENTITY_ID, tethering.entityId)
            list.add(tetheringNBT)
        }
        nbt.put(DataKeys.TETHER_POKEMON, list)
    }
}