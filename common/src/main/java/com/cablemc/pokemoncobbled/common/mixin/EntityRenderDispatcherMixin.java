package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow private Map<String, EntityRenderer<? extends Player>> playerRenderers;

    @Inject(
            method = "onResourceManagerReload",
            at = @At(
                    value = "TAIL"
            )
    )
    public void resourceManagerReloadHook(ResourceManager resourceManager, CallbackInfo ci) {
        PokemonCobbledClient.INSTANCE.onAddLayer(this.playerRenderers);
    }
}
