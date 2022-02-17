package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.client.render.CobbledLayerDefinitions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {
    @Redirect(
            method = "createRoots",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap;builder()Lcom/google/common/collect/ImmutableMap$Builder;",
                    opcode = Opcodes.INVOKESTATIC,
                    remap = false
            ),
            remap = false
    )
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builderRedirect() {
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder = ImmutableMap.builder();

        CobbledLayerDefinitions.INSTANCE.getLayerDefinitions().forEach((loc, def) -> {
            builder.put(loc, def.get());
        });

        return builder;
    }
}
