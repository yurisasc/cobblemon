package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManagerImpl.class)
public class ReloadableResourceManagerImplMixin {

    @Inject(method = "reload", at = @At(value = "HEAD"))
    public void reloadResources(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir) {
        System.out.println("Reloading Resources");
        PokemonCobbledClient.INSTANCE.reloadCodedAssets(MinecraftClient.getInstance().getResourceManager());
    }

}
