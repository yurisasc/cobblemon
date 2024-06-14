package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.Lists
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.Selectable.SelectionType
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.recipebook.RecipeBookGhostSlots
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.ToggleButtonWidget
import net.minecraft.client.recipebook.ClientRecipeBook
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.client.resource.language.LanguageDefinition
import net.minecraft.client.resource.language.LanguageManager
import net.minecraft.client.search.SearchManager
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeGridAligner
import net.minecraft.recipe.RecipeMatcher
import net.minecraft.recipe.book.RecipeBookCategory
import net.minecraft.screen.AbstractRecipeScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import org.jetbrains.annotations.Nullable
import java.util.*

@Environment(EnvType.CLIENT)
class CookBookWidget : RecipeGridAligner<Ingredient>, Drawable, Element, Selectable, RecipeDisplayListener {
    //val SEARCH_HINT_TEXT: Text
    val field_32408 = 147
    val field_32409 = 166
    val field_32410 = 86
    //val TOGGLE_CRAFTABLE_RECIPES_TEXT: Text
    //val TOGGLE_ALL_RECIPES_TEXT: Text
    var leftOffset = 0
    var parentWidth = 0
    var parentHeight = 0
    val ghostSlots = RecipeBookGhostSlots()
    val tabButtons: ArrayList<RecipeGroupButtonWidget> = Lists.newArrayList<RecipeGroupButtonWidget>()
    @Nullable
    var currentTab: RecipeGroupButtonWidget? = null
    lateinit var toggleCraftableButton: ToggleButtonWidget
    lateinit var craftingScreenHandler: AbstractRecipeScreenHandler<*>
    lateinit var client: MinecraftClient
    @Nullable
    var searchField: TextFieldWidget? = null
    var searchText = ""
    lateinit var recipeBook: ClientRecipeBook
    val recipesArea = CookBookResults()
    val recipeFinder = RecipeMatcher()
    var cachedInvChangeCount = 0
    var searching = false
    var open = false
    var narrow = false

    fun initialize(parentWidth: Int, parentHeight: Int, client: MinecraftClient?, narrow: Boolean, craftingScreenHandler: AbstractRecipeScreenHandler<*>) {
        if (client != null) {
            this.client = client
        }
        this.parentWidth = parentWidth
        this.parentHeight = parentHeight
        this.craftingScreenHandler = craftingScreenHandler
        this.narrow = narrow
        client?.player?.currentScreenHandler = craftingScreenHandler
        this.recipeBook = client?.player?.recipeBook ?: return
        this.cachedInvChangeCount = client?.player?.inventory?.changeCount ?: 0
        this.open = isGuiOpen()
        if (this.open) {
            reset()
        }
    }

    fun reset() {
        leftOffset = if (narrow) 0 else 86
        val i = (parentWidth - 147) / 2 - leftOffset
        val j = (parentHeight - 166) / 2
        recipeFinder.clear()
        client.player?.inventory?.populateRecipeFinder(recipeFinder)
        craftingScreenHandler.populateRecipeFinder(recipeFinder)
        val string = searchField?.text ?: ""
        val var10003 = client.textRenderer
        val var10004 = i + 26
        val var10005 = j + 14
        Objects.requireNonNull(client.textRenderer)
        searchField = TextFieldWidget(var10003, var10004, var10005, 79, 9 + 3, Text.translatable("itemGroup.search"))
        searchField?.setMaxLength(50)
        searchField?.isVisible = true
        searchField?.setEditableColor(16777215)
        searchField?.text = string
        searchField?.setPlaceholder(SEARCH_HINT_TEXT)
        recipesArea.initialize(client, i, j)
        recipesArea.setGui(this)
        toggleCraftableButton = ToggleButtonWidget(i + 110, j + 12, 26, 16, recipeBook.isFilteringCraftable(craftingScreenHandler))
        updateTooltip()
        setBookButtonTexture()
        tabButtons.clear()
        val var4 = RecipeBookGroup.getGroups(craftingScreenHandler.category).iterator()
        while (var4.hasNext()) {
            val recipeBookGroup = var4.next() as RecipeBookGroup
            tabButtons.add(RecipeGroupButtonWidget(recipeBookGroup))
        }
        if (currentTab != null) {
            currentTab = tabButtons.stream().filter { button ->
                button.category == currentTab?.category
            }.findFirst().orElse(null)
        }
        if (currentTab == null) {
            currentTab = tabButtons[0]
        }
        currentTab?.setToggled(true)
        refreshResults(false)
        refreshTabButtons()
    }

