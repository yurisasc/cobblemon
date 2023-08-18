/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorLists;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructureProcessorLists.class)
public class StructureProcessorListsMixin {
  @Inject(method = "register", at = @At("HEAD"))
  private static void register(Registerable<StructureProcessorList> processorListRegisterable,
      RegistryKey<StructureProcessorList> key, List<StructureProcessor> processors, CallbackInfo ci) {
    System.out.println("Mixin working");
    //Ensure that the list of processors is mutable
    processors = new ArrayList<>();
  }
}
