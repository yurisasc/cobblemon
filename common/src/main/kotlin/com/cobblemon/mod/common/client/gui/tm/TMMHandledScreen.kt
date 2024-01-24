package com.cobblemon.mod.common.client.gui.tm

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.CobblemonNetwork.sendPacketToServer
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.gui.ColourLibrary
import com.cobblemon.mod.common.api.gui.MultiLineLabelK
import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachineRecipe
import com.cobblemon.mod.common.api.types.ElementalType
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.ExitButton
import com.cobblemon.mod.common.client.gui.MoveCategoryIcon
import com.cobblemon.mod.common.client.gui.TypeIcon
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.renderScaledGuiItemIcon
import com.cobblemon.mod.common.gui.TMMScreenHandler
import com.cobblemon.mod.common.net.messages.client.ui.CraftBlankTMPacket
import com.cobblemon.mod.common.net.messages.client.ui.CraftTMPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.activestate.PokemonState
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class TMMHandledScreen(
    val handler: TMMScreenHandler,
    val inventory: PlayerInventory,
    title: Text?
) : HandledScreen<TMMScreenHandler>(handler, inventory, title), ScreenHandlerListener {

    companion object {
        val TEXTURE_HEIGHT = 222
        val TEXTURE_WIDTH = 254

        val TYPE_MENU_MODE = 1
        val TM_BROWSING_MODE = 2

        val TYPE_SELECTION_BASE = cobblemonResource("textures/gui/tm/type_selection_base.png")
        val TM_SELECTION_BASE = cobblemonResource("textures/gui/tm/tm_selection_base.png")
        val TM_SELECTION_BORDER = cobblemonResource("textures/gui/tm/tm_selection_border.png")
        val TM_SELECTION_LISTING = cobblemonResource("textures/gui/tm/tm_selection_listing.png")
        val EJECT_BUTTON_SMALL = cobblemonResource("textures/gui/tm/eject_button_small.png")
        val EJECT_BUTTON_LARGE = cobblemonResource("textures/gui/tm/eject_button_large.png")
        val PARTY_SLOT = cobblemonResource("textures/gui/tm/party_slot.png")
        val TEACH_BUTTON = cobblemonResource("textures/gui/tm/teach_button.png")
    }

//    val selectedTM: TechnicalMachine? = TechnicalMachines.tmMap[cobblemonResource("thunderbolt")]!!
    var selectedTM: TechnicalMachine? = null
    var tmList: SettableObservable<MutableList<TechnicalMachine>> = SettableObservable(mutableListOf())
    var output: ItemStack? = null
    val children: MutableMap<String, Element> = mutableMapOf()
    var mode: Int = TYPE_MENU_MODE
    var sortType: ElementalType? = null
    var selectedPokemon: Pokemon? = null
    var scroll: TMScrollingList? = null

    init {
        scroll = TMScrollingList(
                x = x + 132,
                y = y + 45,
                parent = this
        )
    }


//    val selectedTM = TechnicalMachine("stompingtantrum", null, emptyList(), "ground", null, null)

    /**
     * Used instead of `addDrawableChild()` to avoid re-drawing children.
     */
    private fun <T> addChild(drawableElement: T, identifier: String) where T : Element, T : Drawable, T : Selectable {
        if (children.contains(identifier)) return
        addDrawableChild(drawableElement)
        children[identifier] = drawableElement
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (scroll?.isHovered(mouseX, mouseY) == true) {
            scroll!!.mouseClicked(mouseX, mouseY, button)
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (scroll?.isHovered(mouseX, mouseY) == true) {
            scroll!!.mouseScrolled(mouseX, mouseY, amount)
            return true
        }
        return false
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (mode != TYPE_MENU_MODE) scroll?.mouseDragged(
                mouseX,
                mouseY,
                button,
                deltaX,
                deltaY
        )
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    fun drawBlankCraftingRecipe(context: DrawContext) {
        val x = (width - TEXTURE_WIDTH) / 2
        val y = (height - TEXTURE_HEIGHT) / 2
        // ingredient
        renderScaledGuiItemIcon(
            itemStack = Items.AMETHYST_SHARD.defaultStack,
            x = (x + 197 + 36).toDouble(),
            y = (y + 127).toDouble(),
            zTranslation = 1f
        )

        drawScaledText(
            context = context,
            text = lang("ui.tms.material_cost"),
            x = (x + 223),
            y = (y + 117.5),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = Text.translatable(CobblemonItems.BLANK_TM.translationKey).bold(),
            x = (x + 223),
            y = (y + 12),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.power"),
            x = (x + 199),
            y = (y + 25),
            scale = 0.5f
        )

        drawScaledText(
            context = context,
            text = Text.literal("-"),
            x = (x + 241),
            y = (y + 25),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.accuracy_short"),
            x = (x + 199),
            y = (y + 35),
            scale = 0.5f
        )

        drawScaledText(
            context = context,
            text = Text.literal("-"),
            x = (x + 241),
            y = (y + 35),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.pp"),
            x = (x + 199),
            y = (y + 45),
            scale = 0.5f
        )

        drawScaledText(
            context = context,
            text = Text.literal("-"),
            x = (x + 241),
            y = (y + 45),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.summary.title"),
            x = (x + 199),
            y = (y + 54.5),
            scale = 0.5f
        )

        val scale = 0.5f

        context.matrices.push()
        context.matrices.scale(scale, scale, 1f)
        MultiLineLabelK.create(
            component = lang("ui.tms.blank_tm_desc"),
            width = 49 / scale,
            maxLines = 7
        ).renderLeftAligned(
            context = context,
            x = (x + 199) / scale,
            y = (y + 62) / scale,
            ySpacing = 5.5 / scale,
            colour = ColourLibrary.WHITE,
            shadow = true
        )
        context.matrices.pop()
    }

    fun drawMoveInfo(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        val currentTm = selectedTM
        if (currentTm == null) {
            return drawBlankCraftingRecipe(context)
        }

        val x = (width - TEXTURE_WIDTH) / 2
        val y = (height - TEXTURE_HEIGHT) / 2
        val move = Moves.getByNameOrDummy(currentTm.moveName)
        // blank tm
        renderScaledGuiItemIcon(
            itemStack = CobblemonItems.BLANK_TM.defaultStack,
            x = (x + 197).toDouble(),
            y = (y + 127).toDouble(),
        )

        val typeGem = Registries.ITEM.get(ElementalTypes.get(currentTm.type)?.typeGem).defaultStack
        // type gem
        renderScaledGuiItemIcon(
            itemStack = typeGem ?: CobblemonItems.NORMAL_TYPE_GEM.defaultStack,
            x = (x + 197 + 18).toDouble(),
            y = (y + 127).toDouble(),
        )

        val recipe = currentTm.recipe ?: TechnicalMachineRecipe(Identifier.of("minecraft", "air")!!, 1)
        // ingredient
        renderScaledGuiItemIcon(
            itemStack = Registries.ITEM.get(recipe.item).defaultStack,
            x = (x + 197 + 36).toDouble(),
            y = (y + 127).toDouble(),
            zTranslation = 1f
        )

        context.matrices.push()
        context.matrices.translate(0f, 0f, 2f)

        if (recipe.count > 1) {
            val xIncrease = if (recipe.count < 10) 47 else 41
            drawScaledText(
                context = context,
                text = Text.literal(recipe.count.toString()),
                x = (x + 197 + xIncrease).toDouble(),
                y = (y + 136).toDouble(),
                shadow = true
            )
        }

        context.matrices.pop()

        drawScaledText(
            context = context,
            text = lang("ui.tms.material_cost"),
            x = (x + 223),
            y = (y + 117.5),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = currentTm.translatedMoveName().bold(),
            x = (x + 223),
            y = (y + 12),
            scale = 0.5f,
            centered = true
        )

        MoveCategoryIcon(
            x = (x + 237.5),
            y = (y + 52.5),
            category = Moves.getByNameOrDummy(currentTm.moveName).damageCategory
        ).render(context)

        drawScaledText(
            context = context,
            text = lang("ui.power"),
            x = (x + 199),
            y = (y + 25),
            scale = 0.5f
        )

        drawScaledText(
            context = context,
            text = Text.literal(move.power.toInt().toString()),
            x = (x + 241),
            y = (y + 25),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.accuracy_short"),
            x = (x + 199),
            y = (y + 35),
            scale = 0.5f
        )

        drawScaledText(
            context = context,
            text = Text.literal(move.accuracy.toInt().toString() + "%"),
            x = (x + 241),
            y = (y + 35),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.pp"),
            x = (x + 199),
            y = (y + 45),
            scale = 0.5f
        )

        drawScaledText(
            context = context,
            text = Text.literal(move.pp.toString()),
            x = (x + 241),
            y = (y + 45),
            scale = 0.5f,
            centered = true
        )

        drawScaledText(
            context = context,
            text = lang("ui.summary.title"),
            x = (x + 199),
            y = (y + 54.5),
            scale = 0.5f
        )

        val scale = 0.5f

        context.matrices.push()
        context.matrices.scale(scale, scale, 1f)
        MultiLineLabelK.create(
            component = lang("move.${currentTm.moveName}.desc"),
            width = 49 / scale,
            maxLines = 7
        ).renderLeftAligned(
            context = context,
            x = (x + 199) / scale,
            y = (y + 62) / scale,
            ySpacing = 5.5 / scale,
            colour = ColourLibrary.WHITE,
            shadow = true
        )
        context.matrices.pop()
    }

    fun drawTypeMenu(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        val x = (width - TEXTURE_WIDTH) / 2
        val y = (height - TEXTURE_HEIGHT) / 2

        blitk(
            matrixStack = context.matrices,
            texture = TYPE_SELECTION_BASE,
            x = x, // horizontal placement of GUI
            y = y, // vertical placement of GUI

            width = TEXTURE_WIDTH, // scale of the GUI width
            height = TEXTURE_HEIGHT // scale of the GUI height
        )

        fun drawTypeRow(drawX: Int, drawY: Int, types: List<ElementalType>) {
            var xOffset = 0
            for (type in types) {
                addChild(TypeButton(
                    pX = drawX + 32 + xOffset,
                    pY = drawY + 16,
                    type = type,
                    onPress = {
                        inventory.player.playSound(CobblemonSounds.GUI_CLICK, 1f, 1f)
                        tmList.set(TechnicalMachine.filterTms(null, type, null).toMutableList())
                        sortType = type
                        mode = TM_BROWSING_MODE
                        clearGUI()
                        scroll?.scrollAmount = 0.0
                    }
                ), "${type.name}type")
                xOffset += 24
            }
        }

        drawTypeRow(x, y, listOf(
            ElementalTypes.NORMAL,
            ElementalTypes.FIRE,
            ElementalTypes.WATER,
            ElementalTypes.GRASS,
            ElementalTypes.ELECTRIC,
            ElementalTypes.ICE
        ))

        drawTypeRow(x + 12, y + 24, listOf(
            ElementalTypes.FIGHTING,
            ElementalTypes.POISON,
            ElementalTypes.GROUND,
            ElementalTypes.FLYING,
        ))

        drawTypeRow(x, y + 48, listOf(
            ElementalTypes.PSYCHIC,
            ElementalTypes.BUG,
            ElementalTypes.ROCK,
            ElementalTypes.GHOST,
        ))

        drawTypeRow(x + 12, y + 72, listOf(
            ElementalTypes.DRAGON,
            ElementalTypes.DARK,
            ElementalTypes.STEEL,
            ElementalTypes.FAIRY,
        ))

        addChild(
            DiscButton(
                pX = x + 134,
                pY = y + 50,
                onPress = {
                    inventory.player.playSound(CobblemonSounds.GUI_CLICK, 1f, 1f)
                    tmList.set(TechnicalMachine.filterTms(null, null, null).toMutableList())
                    mode = TM_BROWSING_MODE
                    sortType = null
                    scroll?.scrollAmount = 0.0
                    clearGUI()
                }
            ), "disc"
        )

    }

    fun drawTmSelectMenu(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        val x = (width - TEXTURE_WIDTH) / 2
        val y = (height - TEXTURE_HEIGHT) / 2

        blitk(
            matrixStack = context.matrices,
            texture = TM_SELECTION_BASE,
            x = x, // horizontal placement of GUI
            y = y, // vertical placement of GUI

            width = TEXTURE_WIDTH, // scale of the GUI width
            height = TEXTURE_HEIGHT // scale of the GUI height
        )

        blitk(
            matrixStack = context.matrices,
            texture = TM_SELECTION_BORDER,
            x = x + 31, // horizontal placement of GUI
            y = y + 14, // vertical placement of GUI
            width = 157,
            height = 101
        )

        val displayType = sortType

        addChild(
            TypeButton(
                pX = x + 31,
                pY = y + 16,
                type = displayType,
                onPress = {
                    mode = TYPE_MENU_MODE
                    inventory.player.playSound(CobblemonSounds.GUI_CLICK, 1f, 1f)
                    clearGUI()
                }
            ), "returnTypeMenu"
        )

        scroll?.render(context, mouseX, mouseY, delta)
    }

    fun clearGUI() {
        val iterator = children.iterator()
        while (iterator.hasNext()) {
            val (id, _) = iterator.next()
            iterator.remove()
            children.remove(id)
        }
        clearChildren()
    }

    override fun drawBackground(context: DrawContext, delta: Float, mouseX: Int, mouseY: Int) {
        super.renderBackground(context)
        val x = (width - TEXTURE_WIDTH) / 2
        val y = (height - TEXTURE_HEIGHT) / 2


        blitk(
            matrixStack = context.matrices,
            texture = TYPE_SELECTION_BASE,
            x = x, // horizontal placement of GUI
            y = y, // vertical placement of GUI

            width = TEXTURE_WIDTH, // scale of the GUI width
            height = TEXTURE_HEIGHT // scale of the GUI height
        )

        when (mode) {
            TYPE_MENU_MODE -> drawTypeMenu(context, delta, mouseX, mouseY)
            else -> drawTmSelectMenu(context, delta, mouseX, mouseY)
        }
        drawMoveInfo(context, delta, mouseX, mouseY)
        inventory.markDirty()

        addChild(
            EjectButton(
                pX = x + 196,
                pY = y + 168,
                small = false,
                onPress = {
                    inventory.player.playSound(CobblemonSounds.GUI_CLICK, 1f, 1f)
                    val currentTm = selectedTM ?: return@EjectButton sendPacketToServer(CraftBlankTMPacket(handler.input.getStack(2)))
                    sendPacketToServer(
                        CraftTMPacket(
                            currentTm,
                            handler.input.getStack(0),
                            handler.input.getStack(1),
                            handler.input.getStack(2)
                        )
                    )
                    this.handler.syncState()
                }
            ), "eject"
        )

        addChild(
                ExitButton(
                        pX = x + 228,
                        pY = y + 185,
                        onPress = {
                            this.close()
                        }
                ), "exit"
        )

        val startY = y + 13
        val offset = 28
        var iterations = 0
        CobblemonClient.storage.myParty.forEach { pokemon ->
            if (pokemon != null) {
                addChild(TMPartySlotWidget(
                    pX = x - 23,
                    pY = startY + (offset * iterations),
                    pokemon = pokemon,
                    onPress = {
                        selectedPokemon = if (selectedPokemon == pokemon) null else pokemon
                    }
                ), "partyslot_${pokemon.uuid}")
            }
            iterations++
        }
    }

    override fun drawForeground(context: DrawContext?, mouseX: Int, mouseY: Int) {
        //Text is usually drawn here, we dont want that
        this.drawMouseoverTooltip(context, mouseX - 158, mouseY - 28)
        //this.renderWithTooltip(context, mouseX, mouseY, 1F)
    }

    override fun onSlotUpdate(handler: ScreenHandler?, slotId: Int, stack: ItemStack?) {

    }

    override fun onPropertyUpdate(handler: ScreenHandler?, property: Int, value: Int) {

    }
}