    private fun updateTooltip() {
        toggleCraftableButton.setTooltip(if (toggleCraftableButton.isToggled) Tooltip.of(getToggleCraftableButtonText()) else Tooltip.of(TOGGLE_ALL_RECIPES_TEXT))
    }

    fun setBookButtonTexture() {
        toggleCraftableButton.setTextureUV(152, 41, 28, 18, TEXTURE)
    }

    fun findLeftEdge(width: Int, backgroundWidth: Int): Int {
        return if (isOpen() && !narrow) {
            177 + (width - backgroundWidth - 200) / 2
        } else {
            (width - backgroundWidth) / 2
        }
    }

    fun toggleOpen() {
        setOpenValue(!isOpen())
    }

    fun isOpen(): Boolean {
        return open
    }

    fun isGuiOpen(): Boolean {
        return recipeBook.isGuiOpen(craftingScreenHandler.category)
    }

    fun setOpenValue(opened: Boolean) {
        if (opened) {
            reset()
        }
        open = opened
        recipeBook.setGuiOpen(craftingScreenHandler.category, opened)
        if (!opened) {
            recipesArea.hideAlternates()
        }
        sendBookDataPacket()
    }

    fun slotClicked(@Nullable slot: Slot?) {
        if (slot != null && slot.id < craftingScreenHandler.craftingSlotCount) {
            ghostSlots.reset()
            if (isOpen()) {
                refreshInputs()
            }
        }
    }

    fun refreshResults(resetCurrentPage: Boolean) {
        val list = recipeBook.getResultsForGroup(currentTab?.category ?: return)
        list.forEach { resultCollection ->
            resultCollection.computeCraftables(recipeFinder, craftingScreenHandler.craftingWidth, craftingScreenHandler.craftingHeight, recipeBook)
        }
        val list2 = Lists.newArrayList(list)
        list2.removeIf { !it.isInitialized }
        list2.removeIf { !it.hasFittingRecipes() }
        val string = searchField?.text ?: ""
        if (string.isNotEmpty()) {
            val objectSet = ObjectLinkedOpenHashSet(client.getSearchProvider(SearchManager.RECIPE_OUTPUT).findAll(string.lowercase(Locale.ROOT)))
            list2.removeIf { !objectSet.contains(it) }
        }
        if (recipeBook.isFilteringCraftable(craftingScreenHandler)) {
            list2.removeIf { !it.hasCraftableRecipes() }
        }
        recipesArea.setResults(list2, resetCurrentPage)
    }

    fun refreshTabButtons() {
        val i = (parentWidth - 147) / 2 - leftOffset - 30
        val j = (parentHeight - 166) / 2 + 3
        var l = 0
        val var5 = tabButtons.iterator()
        while (true) {
            while (var5.hasNext()) {
                val recipeGroupButtonWidget = var5.next()
                val recipeBookGroup = recipeGroupButtonWidget.category
                if (recipeBookGroup != RecipeBookGroup.CRAFTING_SEARCH && recipeBookGroup != RecipeBookGroup.FURNACE_SEARCH) {
                    if (recipeGroupButtonWidget.hasKnownRecipes(recipeBook)) {
                        recipeGroupButtonWidget.setPosition(i, j + 27 * l++)
                        recipeGroupButtonWidget.checkForNewRecipes(client)
                    }
                } else {
                    recipeGroupButtonWidget.visible = true
                    recipeGroupButtonWidget.setPosition(i, j + 27 * l++)
                }
            }
            return
        }
    }

    fun update() {
        val bl = isGuiOpen()
        if (isOpen() != bl) {
            setOpenValue(bl)
        }
        if (isOpen()) {
            if (cachedInvChangeCount != client.player?.inventory?.changeCount) {
                refreshInputs()
                cachedInvChangeCount = client.player?.inventory?.changeCount ?: 0
            }
            searchField?.tick()
        }
    }

    fun refreshInputs() {
        recipeFinder.clear()
        client.player?.inventory?.populateRecipeFinder(recipeFinder)
        craftingScreenHandler.populateRecipeFinder(recipeFinder)
        refreshResults(false)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (isOpen()) {
            context.matrices.push()
            context.matrices.translate(0.0F, 0.0F, 100.0F)
            val i = (parentWidth - 147) / 2 - leftOffset
            val j = (parentHeight - 166) / 2
            context.drawTexture(TEXTURE, i, j, 1, 1, 147, 166)
            searchField?.render(context, mouseX, mouseY, delta)
            for (recipeGroupButtonWidget in tabButtons) {
                recipeGroupButtonWidget.render(context, mouseX, mouseY, delta)
            }
            toggleCraftableButton.render(context, mouseX, mouseY, delta)
            recipesArea.draw(context, i, j, mouseX, mouseY, delta)
            context.matrices.pop()
        }
    }

