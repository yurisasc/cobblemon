package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.entity.player.IShoulderable;
import com.cablemc.pokemoncobbled.common.util.CompoundTagExtensionsKt;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity implements IShoulderable {

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, World p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow public void dropShoulderEntity(NbtCompound p_36371_) {}
    @Shadow public abstract NbtCompound getShoulderEntityLeft();
    @Shadow public abstract void setShoulderEntityLeft(NbtCompound pTag);
    @Shadow public abstract NbtCompound getShoulderEntityRight();
    @Shadow public abstract void setShoulderEntityRight(NbtCompound pTag);

    @Inject(method = "dropShoulderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntity(Lnet/minecraft/nbt/NbtCompound;)V", ordinal = 0), cancellable = true)
    private void removeEntitiesOnShoulder$sanitizeCobbledEntity(CallbackInfo ci) {
        if (!CompoundTagExtensionsKt.isPokemonEntity(this.getShoulderEntityLeft())) {
            this.dropShoulderEntity(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new NbtCompound());
        }
        if (!CompoundTagExtensionsKt.isPokemonEntity(this.getShoulderEntityRight())) {
            this.dropShoulderEntity(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new NbtCompound());
        }
        ci.cancel();
    }

    @Override
    public void changeShoulderEntityLeft(@NotNull NbtCompound compoundTag) {
        this.setShoulderEntityLeft(compoundTag);
    }

    @Override
    public void changeShoulderEntityRight(@NotNull NbtCompound compoundTag) {
        this.setShoulderEntityRight(compoundTag);
    }

}