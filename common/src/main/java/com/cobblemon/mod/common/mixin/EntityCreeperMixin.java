package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.ai.EntityBehaviour;
import com.cobblemon.mod.common.pokemon.ai.PokemonBehaviour;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public class EntityCreeperMixin extends MobEntity {

    private EntityCreeperMixin(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At(value = "TAIL"), cancellable = false)
    private void cobblemon$initGoals(CallbackInfo callbackInfo) {
        final CreeperEntity creeper = (CreeperEntity) (Object) this;
        // Pokemon Entities
        this.goalSelector.add(
                3,
                new FleeEntityGoal<PokemonEntity>(
                        creeper,
                        PokemonEntity.class,
                        6.0f,
                        1.0,
                        1.2,
                         entity -> ((PokemonEntity)entity).getBehaviour().getEntityInteract().getAvoidedByCreeper()
                                 && ((PokemonEntity)entity).getBeamMode() != 1));

        // Players with shoulder mounted Pokemon
        this.goalSelector.add(
                3,
                new FleeEntityGoal<ServerPlayerEntity>(
                        creeper,
                        ServerPlayerEntity.class,
                        6.0f,
                        1.0,
                        1.2,
                        entity ->
                            EntityBehaviour.Companion.hasCreeperFearedShoulderMount((ServerPlayerEntity)entity))
                        );

    }
}

