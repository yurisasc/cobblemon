package com.cablemc.pokemoncobbled.common.entity.pokemon.ai

import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker
import net.minecraft.entity.ai.pathing.BirdPathNodeMaker
import net.minecraft.entity.ai.pathing.MobNavigation
import net.minecraft.world.World

class PokemonNavigation(val world: World, val pokemonEntity: PokemonEntity) : MobNavigation(pokemonEntity, world) {

    fun onPoseChange(newPoseType: PoseType) {
        if (newPoseType in setOf(PoseType.FLY, PoseType.HOVER)) {
            if (nodeMaker !is BirdPathNodeMaker) {
                nodeMaker = BirdPathNodeMaker()
                recalculatePath()
            }
        } else if (nodeMaker !is AmphibiousPathNodeMaker) {
            nodeMaker = AmphibiousPathNodeMaker(!pokemonEntity.behaviour.moving.swim.canBreatheUnderwater)
            recalculatePath()
        }
    }

    override fun isAtValidPosition(): Boolean {
        // Check for like, 'is air? it's ok if flying'
//        return entity.isOnGround || this.isInLiquid || entity.hasVehicle()
        return super.isAtValidPosition()
    }
}