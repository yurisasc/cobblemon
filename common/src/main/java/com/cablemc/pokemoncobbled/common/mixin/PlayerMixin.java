package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.entity.player.IShoulderable;
import com.cablemc.pokemoncobbled.common.util.CompoundTagExtensionsKt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IShoulderable {

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow public void respawnEntityOnShoulder(CompoundTag p_36371_) {}
    @Shadow public abstract CompoundTag getShoulderEntityLeft();
    @Shadow public abstract void setShoulderEntityLeft(CompoundTag pTag);
    @Shadow public abstract CompoundTag getShoulderEntityRight();
    @Shadow public abstract void setShoulderEntityRight(CompoundTag pTag);

    @Inject(method = "removeEntitiesOnShoulder", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;respawnEntityOnShoulder(Lnet/minecraft/nbt/CompoundTag;)V", ordinal = 0), cancellable = true)
    private void removeEntitiesOnShoulder$sanitizeCobbledEntity(CallbackInfo ci) {
        if (!CompoundTagExtensionsKt.isPokemonEntity(this.getShoulderEntityLeft())) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
        }
        if (!CompoundTagExtensionsKt.isPokemonEntity(this.getShoulderEntityRight())) {
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
        ci.cancel();
    }

    @Override
    public void changeShoulderEntityLeft(@NotNull CompoundTag compoundTag) {
        this.setShoulderEntityLeft(compoundTag);
    }

    @Override
    public void changeShoulderEntityRight(@NotNull CompoundTag compoundTag) {
        this.setShoulderEntityRight(compoundTag);
    }

}