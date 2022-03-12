package com.cablemc.pokemoncobbled.common.api.text

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Text utility shamelessly stolen from Cable Libs
 *
 * @author Hiroku, Craft (on Cable Libs)
 */
class Text internal constructor() {

    companion object {
        internal fun resolveComponent(text: Any): MutableComponent {
            return TextComponent(text.toString().replace("&[A-Fa-f0-9k-oK-oRr]".toRegex()) { "§${it.value.substring(1)}" })
        }
    }

    private var style = Style.EMPTY
    private var head: MutableComponent? = null

    fun parse(vararg components: Any): MutableComponent {
        components.forEach {
            when (it) {
                is MutableComponent -> {
                    addComponent(it)
                    style = getBlankStyle()
                }
                is ClickEvent -> style = style.withClickEvent(it)
                is HoverEvent -> style = style.withHoverEvent(it)
                is ChatFormatting -> {
                    when {
                        it.isColor -> style = style.withColor(it)
                        it == ChatFormatting.UNDERLINE || it == UNDERLINED -> style = style.withUnderlined(true)
                        it == ChatFormatting.BOLD || it == BOLD -> style = style.withBold(true)
                        it == ChatFormatting.ITALIC || it == ITALIC -> style = style.withItalic(true)
                        it == ChatFormatting.OBFUSCATED || it == OBFUSCATED -> style = style.withObfuscated(true)
                        it == ChatFormatting.RESET || it == RESET -> style = Style.EMPTY
                    }
                }
                else -> addComponent(resolveComponent(it).also { it.style = style.applyTo(it.style) })
            }
        }

        return head?: TextComponent("Empty!")
    }

    private fun addComponent(component: MutableComponent) {
        if (head == null) {
            head = component
            component.style = style.applyTo(component.style)
            style = getBlankStyle()
        } else {
            head?.add(component.also { it.style = style.applyTo(it.style) })
        }
    }

    private fun getBlankStyle() = Style.EMPTY.withBold(false).withItalic(false).withUnderlined(false).withObfuscated(false).withColor(
        ChatFormatting.WHITE).withClickEvent(null).withHoverEvent(null)
}

fun text(vararg components: Any) = Text().parse(*components)

val textClickHandlers = hashMapOf<UUID, (p: ServerPlayer) -> Unit>()

fun click(consumed: AtomicBoolean, action: (p: ServerPlayer) -> Unit): ClickEvent {
    val uuid = UUID.randomUUID()
    textClickHandlers[uuid] = {
        if (!consumed.get()) {
            action.invoke(it)
            consumed.set(true)
        }
        textClickHandlers.remove(uuid)
    }
    return ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cobbledclicktext $uuid")
}

fun click(onlyOnce: Boolean = false, action: (p: ServerPlayer) -> Unit): ClickEvent {
    val uuid = UUID.randomUUID()
    textClickHandlers[uuid] = if (onlyOnce) {
        {
            textClickHandlers.remove(uuid)
            action.invoke(it)
        }
    } else {
        { action.invoke(it) }
    }
    return ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cobbledclicktext $uuid")
}

fun hover(text: Component) = HoverEvent(HoverEvent.Action.SHOW_TEXT, text)
fun hover(text: String) = hover(Text.resolveComponent(text))
fun hover(item: ItemStack) = HoverEvent(HoverEvent.Action.SHOW_ITEM, HoverEvent.ItemStackInfo(item))
fun hover(entity: LivingEntity) = HoverEvent(HoverEvent.Action.SHOW_ENTITY, HoverEvent.EntityTooltipInfo(entity.type, entity.uuid, entity.displayName))

val BOLD = Object()
val ITALIC = Object()
val UNDERLINED = Object()
val OBFUSCATED = Object()
val RESET = Object()

