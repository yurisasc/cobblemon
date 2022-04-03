package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.entity.EntityAttributeEvent
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.Registry
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes

object CobbledEntities {
    private val registry = DeferredRegister.create(PokemonCobbled.MODID, Registry.ENTITY_TYPE_REGISTRY)
    fun register() {
        registry.register()

        EntityAttributeRegistry.register(
            { POKEMON_TYPE },
            {
                AttributeSupplier
                    .builder()
                    .add(Attributes.FOLLOW_RANGE)
                    .add(Attributes.MAX_HEALTH)
                    .add(Attributes.MOVEMENT_SPEED)
                    .add(Attributes.JUMP_STRENGTH)
                    .add(Attributes.KNOCKBACK_RESISTANCE)
                    .add(Attributes.ARMOR)
                    .also { CobbledEvents.ENTITY_ATTRIBUTE.post(EntityAttributeEvent(POKEMON_TYPE, it)) }
            }
        )
    }
    private fun <T : Entity> entity(
        name: String,
        entityTypeBuilder: EntityType.Builder<T>
    ): RegistrySupplier<EntityType<T>> {
        return registry.register(name) { entityTypeBuilder.build(cobbledResource(name).toString()) }
    }

    private fun <T : LivingEntity> livingEntity(
        name: String,
        entityTypeBuilder: EntityType.Builder<T>
    ): RegistrySupplier<EntityType<T>> {
        return registry.register(name) { entityTypeBuilder.build(cobbledResource("pokemon").toString()) }
    }

    val POKEMON = livingEntity(
        name = "pokemon",
        entityTypeBuilder = EntityType.Builder.of<PokemonEntity>(
            { _, level -> PokemonEntity(level) },
            MobCategory.CREATURE
        )
    )

    val EMPTY_POKEBALL = entity(
        name = "empty_pokeball",
        entityTypeBuilder = EntityType.Builder.of<EmptyPokeBallEntity>(
            { _, level -> EmptyPokeBallEntity(PokeBalls.POKE_BALL, level) },
            MobCategory.MISC
        )
    )

    val POKEMON_TYPE: EntityType<PokemonEntity>
        get() = POKEMON.get()

    val EMPTY_POKEBALL_TYPE: EntityType<EmptyPokeBallEntity>
        get() = EMPTY_POKEBALL.get()
}