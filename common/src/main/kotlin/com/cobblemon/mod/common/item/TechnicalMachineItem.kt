package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.api.text.gray
import com.cobblemon.mod.common.api.tms.TechnicalMachine
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.util.lang
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World

class TechnicalMachineItem(
    settings: Settings
): CobblemonItem(settings) {

    companion object {
        val STORED_MOVE_KEY = "StoredMove"
    }

    override fun hasGlint(stack: ItemStack?) = true

    fun getMoveNbt(stack: ItemStack): TechnicalMachine? {
        val nbtCompound = stack.nbt ?: return null
        val tm = TechnicalMachines.tmMap[Identifier.tryParse(nbtCompound.getString(STORED_MOVE_KEY))]

        return tm ?: TechnicalMachines.tmMap[Identifier.tryParse("cobblemon:take_down")!!]
    }

    fun setMoveNbt(stack: ItemStack, id: String) {
        stack.getOrCreateNbt().put(STORED_MOVE_KEY, NbtString.of(id))
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext?
    ) {
        if (getMoveNbt(stack) != null) {
            val nbt = getMoveNbt(stack)
            val string = lang("move." + nbt!!.moveName)
            tooltip.add(string.gray())
        }
    }

}