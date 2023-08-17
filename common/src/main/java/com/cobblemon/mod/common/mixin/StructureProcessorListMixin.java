package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.bridges.StructureProcessorListBridge;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureProcessorList.class)
public class StructureProcessorListMixin implements StructureProcessorListBridge {

    @Final
    @Mutable
    @Shadow
    private List<StructureProcessor> list;

    @Override
    public void append(StructureProcessor processor) {
        List<StructureProcessor> mutable = new ArrayList<>(this.list);
        mutable.add(processor);
        this.list = ImmutableList.copyOf(mutable);
    }

    @Override
    public void clear() {
        this.list = ImmutableList.of();
    }


}
