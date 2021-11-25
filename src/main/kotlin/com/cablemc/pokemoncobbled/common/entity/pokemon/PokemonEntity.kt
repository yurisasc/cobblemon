package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.entity.EntityProperty
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AgeableMob
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class PokemonEntity(
    level: Level,
    type: EntityType<out PokemonEntity> = PokemonCobbledMod.entityRegistry.POKEMON.get()
) : TamableAnimal(type, level) {
    companion object {
        private val SPECIES_DEX = SynchedEntityData.defineId(PokemonEntity::class.java, EntityDataSerializers.INT)
        private val MOVING = SynchedEntityData.defineId(PokemonEntity::class.java, EntityDataSerializers.BOOLEAN)
    }

    val delegate = if (level.isClientSide) {
        // Don't import because scanning for imports is a CI job we'll do later to detect errant access to client from server
        com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate()
    } else {
        PokemonServerDelegate()
    }

    val entityProperties = mutableListOf<EntityProperty<*>>()
    var pokemon = Pokemon()
    val dexNumber = addEntityProperty(SPECIES_DEX, pokemon.species.nationalPokedexNumber)
    val isMoving = addEntityProperty(MOVING, false)
    // properties like the above are synced and can be subscribed to changes for on either side

    init {
        delegate.initialize(this)
    }

    override fun tick() {
        super.tick()
        entityProperties.forEach { it.checkForUpdate() }
        delegate.tick(this)
    }

    override fun getSpeed() = 0.15F
    override fun registerGoals() {
        goalSelector.addGoal(1, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 5F))
    }

    fun <T> addEntityProperty(accessor: EntityDataAccessor<T>, initialValue: T): EntityProperty<T> {
        val property = EntityProperty(
            entity = this,
            accessor = accessor,
            initialValue = initialValue
        )
        entityProperties.add(property)
        return property
    }

    override fun getBreedOffspring(level: ServerLevel, partner: AgeableMob): AgeableMob? {
        return null
    }
}