/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.pc

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.api.storage.StorePosition
import com.cablemc.pokemod.common.api.storage.party.PartyPosition
import com.cablemc.pokemod.common.api.storage.pc.PCPosition
import com.cablemc.pokemod.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemod.common.client.settings.ServerSettings
import com.cablemc.pokemod.common.client.storage.ClientPC
import com.cablemc.pokemod.common.client.storage.ClientParty
import com.cablemc.pokemod.common.net.messages.server.storage.SwapPCPartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.party.ReleasePartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePCPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.ReleasePCPokemonPacket
import com.cablemc.pokemod.common.net.messages.server.storage.pc.SwapPCPokemonPacket
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

class PCWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pcGui: PCGui,
    private val pc: ClientPC,
    private val party: ClientParty
) : SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("PCWidget")) {

    companion object {
        val selectedResource = pokemodResource("ui/pc/pc_selected.png")
    }

    var selectedPosition: StorePosition? = null

    var box = 0
        set(value) {
            // If value is within min and max
            field = if (value > 0 && value < pc.boxes.size) {
                value
            }
            // If value is less than zero, wrap around to end.
            else if (value < 0) {
                pc.boxes.size - 1
            }
            // Else it's greater than max, wrap around to start.
            else {
                0
            }
            this.setupMemberWidgets()
        }

    private val partyWidgets = arrayListOf<PartyMemberWidget>()
    private val pcWidgets = arrayListOf<PCBoxMemberWidget>()
    private val previewWidget: PCPreviewSelectedWidget
    private val trashWidget: PCTrashWidget

    fun canDeleteSelected(): Boolean {
        return !(selectedPosition is PartyPosition && party.filterNotNull().size <= 1) && selectedPosition != null
    }

    init {
        this.setupMemberWidgets()

        this.previewWidget = PCPreviewSelectedWidget(
            pX = x - 105,
            pY = y,
            pWidth = 60,
            pHeight = 70,
            baseScale = 1.5f,
            parent = this,
            pc = pc,
            party = party
        ).also {
            this.addWidget(it)
        }

        this.trashWidget = PCTrashWidget(
            x = x - 22,
            y = y + 150,
            parent = this,
            pc = pc,
            onPress = {
                if (canDeleteSelected()) {
                    val position = selectedPosition ?: return@PCTrashWidget
                    val pokemon = getSelectedPokemon() ?: return@PCTrashWidget

                    val packet = when (position) {
                        is PartyPosition -> ReleasePartyPokemonPacket(pokemon.uuid, position)
                        is PCPosition -> ReleasePCPokemonPacket(pokemon.uuid, position)
                        else -> return@PCTrashWidget
                    }

                    PokemodNetwork.sendToServer(packet)
                    selectedPosition = null
                }
            }
        ).also {
            addWidget(it)
        }
    }

    private fun setupMemberWidgets() {
        this.resetWidgets()
        var index = 0

        // Box members
        for (row in 1..5) {
            for (col in 1..6) {
                PCBoxMemberWidget(
                    x = x + (col-1) * 29,
                    y = y + (row-1) * 29,
                    parent = this,
                    pcGui = pcGui,
                    pc = pc,
                    position = PCPosition(box, index),
                    onPress = { this.onPokemonPressed(it) }
                ).also {  widget ->
                    this.addWidget(widget)
                    this.pcWidgets.add(widget)
                }
                index++
            }
        }

        // Party slots
        for (partySlot in 0..5) {
            val texture = when(partySlot) {
                0 -> PartyMemberWidget.slotOneResource
                5 -> PartyMemberWidget.slotSixResource
                else -> PartyMemberWidget.slotTwoThroughFiveResource
            }

            PartyMemberWidget(
                x = x + 185,
                y = (y - 19) + partySlot * 31,
                parent = this,
                pcGui = pcGui,
                pc = pc,
                party = party,
                position = PartyPosition(partySlot),
                texture = texture,
                onPress = { this.onPokemonPressed(it) }
            ).also { widget ->
                this.addWidget(widget)
                this.partyWidgets.add(widget)
            }
        }
    }

    fun getSelectedPokemon(): Pokemon? {
        val selectedPosition = this.selectedPosition ?: return null
        return when (selectedPosition) {
            is PCPosition -> pc.get(selectedPosition)
            is PartyPosition -> party.get(selectedPosition)
            else -> null
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.previewWidget.render(matrices, mouseX, mouseY, delta)
        this.pcWidgets.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }
        this.partyWidgets.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }
        this.trashWidget.render(matrices, mouseX, mouseY, delta)
    }

    private fun resetWidgets() {
        this.partyWidgets.forEach(this::removeWidget)
        this.partyWidgets.clear()

        this.pcWidgets.forEach(this::removeWidget)
        this.pcWidgets.clear()
    }
    
    fun clickSound() {
        MinecraftClient.getInstance().soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

    private fun onPokemonPressed(button: ButtonWidget) {
        // Only use on member widgets
        val clickedPosition = when(button) {
            is PCBoxMemberWidget -> button.position
            is PartyMemberWidget -> button.position
            else -> return
        }

        // Clicking on itself, so unselect position.
        if (this.selectedPosition != null && this.selectedPosition == clickedPosition) {
            this.selectedPosition = null
            clickSound()
            return
        }

        // Check for selecting a Pokémon.
        val clickedPokemon = when(button) {
            is PCBoxMemberWidget -> pc.get(clickedPosition as PCPosition)
            is PartyMemberWidget -> party.get(clickedPosition as PartyPosition)
            else -> null
        }
        if (this.selectedPosition == null && clickedPokemon != null) {
            this.selectedPosition = clickedPosition
            clickSound()
            return
        }

        // Handle movement within the PC.
        val selectedPokemon = when(this.selectedPosition) {
            is PCPosition -> pc.get(this.selectedPosition as PCPosition)
            is PartyPosition -> party.get(this.selectedPosition as PartyPosition)
            else -> null
        } ?: return

        // PC -> PC
        if (this.selectedPosition is PCPosition && clickedPosition is PCPosition) {
            val packet = clickedPokemon?.let { SwapPCPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) }
                ?: MovePCPokemonPacket(selectedPokemon.uuid, selectedPosition as PCPosition, clickedPosition)
            packet.sendToServer()
            clickSound()
            this.selectedPosition = null
        }
        // PC -> Party
        else if (this.selectedPosition is PCPosition && clickedPosition is PartyPosition) {
            val packet = clickedPokemon?.let { SwapPCPartyPokemonPacket(clickedPokemon.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) }
                ?: MovePCPokemonToPartyPacket(selectedPokemon.uuid, this.selectedPosition as PCPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
        // Party -> PC
        else if (this.selectedPosition is PartyPosition && clickedPosition is PCPosition) {
            if (ServerSettings.preventCompletePartyDeposit && this.party.filterNotNull().size == 1 && clickedPokemon == null) {
                return
            }
            val packet = clickedPokemon?.let { SwapPCPartyPokemonPacket(selectedPokemon.uuid, this.selectedPosition as PartyPosition, clickedPokemon.uuid, clickedPosition) }
                ?: MovePartyPokemonToPCPacket(selectedPokemon.uuid, this.selectedPosition as PartyPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
        // Party -> Party
        else if (this.selectedPosition is PartyPosition && clickedPosition is PartyPosition) {
            val packet = clickedPokemon?.let { SwapPartyPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PartyPosition) }
                ?: MovePartyPokemonPacket(selectedPokemon.uuid, selectedPosition as PartyPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
    }
}