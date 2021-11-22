package com.cablemc.pokemoncobbled.common.entity

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.resources.ResourceLocation
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

class EntityRegistry {
    private val ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, PokemonCobbled.MODID)
    val POKEMON: RegistryObject<EntityType<PokemonEntity>> = registerEntity(
        name = "assets/pokemoncobbled/pokemon",
        classification = MobCategory.MISC,
        factory = { type, level -> PokemonEntity(type, level) }, // TODO Landon's use of an actual factory is still a great idea for here
        builderModifiers = { builder -> builder.sized(1f, 1f).fireImmune() }
    )

    private inline fun <reified T : Entity> registerEntity(
        name: String,
        classification: MobCategory,
        crossinline factory: (EntityType<T>, Level) -> T,
        builderModifiers: (EntityType.Builder<T>) -> EntityType.Builder<T>
    ): RegistryObject<EntityType<T>> {
        val resourceLoc = ResourceLocation(PokemonCobbled.MODID, name)
        val entityFactory: EntityType.EntityFactory<T> = EntityType.EntityFactory { type, level ->
            return@EntityFactory factory(type, level)
        }
        val builder = EntityType.Builder.of(entityFactory, classification)
        val type = builderModifiers(builder).build(resourceLoc.toString())
        return ENTITIES.register(name) { type }
    }

    fun registerAttributes(event: EntityAttributeCreationEvent) {
        println("Registering attribute")
        event.put(POKEMON.get(), createLivingAttributes()
            .add(Attributes.FOLLOW_RANGE) // TODO: Probably not needed?
            .build())
    }

    fun register(bus: IEventBus) {
        ENTITIES.register(bus)
    }
}