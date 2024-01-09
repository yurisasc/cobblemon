/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

object CobblemonSounds : PlatformRegistry<Registry<SoundEvent>, RegistryKey<Registry<SoundEvent>>, SoundEvent>() {

    override val registry: Registry<SoundEvent> = Registries.SOUND_EVENT
    override val registryKey: RegistryKey<Registry<SoundEvent>> = RegistryKeys.SOUND_EVENT

    @JvmField
    val GUI_CLICK = this.create("gui.click")

    @JvmField
    val PC_ON = this.create("pc.on")
    @JvmField
    val PC_OFF = this.create("pc.off")
    @JvmField
    val PC_GRAB = this.create("pc.grab")
    @JvmField
    val PC_DROP = this.create("pc.drop")
    @JvmField
    val PC_RELEASE = this.create("pc.release")
    @JvmField
    val PC_CLICK = this.create("pc.click")

    @JvmField
    val HEALING_MACHINE_ACTIVE = this.create("healing_machine.active")

    @JvmField
    val POKE_BALL_CAPTURE_STARTED = this.create("poke_ball.capture_started")
    @JvmField
    val POKE_BALL_CAPTURE_SUCCEEDED = this.create("poke_ball.capture_succeeded")
    @JvmField
    val POKE_BALL_SHAKE = this.create("poke_ball.shake")
    @JvmField
    val POKE_BALL_OPEN = this.create("poke_ball.open")
    @JvmField
    val POKE_BALL_HIT = this.create("poke_ball.hit")
    @JvmField
    val POKE_BALL_SEND_OUT = this.create("poke_ball.send_out")
    @JvmField
    val POKE_BALL_RECALL = this.create("poke_ball.recall")

    @JvmField
    val ITEM_USE = this.create("item.use")
    @JvmField
    val CAN_EVOLVE = this.create("pokemon.can_evolve")
    @JvmField
    val EVOLVING = this.create("pokemon.evolving")

    @JvmField
    val PVN_BATTLE = this.create("battle.pvn.default")
    @JvmField
    val PVP_BATTLE = this.create("battle.pvp.default")
    @JvmField
    val PVW_BATTLE = this.create("battle.pvw.default")

    @JvmField
    val MEDICINE_HERB_USE = this.create("medicine_herb.use")
    @JvmField
    val MEDICINE_LIQUID_USE = this.create("medicine_liquid.use")
    @JvmField
    val MEDICINE_PILLS_USE = this.create("medicine_pills.use")
    @JvmField
    val MEDICINE_SPRAY_USE = this.create("medicine_spray.use")

    @JvmField
    val BERRY_HARVEST = this.create("berry.harvest")
    @JvmField
    val BERRY_EAT = this.create("berry.eat")

    @JvmField
    val MULCH_PLACE = this.create("mulch.place")
    @JvmField
    val MULCH_REMOVE = this.create("mulch.remove")

    @JvmField
    val FOSSIL_MACHINE_ACTIVATE = this.create("fossilmachine.activate")
    @JvmField
    val FOSSIL_MACHINE_ACTIVE_LOOP = this.create("fossilmachine.active_loop")
    @JvmField
    val FOSSIL_MACHINE_ASSEMBLE = this.create("fossilmachine.assemble")
    @JvmField
    val FOSSIL_MACHINE_DNA_FULL = this.create("fossilmachine.dna_full")
    @JvmField
    val FOSSIL_MACHINE_FINISHED = this.create("fossilmachine.finished")
    @JvmField
    val FOSSIL_MACHINE_INSERT_DNA = this.create("fossilmachine.insert_dna")
    @JvmField
    val FOSSIL_MACHINE_INSERT_DNA_SMALL = this.create("fossilmachine.insert_dna_small")
    @JvmField
    val FOSSIL_MACHINE_INSERT_FOSSIL = this.create("fossilmachine.insert_fossil")
    @JvmField
    val FOSSIL_MACHINE_RETRIEVE_FOSSIL = this.create("fossilmachine.retrieve_fossil")
    @JvmField
    val FOSSIL_MACHINE_RETRIEVE_POKEMON = this.create("fossilmachine.retrieve_pokemon")
    @JvmField
    val FOSSIL_MACHINE_UNPROTECTED = this.create("fossilmachine.unprotected")

    @JvmField
    val COIN_POUCH_BREAK = this.create("coin_pouch.break")
    @JvmField
    val COIN_POUCH_HIT = this.create("coin_pouch.hit")
    @JvmField
    val COIN_POUCH_STEP = this.create("coin_pouch.step")
    @JvmField
    val COIN_POUCH_PLACE = this.create("coin_pouch.place")
    @JvmField
    val TUMBLESTONE_BREAK = this.create("tumblestone.break")
    @JvmField
    val TUMBLESTONE_BLOCK_BREAK = this.create("tumblestone.block_break")
    @JvmField
    val TUMBLESTONE_HIT = this.create("tumblestone.hit")
    @JvmField
    val TUMBLESTONE_PLACE = this.create("tumblestone.place")
    @JvmField
    val TUMBLESTONE_STEP = this.create("tumblestone.step")

