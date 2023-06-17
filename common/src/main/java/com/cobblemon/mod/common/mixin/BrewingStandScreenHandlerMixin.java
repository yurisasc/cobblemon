package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/BrewingStandScreenHandler$PotionSlot")
public abstract class BrewingStandScreenHandlerMixin extends Slot {
    public BrewingStandScreenHandlerMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void cobblemonMatches(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isOf(CobblemonItems.POKE_BALL)) {
            cir.setReturnValue(true);
        }
    }
}

