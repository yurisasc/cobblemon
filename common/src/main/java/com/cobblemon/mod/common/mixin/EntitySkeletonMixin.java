package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.ai.EntityBehaviour;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSkeletonEntity.class)
public class EntitySkeletonMixin extends MobEntity {

    private EntitySkeletonMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At(value = "TAIL"), cancellable = false)
    private void cobblemon$initGoals(CallbackInfo callbackInfo) {
        final AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity) (Object) this;
        // Pokemon Entities
        this.goalSelector.add(
                3,
                new FleeEntityGoal<PokemonEntity>(
                        skeleton,
                        PokemonEntity.class,
                        6.0f,
                        1.0,
                        1.2,
                         entity -> ((PokemonEntity)entity).getBehaviour().getEntityInteract().getAvoidedBySkeleton()
                                 && ((PokemonEntity)entity).getBeamMode() != 1));

        // Players with shoulder mounted Pokemon
        this.goalSelector.add(
                3,
                new FleeEntityGoal<ServerPlayerEntity>(
                        skeleton,
                        ServerPlayerEntity.class,
                        6.0f,
                        1.0,
                        1.2,
                        entity -> EntityBehaviour.Companion.hasSkeletonFearedShoulderMount((ServerPlayerEntity)entity)
                )
        );

    }
}

