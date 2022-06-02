package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.client.keybind.CurrentKeyAccessor;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Mixin allowing to get the current key and not just the default key from a {@link KeyBinding}
 *
 * @author Qu
 * @since 2022-02-17
 */
@Mixin(KeyBinding.class)
public class KeyBindingMixin implements CurrentKeyAccessor {

    @Shadow
    private InputUtil.Key boundKey;

    @NotNull
    @Override
    public InputUtil.Key currentKey() {
        return boundKey;
    }
}
