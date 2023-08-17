package com.cobblemon.mod.common.bridges;

import net.minecraft.structure.processor.StructureProcessor;

public interface StructureProcessorListBridge {
    void append(StructureProcessor processor);
    void clear();
}
