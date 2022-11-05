/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.client.keybind.CurrentKeyAccessor;
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
