package com.cobblemon.mod.common.mixin.invoker;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerInvoker {
    @Invoker
    void callPlaySpawnSound(Entity entity);
}
