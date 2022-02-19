package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.util.CompoundTagExtensionsKt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow
    private long timeEntitySatOnShoulder;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    /**
     * @author Cobbled Mod Team
     * mixin requires this
     * @reason Enabling the removal of entities on the shoulder
     */
    @Overwrite
    public void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
            if (!CompoundTagExtensionsKt.isPokemonEntity(this.getShoulderEntityLeft())) {
                this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
                this.setShoulderEntityLeft(new CompoundTag());
            }
            if (!CompoundTagExtensionsKt.isPokemonEntity(this.getShoulderEntityRight())) {
                this.respawnEntityOnShoulder(this.getShoulderEntityRight());
                this.setShoulderEntityRight(new CompoundTag());
            }
        }
    }

    @Shadow
    private void respawnEntityOnShoulder(CompoundTag p_36371_) {}

    @Shadow
    public abstract CompoundTag getShoulderEntityLeft();

    @Shadow
    protected abstract void setShoulderEntityLeft(CompoundTag pTag);

    @Shadow
    public abstract CompoundTag getShoulderEntityRight();

    @Shadow
    protected abstract void setShoulderEntityRight(CompoundTag pTag);
}