package com.cablemc.pokemoncobbled.client.gui

import com.cablemc.pokemoncobbled.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.client.keybinding.HidePartyBinding
import com.cablemc.pokemoncobbled.client.util.PokemonSpriteProvider
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

class PartyOverlay(
    minecraft: Minecraft = Minecraft.getInstance()
) : Gui(minecraft) {
    var selectedSlot = 0
    val partyResource = ResourceLocation("pokemoncobbled", "party/background.png")
    val selectedResource = ResourceLocation("pokemoncobbled", "party/selected_slot.png")
    val screenExemptions: List<Class<out Screen>> = listOf(
        ChatScreen::class.java
    )

    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return
        }
        // Hiding if a Screen is open and not exempt
        if(minecraft.screen != null) {
            if(!screenExemptions.contains(minecraft.screen?.javaClass as Class<out Screen>))
                return
        }
        // Hiding if toggled via Keybind
        if(HidePartyBinding.shouldHide)
            return

        val party = PokemonCobbledClient.storage.myParty

        val pokemonSlotHeight = 20
        val panelX = 10
        val panelHeight = 6 + party.slots.size * pokemonSlotHeight
        val panelWidth = 26
        val panelY = minecraft.window.guiScaledHeight / 2 - panelHeight / 2

        blitk(
            poseStack = event.matrixStack,
            texture = partyResource,
            x = panelX - 2,
            y = panelY,
            height = panelHeight,
            width = panelWidth,
            alpha = 0.4
        )

        party.forEachIndexed { index, pokemon ->
            if (pokemon != null) {
                val spriteY = panelY + 3 + pokemonSlotHeight * index
                blitk(
                    poseStack = event.matrixStack,
                    texture = PokemonSpriteProvider.getSprite(pokemon),
                    x = panelX + 2,
                    y = spriteY,
                    height = pokemonSlotHeight,
                    width = pokemonSlotHeight
                )
                if (PokemonCobbledClient.storage.selectedSlot == index) {
                    blitk(
                        poseStack = event.matrixStack,
                        texture = selectedResource,
                        x = panelX,
                        y = spriteY - 2,
                        height = pokemonSlotHeight + 2,
                        width = pokemonSlotHeight + 2
                    )
                }
            }
        }
    }
}