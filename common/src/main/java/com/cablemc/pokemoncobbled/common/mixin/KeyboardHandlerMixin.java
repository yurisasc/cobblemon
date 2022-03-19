package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.client.keybind.CobbledKeybinds;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin notifying our keybinds registry to trigger actions on our keybinds
 *
 * @author Qu
 * @since 2022-02-17
 */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(
            method = "keyPress",
            at = @At(
                    value = "TAIL",
                    target = "Lnet/minecraft/client/KeyboardHandler;keyPress(JIIII)V"
            )
    )
    public void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (l == this.minecraft.getWindow().getWindow()) {
            if (this.minecraft.screen == null) {
                CobbledKeybinds.INSTANCE.onAnyKey(i, j, k, m);
            }
        }
    }
}