    @JvmField
    val GIMMIGHOUL_GIVE_ITEM_SMALL = this.create("gimmighoul.give_item_small")
    @JvmField
    val GIMMIGHOUL_REVEAL = this.create("gimmighoul.reveal")

    @JvmField
    val BERRY_BUSH_BREAK = this.create("berry_bush.break")
    @JvmField
    val BERRY_BUSH_PLACE = this.create("berry_bush.place")

    @JvmField
    val BIG_ROOT_BREAK = this.create("big_root.break")
    @JvmField
    val ENERGY_ROOT_PLACE = this.create("energy_root.place")

    @JvmField
    val MINT_BREAK = this.create("mint.break")
    @JvmField
    val MINT_PLACE = this.create("mint.place")

    @JvmField
    val MEDICINAL_LEEK_BREAK = this.create("medicinal_leek.break")
    @JvmField
    val MEDICINAL_LEEK_PLACE = this.create("medicinal_leek.plant")

    @JvmField
    val GILDED_CHEST_OPEN = this.create("gilded_chest.open")
    @JvmField
    val GILDED_CHEST_CLOSE = this.create("gilded_chest.close")
    @JvmField
    val GILDED_CHEST_STEP = this.create("gilded_chest.step")
    @JvmField
    val GILDED_CHEST_HIT = this.create("gilded_chest.hit")
    @JvmField
    val GILDED_CHEST_BREAK = this.create("gilded_chest.break")
    @JvmField
    val GILDED_CHEST_PLACE = this.create("gilded_chest.place")

    @JvmField
    val COIN_POUCH_SOUNDS = BlockSoundGroup(1f, 1f,
        COIN_POUCH_BREAK,
        COIN_POUCH_STEP,
        COIN_POUCH_PLACE,
        COIN_POUCH_HIT,
        COIN_POUCH_STEP
    )
    @JvmField
    val TUMBLESTONE_SOUNDS = BlockSoundGroup(1f, 1f,
        TUMBLESTONE_BREAK,
        TUMBLESTONE_STEP,
        TUMBLESTONE_PLACE,
        TUMBLESTONE_HIT,
        TUMBLESTONE_STEP
    )

    @JvmField
    val TUMBLESTONE_BLOCK_SOUNDS = BlockSoundGroup(1f, 1f,
        TUMBLESTONE_BLOCK_BREAK,
        TUMBLESTONE_STEP,
        TUMBLESTONE_PLACE,
        TUMBLESTONE_HIT,
        TUMBLESTONE_STEP
    )

    @JvmField
    val BERRY_BUSH_SOUNDS = BlockSoundGroup(1f, 1f,
        BERRY_BUSH_BREAK,
        SoundEvents.BLOCK_GRASS_STEP,
        BERRY_BUSH_PLACE,
        SoundEvents.BLOCK_GRASS_HIT,
        SoundEvents.BLOCK_GRASS_STEP
    )

    @JvmField
    val BIG_ROOT_SOUNDS = BlockSoundGroup(1f, 1f,
        BIG_ROOT_BREAK,
        SoundEvents.BLOCK_ROOTS_STEP,
        SoundEvents.BLOCK_ROOTS_PLACE,
        SoundEvents.BLOCK_ROOTS_HIT,
        SoundEvents.BLOCK_ROOTS_FALL
    )

    @JvmField
    val ENERGY_ROOT_SOUNDS = BlockSoundGroup(1f, 1f,
        SoundEvents.BLOCK_ROOTS_BREAK,
        SoundEvents.BLOCK_ROOTS_STEP,
        ENERGY_ROOT_PLACE,
        SoundEvents.BLOCK_ROOTS_HIT,
        SoundEvents.BLOCK_ROOTS_FALL
    )

    @JvmField
    val MEDICINAL_LEEK_SOUNDS = BlockSoundGroup(1f, 1f,
        MEDICINAL_LEEK_BREAK,
        SoundEvents.BLOCK_GRASS_STEP,
        MEDICINAL_LEEK_PLACE,
        SoundEvents.BLOCK_GRASS_HIT,
        SoundEvents.BLOCK_GRASS_FALL
    )

    @JvmField
    val MINT_SOUNDS = BlockSoundGroup(1f, 1f,
        MINT_BREAK,
        SoundEvents.BLOCK_GRASS_STEP,
        MINT_PLACE,
        SoundEvents.BLOCK_GRASS_HIT,
        SoundEvents.BLOCK_GRASS_FALL
    )

    @JvmField
    val GILDED_CHEST_SOUNDS = BlockSoundGroup(1f, 1f,
        GILDED_CHEST_BREAK,
        GILDED_CHEST_STEP,
        GILDED_CHEST_PLACE,
        GILDED_CHEST_HIT,
        GILDED_CHEST_STEP
    )

    private fun create(name: String): SoundEvent = this.create(name, SoundEvent.of(cobblemonResource(name)))
}