package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.client.storage.ClientParty
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.SwapPCPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.party.MovePartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.party.SwapPartyPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePCPokemonPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePCPokemonToPartyPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePartyPokemonToPCPacket
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.SwapPCPokemonPacket
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class PCWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pcGui: PCGui,
    private val pc: ClientPC,
    private val party: ClientParty
) : SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("PCWidget")) {

    var selectedPosition: StorePosition? = null

    var box = 0
        set(value) {
            // If value is within min and max
            field = if (value > 0 && value < pc.boxes.size) {
                value
            }
            // If value is less than zero, wrap around to end.
            else if (value < 0) {
                pc.boxes.size - 1;
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
    }

    private fun setupMemberWidgets() {
        this.resetWidgets()
        var index = 0;

        // Box members
        for (row in 1..5) {
            for (col in 1..6) {
                PCBoxMemberWidget(
                    x = x + (col-1) * 29,
                    y = y + (row-1) * 29,
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

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        this.previewWidget.render(matrices, mouseX, mouseY, delta)
        this.pcWidgets.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }
        this.partyWidgets.forEach { widget -> widget.render(matrices, mouseX, mouseY, delta) }
    }

    private fun resetWidgets() {
        this.partyWidgets.forEach(this::removeWidget)
        this.partyWidgets.clear()

        this.pcWidgets.forEach(this::removeWidget)
        this.pcWidgets.clear()
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
            val packet = clickedPokemon?.let { SwapPCPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) } ?:
                MovePCPokemonPacket(selectedPokemon.uuid, selectedPosition as PCPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
        // PC -> Party
        else if (this.selectedPosition is PCPosition && clickedPosition is PartyPosition) {
            val packet = clickedPokemon?.let { SwapPCPartyPokemonPacket(clickedPokemon.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) } ?:
                MovePCPokemonToPartyPacket(selectedPokemon.uuid, this.selectedPosition as PCPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
        // Party -> PC
        else if (this.selectedPosition is PartyPosition && clickedPosition is PCPosition) {
            val packet = clickedPokemon?.let { SwapPCPartyPokemonPacket(selectedPokemon.uuid, this.selectedPosition as PartyPosition, clickedPokemon.uuid, clickedPosition) } ?:
                MovePartyPokemonToPCPacket(selectedPokemon.uuid, this.selectedPosition as PartyPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
        // Party -> Party
        else if (this.selectedPosition is PartyPosition && clickedPosition is PartyPosition) {
            val packet = clickedPokemon?.let { SwapPartyPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PartyPosition) } ?:
                MovePartyPokemonPacket(selectedPokemon.uuid, selectedPosition as PartyPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
        }
    }

}