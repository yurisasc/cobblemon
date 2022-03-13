package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.client.keybind.CurrentKeyAccessor;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Mixin allowing to get the current key and not just the default key from a {@link KeyMapping}
 *
 * @author Qu
 * @since 2022-02-17
 */
@Mixin(KeyMapping.class)
public class KeyMappingMixin implements CurrentKeyAccessor {

    @Shadow
    private InputConstants.Key key;

    @NotNull
    @Override
    public InputConstants.Key currentKey() {
        return key;
    }
}
