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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin extends LivingEntity {

    @Shadow public abstract NbtCompound getShoulderEntityLeft();

    @Shadow public abstract NbtCompound getShoulderEntityRight();

    @Shadow public abstract void dropShoulderEntity(NbtCompound entityNbt);

    @Shadow public abstract void setShoulderEntityRight(NbtCompound entityNbt);

    @Shadow public abstract void setShoulderEntityLeft(NbtCompound entityNbt);

    @Shadow public abstract boolean isSpectator();

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, World p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "dropShoulderEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;getEntityFromNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/world/World;)Ljava/util/Optional;"), cancellable = true)
    private void cobbled$removePokemon(NbtCompound nbt, CallbackInfo ci) {
        if (CompoundTagExtensionsKt.isPokemonEntity(nbt)) {
            final UUID uuid = this.getPokemonID(nbt);
            if (this.isShoulderPokemon(this.getShoulderEntityRight())) {
                final UUID uuidRight = this.getPokemonID(this.getShoulderEntityRight());
                if (uuid.equals(uuidRight)) {
                    this.recallPokemon(uuidRight);
                    this.setShoulderEntityRight(new NbtCompound());
                }
            }
            if (this.isShoulderPokemon(this.getShoulderEntityLeft())) {
                final UUID uuidLeft = this.getPokemonID(this.getShoulderEntityLeft());
                if (uuid.equals(uuidLeft)) {
                    this.recallPokemon(uuidLeft);
                    this.setShoulderEntityLeft(new NbtCompound());
                }
            }
            ci.cancel();
        }
    }

    @Inject(
        method = "dropShoulderEntities",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;dropShoulderEntity(Lnet/minecraft/nbt/NbtCompound;)V",
            ordinal = 0
        ),
        cancellable = true
    )
    private void cobbled$preventPokemonDropping(CallbackInfo ci) {
        // We want to allow both of these to forcefully remove the entities
        if (this.isSpectator() || this.isDead())
            return;
        if (!this.isShoulderPokemon(this.getShoulderEntityLeft())) {
            this.dropShoulderEntity(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new NbtCompound());
        }
        if (!this.isShoulderPokemon(this.getShoulderEntityRight())) {
            this.dropShoulderEntity(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new NbtCompound());
        }
        ci.cancel();
    }

    private UUID getPokemonID(NbtCompound nbt) {
        return nbt.getCompound(DataKeys.POKEMON)
                .getUuid(DataKeys.POKEMON_UUID);
    }

    private void recallPokemon(UUID uuid) {
        // We need to do this cause the Entity doesn't store a reference to it's storage
        try {
            final PartyStore party = PokemonCobbled.INSTANCE.getStorage().getParty(this.uuid);
            for (Pokemon pokemon : party) {
                if (pokemon.getUuid().equals(uuid)) {
                    pokemon.recall();
                }
            }
        } catch (NoPokemonStoreException ignored) {}
    }

    private boolean isShoulderPokemon(NbtCompound nbt) {
        return CompoundTagExtensionsKt.isPokemonEntity(nbt);
    }

}