    fun drawTooltip(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        if (isOpen()) {
            recipesArea.drawTooltip(context, mouseX, mouseY)
            drawGhostSlotTooltip(context, x, y, mouseX, mouseY)
        }
    }

    fun getToggleCraftableButtonText(): Text {
        return TOGGLE_CRAFTABLE_RECIPES_TEXT
    }

    fun drawGhostSlotTooltip(context: DrawContext, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        var itemStack: ItemStack? = null
        for (i in 0 until ghostSlots.slotCount) {
            val ghostInputSlot = ghostSlots.getSlot(i)
            val j = ghostInputSlot.x + x
            val k = ghostInputSlot.y + y
            if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16) {
                itemStack = ghostInputSlot.currentItemStack
            }
        }
        if (itemStack != null && client.currentScreen != null) {
            context.drawTooltip(client.textRenderer, Screen.getTooltipFromItem(client, itemStack), mouseX, mouseY)
        }
    }

    fun drawGhostSlots(context: DrawContext, x: Int, y: Int, notInventory: Boolean, delta: Float) {
        ghostSlots.draw(context, client, x, y, notInventory, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isOpen() && !client.player?.isSpectator()!!) {
            if (recipesArea.mouseClicked(mouseX, mouseY, button, (parentWidth - 147) / 2 - leftOffset, (parentHeight - 166) / 2, 147, 166)) {
                val recipe = recipesArea.getLastClickedRecipeValue()
                val recipeResultCollection = recipesArea.getLastClickedResults()
                if (recipe != null && recipeResultCollection != null) {
                    if (!recipeResultCollection.isCraftable(recipe) && ghostSlots.recipe == recipe) {
                        return false
                    }
                    ghostSlots.reset()
                    client.interactionManager?.clickRecipe(client.player?.currentScreenHandler?.syncId ?: 0, recipe, Screen.hasShiftDown())
                    if (!isWide()) {
                        setOpenValue(false)
                    }
                }
                return true
            } else if (searchField?.mouseClicked(mouseX, mouseY, button) == true) {
                searchField?.setFocused(true)
                return true
            } else {
                searchField?.setFocused(false)
                if (toggleCraftableButton.mouseClicked(mouseX, mouseY, button)) {
                    val bl = toggleFilteringCraftable()
                    toggleCraftableButton.setToggled(bl)
                    updateTooltip()
                    sendBookDataPacket()
                    refreshResults(false)
                    return true
                } else {
                    for (recipeGroupButtonWidget in tabButtons) {
                        if (recipeGroupButtonWidget.mouseClicked(mouseX, mouseY, button)) {
                            if (currentTab != recipeGroupButtonWidget) {
                                currentTab?.setToggled(false)
                                currentTab = recipeGroupButtonWidget
                                currentTab?.setToggled(true)
                                refreshResults(true)
                            }
                            return true
                        }
                    }
                }
            }
        } else {
            return false
        }
        return false
    }

    private fun toggleFilteringCraftable(): Boolean {
        val recipeBookCategory = craftingScreenHandler.category
        val bl = !recipeBook.isFilteringCraftable(recipeBookCategory)
        recipeBook.setFilteringCraftable(recipeBookCategory, bl)
        return bl
    }

    fun isClickOutsideBounds(mouseX: Double, mouseY: Double, x: Int, y: Int, backgroundWidth: Int, backgroundHeight: Int, button: Int): Boolean {
        return if (!isOpen()) {
            true
        } else {
            val bl = mouseX < x.toDouble() || mouseY < y.toDouble() || mouseX >= (x + backgroundWidth).toDouble() || mouseY >= (y + backgroundHeight).toDouble()
            val bl2 = (x - 147).toDouble() < mouseX && mouseX < x.toDouble() && y.toDouble() < mouseY && mouseY < (y + backgroundHeight).toDouble()
            bl && !bl2 && !currentTab?.isSelected()!!
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        searching = false
        if (isOpen() && !client.player?.isSpectator()!!) {
            return if (keyCode == 256 && !isWide()) {
                setOpenValue(false)
                true
            } else if (searchField?.keyPressed(keyCode, scanCode, modifiers) == true) {
                refreshSearchResults()
                true
            } else if (searchField?.isFocused == true && searchField?.isVisible == true && keyCode != 256) {
                true
            } else if (client.options.chatKey.matchesKey(keyCode, scanCode) && searchField?.isFocused == false) {
                searching = true
                searchField?.setFocused(true)
                true
            } else {
                false
            }
        } else {
            false
        }
        return false
    }

    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        searching = false
        return super.keyReleased(keyCode, scanCode, modifiers)
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        return if (searching) {
            false
        } else if (isOpen() && !client.player?.isSpectator()!!) {
            if (searchField?.charTyped(chr, modifiers) == true) {
                refreshSearchResults()
                true
            } else {
                super.charTyped(chr, modifiers)
            }
        } else {
            false
        }
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        return false
    }

    override fun setFocused(focused: Boolean) {}

    override fun isFocused(): Boolean {
        return false
    }

    fun refreshSearchResults() {
        val string = searchField?.text?.lowercase(Locale.ROOT) ?: ""
        triggerPirateSpeakEasterEgg(string)
        if (string != searchText) {
            refreshResults(false)
            searchText = string
        }
    }

    fun triggerPirateSpeakEasterEgg(search: String) {
        if ("excitedze" == search) {
            val languageManager = client.languageManager
            val string = "en_pt"
            val languageDefinition = languageManager.getLanguage("en_pt")
            if (languageDefinition == null || languageManager.language == "en_pt") {
                return
            }
            languageManager.language = "en_pt"
            client.options.language = "en_pt"
            client.reloadResources()
            client.options.write()
        }
    }

    fun isWide(): Boolean {
        return leftOffset == 86
    }

    fun refresh() {
        refreshTabButtons()
        if (isOpen()) {
            refreshResults(false)
        }
    }

    override fun onRecipesDisplayed(recipes: List<Recipe<*>>) {
        for (recipe in recipes) {
            client.player?.onRecipeDisplayed(recipe)
        }
    }

    fun showGhostRecipe(recipe: Recipe<*>, slots: List<Slot>) {
        val itemStack = recipe.getOutput(client.world?.registryManager)
        ghostSlots.setRecipe(recipe)
        ghostSlots.addSlot(Ingredient.ofStacks(itemStack), slots[0].x, slots[0].y)
        alignRecipeToGrid(craftingScreenHandler.craftingWidth, craftingScreenHandler.craftingHeight, craftingScreenHandler.craftingResultSlotIndex, recipe, recipe.ingredients.iterator(), 0)
    }

    override fun acceptAlignedInput(inputs: Iterator<Ingredient>, slot: Int, amount: Int, gridX: Int, gridY: Int) {
        val ingredient = inputs.next()
        if (!ingredient.isEmpty) {
            val slot2 = craftingScreenHandler.slots[slot]
            ghostSlots.addSlot(ingredient, slot2.x, slot2.y)
        }
    }

    fun sendBookDataPacket() {
        if (client.networkHandler != null) {
            val recipeBookCategory = craftingScreenHandler.category
            val bl = recipeBook.options.isGuiOpen(recipeBookCategory)
            val bl2 = recipeBook.options.isFilteringCraftable(recipeBookCategory)
            client.networkHandler?.sendPacket(RecipeCategoryOptionsC2SPacket(recipeBookCategory, bl, bl2))
        }
    }

    override fun getType(): SelectionType {
        return if (open) SelectionType.HOVERED else SelectionType.NONE
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {
        val list = Lists.newArrayList<Selectable>()
        recipesArea.forEachButton { button ->
            if (button.isNarratable) {
                list.add(button)
            }
        }
        searchField?.let { list.add(it) } // not sure if this is the right way
        list.add(toggleCraftableButton)
        list.addAll(tabButtons)
        val selectedElementNarrationData = Screen.findSelectedElementData(list, null)
        selectedElementNarrationData?.selectable?.appendNarrations(builder.nextMessage())
    }

    companion object {
        val TEXTURE = Identifier("textures/gui/recipe_book.png")
        val SEARCH_HINT_TEXT: Text = Text.translatable("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY)
        val TOGGLE_CRAFTABLE_RECIPES_TEXT: Text = Text.translatable("gui.recipebook.toggleRecipes.craftable")
        val TOGGLE_ALL_RECIPES_TEXT: Text = Text.translatable("gui.recipebook.toggleRecipes.all")

    }
}