fun String.red() = text(ChatFormatting.RED, this)
fun String.black() = text(ChatFormatting.BLACK, this)
fun String.darkBlue() = text(ChatFormatting.DARK_BLUE, this)
fun String.darkGreen() = text(ChatFormatting.DARK_GREEN, this)
fun String.darkAqua() = text(ChatFormatting.DARK_AQUA, this)
fun String.darkRed() = text(ChatFormatting.DARK_RED, this)
fun String.darkPurple() = text(ChatFormatting.DARK_PURPLE, this)
fun String.gold() = text(ChatFormatting.GOLD, this)
fun String.gray() = text(ChatFormatting.GRAY, this)
fun String.darkGray() = text(ChatFormatting.DARK_GRAY, this)
fun String.blue() = text(ChatFormatting.BLUE, this)
fun String.green() = text(ChatFormatting.GREEN, this)
fun String.aqua() = text(ChatFormatting.AQUA, this)
fun String.lightPurple() = text(ChatFormatting.LIGHT_PURPLE, this)
fun String.yellow() = text(ChatFormatting.YELLOW, this)
fun String.white() = text(ChatFormatting.WHITE, this)

fun MutableComponent.red() = also { it.style = it.style.withColor(ChatFormatting.RED) }
fun MutableComponent.black() = also { it.style = it.style.withColor(ChatFormatting.BLACK) }
fun MutableComponent.darkBlue() = also { it.style = it.style.withColor(ChatFormatting.DARK_BLUE) }
fun MutableComponent.darkGreen() = also { it.style = it.style.withColor(ChatFormatting.DARK_GREEN) }
fun MutableComponent.darkAqua() = also { it.style = it.style.withColor(ChatFormatting.DARK_AQUA) }
fun MutableComponent.darkRed() = also { it.style = it.style.withColor(ChatFormatting.DARK_RED) }
fun MutableComponent.darkPurple() = also { it.style = it.style.withColor(ChatFormatting.DARK_PURPLE) }
fun MutableComponent.gold() = also { it.style = it.style.withColor(ChatFormatting.GOLD) }
fun MutableComponent.gray() = also { it.style = it.style.withColor(ChatFormatting.GRAY) }
fun MutableComponent.darkGray() = also { it.style = it.style.withColor(ChatFormatting.DARK_GRAY) }
fun MutableComponent.blue() = also { it.style = it.style.withColor(ChatFormatting.BLUE) }
fun MutableComponent.green() = also { it.style = it.style.withColor(ChatFormatting.GREEN) }
fun MutableComponent.aqua() = also { it.style = it.style.withColor(ChatFormatting.AQUA) }
fun MutableComponent.lightPurple() = also { it.style = it.style.withColor(ChatFormatting.LIGHT_PURPLE) }
fun MutableComponent.yellow() = also { it.style = it.style.withColor(ChatFormatting.YELLOW) }
fun MutableComponent.white() = also { it.style = it.style.withColor(ChatFormatting.WHITE) }

fun String.text() = text(this)
fun String.stripCodes(): String = this.replace("[&§][A-Ea-e0-9K-Ok-oRr]".toRegex(), "")

fun MutableComponent.onClick(consumed: AtomicBoolean, action: (p: ServerPlayer) -> Unit) = also { it.style = it.style.withClickEvent(click(consumed, action)) }
fun MutableComponent.onClick(onlyOnce: Boolean = false, action: (p: ServerPlayer) -> Unit) = also { it.style = it.style.withClickEvent(click(onlyOnce, action)) }
fun MutableComponent.onHover(string: String) = also { it.style = it.style.withHoverEvent(hover(string)) }
fun MutableComponent.onHover(text: MutableComponent) = also { it.style = it.style.withHoverEvent(hover(text)) }
fun MutableComponent.underline() = also { it.style = it.style.withUnderlined(true) }
fun MutableComponent.bold() = also { it.style = it.style.withBold(true) }
fun MutableComponent.italicise() = also { it.style = it.style.withItalic(true) }
fun MutableComponent.strikethrough() = also { it.style = it.style.withStrikethrough(true) }
fun MutableComponent.obfuscate() = also { it.style = it.style.withObfuscated(true) }
fun MutableComponent.suggest(command: String) = also { it.style = it.style.withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)) }

fun MutableComponent.add(other: Component): MutableComponent {
    this.siblings.add(other)
    return this;
}

fun MutableComponent.add(string: String): MutableComponent {
    this.add(text(string))
    return this;
}

operator fun MutableComponent.plus(component: Component) = this.add(component)
operator fun MutableComponent.plus(string: String) = this.add(string)

fun Iterable<MutableComponent>.sum(separator: MutableComponent = ", ".text()) = if (any()) reduce { acc, next -> acc + separator + next } else "".text()