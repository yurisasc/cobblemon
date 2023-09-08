package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonSounds
import net.minecraft.item.ArmorItem
import net.minecraft.item.ArmorMaterial
import net.minecraft.item.ItemConvertible
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent

class AuspiciousArmorMaterial : ArmorMaterial {
    override fun getDurability(type: ArmorItem.Type?): Int {
        return 240
    }

    override fun getProtection(type: ArmorItem.Type?): Int {
        return 6
    }

    override fun getEnchantability(): Int {
        return 15
    }

    override fun getEquipSound(): SoundEvent {
        return CobblemonSounds.AUSPICIOUS_ARMOR_EQUIP
    }

    override fun getRepairIngredient(): Ingredient {
        return Ingredient.ofItems(ItemConvertible { Items.FIRE_CHARGE })
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