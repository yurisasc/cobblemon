package com.cobblemon.mod.common.item

import net.minecraft.item.ArmorItem
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class AuspiciousArmorMaterial : ArmorMaterial {
    override fun getDurability(type: ArmorItem.Type?): Int {
        return 300
    }

    override fun getProtection(type: ArmorItem.Type?): Int {
        return 1
    }

    override fun getEnchantability(): Int {
        return 5
    }

    override fun getEquipSound(): SoundEvent {
        return SoundEvents.ITEM_ARMOR_EQUIP_IRON
    }

    override fun getRepairIngredient(): Ingredient {
        return Ingredient.EMPTY
    }

    override fun getName(): String {
        return "auspicious_armor"
    }

    override fun getToughness(): Float {
        return 0.0f
    }

    override fun getKnockbackResistance(): Float {
        return 0.0f
    }
}