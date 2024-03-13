package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeehiveTreeDecorator.class)
public abstract class BeeHiveTreeDecoratorMixin extends TreeDecorator {
    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/treedecorator/TreeDecorator$Generator;getWorld()Lnet/minecraft/world/TestableWorld;"), cancellable = true)
    private void cobblemon$cancelBeeSpawn(Generator generator, CallbackInfo ci) {
        if (!Cobblemon.config.getVanillaSpawns()) {
            ci.cancel();
        }
    }
}
