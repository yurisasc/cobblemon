package com.cablemc.pokemoncobbled.common.mixin;

import com.cablemc.pokemoncobbled.common.PokemonCobbled;
import com.cablemc.pokemoncobbled.common.api.storage.NoPokemonStoreException;
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore;
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon;
import com.cablemc.pokemoncobbled.common.util.CompoundTagExtensionsKt;
import com.cablemc.pokemoncobbled.common.util.DataKeys;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract NbtCompound getShoulderEntityLeft();

    @Shadow public abstract NbtCompound getShoulderEntityRight();

    @Shadow private long shoulderEntityAddedTime;

    @Shadow public abstract void dropShoulderEntity(NbtCompound entityNbt);

    @Shadow public abstract void setShoulderEntityRight(NbtCompound entityNbt);

    @Shadow public abstract void setShoulderEntityLeft(NbtCompound entityNbt);

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, World p_20967_) {
        super(p_20966_, p_20967_);
    }

    private boolean cobbled$isRightShoulderPokemon = false;
    private boolean cobbled$isLeftShoulderPokemon = false;

    @Inject(method = "dropShoulderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;getEntityFromNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/world/World;)Ljava/util/Optional;"), cancellable = true)
    private void cobbled$removePokemon(NbtCompound nbt, CallbackInfo ci) {
        if (CompoundTagExtensionsKt.isPokemonEntity(nbt)) {
            final UUID uuid = this.getPokemonID(nbt);
            if (this.cobbled$isRightShoulderPokemon) {
                final UUID uuidRight = this.getPokemonID(this.getShoulderEntityRight());
                if (uuid.equals(uuidRight)) {
                    this.recallPokemon(uuidRight);
                }
            }
            if (this.cobbled$isLeftShoulderPokemon) {
                final UUID uuidLeft = this.getPokemonID(this.getShoulderEntityLeft());
                if (uuid.equals(uuidLeft)) {
                    this.recallPokemon(uuidLeft);
                }
            }
            ci.cancel();
        }
    }

    @Inject(method = "setShoulderEntityRight", at = @At("TAIL"))
    private void cobbled$queryRightShoulderPokemon(NbtCompound nbt, CallbackInfo ci) {
        this.cobbled$isRightShoulderPokemon = CompoundTagExtensionsKt.isPokemonEntity(nbt);
    }

    @Inject(method = "setShoulderEntityLeft", at = @At("TAIL"))
    private void cobbled$queryLeftShoulderPokemon(NbtCompound nbt, CallbackInfo ci) {
        this.cobbled$isLeftShoulderPokemon = CompoundTagExtensionsKt.isPokemonEntity(nbt);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private void cobbled$preventFallShoulderDrop(PlayerEntity instance) {
        this.handleNormalShoulderDrop();
    }

    @Redirect(method = "useRiptide", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private void cobbled$preventRiptideShoulderDrop(PlayerEntity instance) {
        this.handleNormalShoulderDrop();
    }

    @Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntities()V"))
    private void cobbled$preventDamagedShoulderDrop(PlayerEntity instance) {
        this.handleNormalShoulderDrop();
    }

    // We do this because we want to prevent the gameplay factors that cause the drop for Pokémon, but we don't want to prevent it during death and spectator.
    private void handleNormalShoulderDrop() {
        if (this.shoulderEntityAddedTime + 20L < this.world.getTime()) {
            if (!this.cobbled$isLeftShoulderPokemon) {
                this.dropShoulderEntity(this.getShoulderEntityLeft());
                this.setShoulderEntityLeft(new NbtCompound());
            }
            if (!this.cobbled$isRightShoulderPokemon) {
                this.dropShoulderEntity(this.getShoulderEntityRight());
                this.setShoulderEntityRight(new NbtCompound());
            }
        }
    }

    private UUID getPokemonID(NbtCompound nbt) {
        return nbt.getCompound(DataKeys.POKEMON)
                .getUuid(DataKeys.POKEMON_UUID);
    }

    private void recallPokemon(UUID uuid) {
        // We need to do this cause the Entity doesn't store a reference to it's storage
        try {
            final PartyStore party = PokemonCobbled.INSTANCE.getStorage().getParty(this.uuid);
            for (Pokemon pokemon : party.getAll()) {
                if (pokemon.getUuid().equals(uuid)) {
                    pokemon.recall();
                }
            }
        } catch (NoPokemonStoreException ignored) {}
    }

}