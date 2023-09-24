package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.api.tags.CobblemonBiomeTags;
import com.cobblemon.mod.common.music.CustomMusic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.MusicSound;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "getMusicType", at = @At("RETURN"), cancellable = true)
    private void cobblemon$getMusicType(CallbackInfoReturnable<MusicSound> cir) {

        if (this.player != null) {
            RegistryEntry<Biome> playerBiome = this.player.getWorld().getBiome(this.player.getBlockPos());

            List<MusicSound> possibleTracks = new ArrayList<>();

            possibleTracks.add(cir.getReturnValue());

            // Custom BiomeTag check to add music to certain biomes
            if (playerBiome.isIn(CobblemonBiomeTags.IS_ABYSS)) {
                possibleTracks.add(CustomMusic.DEEP_DARK);
            }

            cir.setReturnValue(possibleTracks.get(new Random().nextInt(possibleTracks.size())));
        }
    }
}
