package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.client.storage.ClientParty
import com.cablemc.pokemoncobbled.common.net.messages.server.storage.pc.MovePCPokemonPacket
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

    init {
        this.setupMemberWidgets()
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
                    pokemon = pc.get(PCPosition(box, index)),
                    index = index,
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
                pokemon = party.get(partySlot),
                index = index,
                texture = texture,
                onPress = {
                    println("hi there you click")
                }
            ).also { widget ->
                this.addWidget(widget)
                this.partyWidgets.add(widget)
            }
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
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
            is PCBoxMemberWidget -> PCPosition(box, button.index)
            is PartyMemberWidget -> PartyPosition(button.index)
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
            this.selectedPosition = clickedPosition;
            return
        }

        // Handle movement within the PC.
        val selectedPokemon = when(this.selectedPosition) {
            is PCPosition -> pc.get(this.selectedPosition as PCPosition)
            is PartyPosition -> party.get(this.selectedPosition as PartyPosition)
            else -> null
        } ?: return

        if (this.selectedPosition is PCPosition && clickedPosition is PCPosition) {
            val packet = clickedPokemon?.let { SwapPCPokemonPacket(it.uuid, clickedPosition, selectedPokemon.uuid, this.selectedPosition as PCPosition) } ?:
                MovePCPokemonPacket(selectedPokemon.uuid, selectedPosition as PCPosition, clickedPosition)
            packet.sendToServer()
            this.selectedPosition = null
            this.setupMemberWidgets()
        } else if (this.selectedPosition is PCPosition && clickedPosition is PartyPosition) {

        } else if (this.selectedPosition is PartyPosition && clickedPosition is PCPosition) {

        }

        /*if (selectedPokemon != null) {
            if (clickedPokemon != null) {
                pc.swap(clickedPokemon.uuid, selectedPokemon.uuid)
            } else {
                pc.move(selectedPokemon.uuid, clickedPosition)
            }
            this.selectedPosition = null
            this.setupMemberWidgets()
        }*/
    }

}