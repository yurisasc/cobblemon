package com.cablemc.pokemoncobbled.common.entity

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity.createLivingAttributes
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level
import net.minecraftforge.event.entity.EntityAttributeCreationEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fmllegacy.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object EntityRegistry {
    private val ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, PokemonCobbled.MODID)
    val POKEMON : RegistryObject<EntityType<PokemonEntity>> = registerEntity(
        name = "pokemon",
        classification = MobCategory.MISC,
        factory = { _, level -> PokemonEntity(level) }, // TODO Landon's use of an actual factory is still a great idea for here
        builderModifiers = { builder -> builder.sized(1f, 1f).fireImmune() }
    )
    val EMPTY_POKEBALL : RegistryObject<EntityType<EmptyPokeBallEntity>> = registerEntity(
        name = "empty_pokeball",
        classification = MobCategory.MISC,
        factory = { type, level -> EmptyPokeBallEntity(type, level) },
        builderModifiers = { builder -> builder.sized(1f, 1f).fireImmune() } // TODO: Specify better modifiers
    )

    private inline fun <reified T : Entity> registerEntity(
        name: String,
        classification: MobCategory,
        crossinline factory: (EntityType<T>, Level) -> T,
        builderModifiers: (EntityType.Builder<T>) -> EntityType.Builder<T>
    ): RegistryObject<EntityType<T>> {
        val entityFactory: EntityType.EntityFactory<T> = EntityType.EntityFactory { type, level ->
            return@EntityFactory factory(type, level)
        }
        val builder = EntityType.Builder.of(entityFactory, classification)
        val type = builderModifiers(builder).build(cobbledResource(name).toString())
        return ENTITIES.register(name) { type }
    }

    fun registerAttributes(event: EntityAttributeCreationEvent) {
        event.put(POKEMON.get(), createLivingAttributes()
            .add(Attributes.FOLLOW_RANGE) // TODO: Probably not needed?
            .build())
    }

    fun register(bus: IEventBus) {
        ENTITIES.register(bus)
    }
}