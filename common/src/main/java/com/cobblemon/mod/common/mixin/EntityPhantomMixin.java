package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.ai.EntityBehaviour;
import com.cobblemon.mod.common.villager.VillagerGatherableItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.util.Objects;

@Mixin(targets = "net/minecraft/entity/mob/PhantomEntity$SwoopMovementGoal")
public class EntityPhantomMixin {
    // TODO: Figure out how to access the outer class that wraps SwoopMovementGoal
    // Need to find the cat mons that are near the phantom

//    @Inject(method = "shouldContinue", at = @At(value = "RETURN"), cancellable = true)
//    public void cobblemon$shouldContinue(CallbackInfoReturnable<Boolean> ci) {
////        var goalSelectorField = PhantomEntity.class.getClasses();// PhantomEntity.class.getFields();// .class.getDeclaredField("goalSelector");
//
////        goalSelectorField.setAccessible(true);
////        GoalSelector goalSelector = (GoalSelector) goalSelectorField.get(mobEntity);
////        final SwoopMovementGoal phantom = (SwoopMovementGoal) (Object) this;
////        if(ci.getReturnValue() == false) {
////            var result = this$0.getWorld().getEntitiesByClass(
////                    PokemonEntity.class,
////                    this$0.getBoundingBox().expand(16.0),
////                    entity ->  {
////                        return ((PokemonEntity) entity).getBehaviour().getEntityInteract().getAvoidedByPhantom();
////                    }
////            ).isEmpty();
////
////                ci.setReturnValue(result);
////        }
//    }
}

