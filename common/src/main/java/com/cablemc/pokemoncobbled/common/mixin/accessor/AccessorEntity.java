package com.cablemc.pokemoncobbled.common.mixin.accessor;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface AccessorEntity {

    @Accessor("standingEyeHeight")
    void standingEyeHeight(float standingEyeHeight);

}
