package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.CobblemonItems;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PiglinBrain.class)
public class PiglinBarterMixin {

    @Inject(method = "acceptsForBarter", at = @At(value = "RETURN"), cancellable = true)
    private static void cobblemon$acceptsForBarter(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if(!ci.getReturnValue()) {
            //TODO: Make a tag?
            if(stack.isOf(CobblemonItems.RELIC_COIN_POUCH))
                ci.setReturnValue(true);
        }
    }


}
