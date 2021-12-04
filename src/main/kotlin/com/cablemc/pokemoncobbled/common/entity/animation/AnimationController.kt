package com.cablemc.pokemoncobbled.common.entity.animation

import com.cablemc.pokemoncobbled.client.render.pokeball.animation.ModelAnimation
import net.minecraft.client.model.EntityModel
import net.minecraft.world.entity.Entity
import net.minecraftforge.eventbus.api.EventPriority

/**
 * Controls which animation an entity is currently running for it's model.
 * The idea here is to register animations with a predicate, to determine when the animation should run.
 * Any time this is ticked, it will determine if the animation should change or continue based on the predicates.
 *
 * This likely will need to be expanded upon in the future or refactored.
 *
 * @author landonjw
 * @since  December 3, 2021
 */
class AnimationController<T : Entity> {

    /**
     * Animations registered in the controller based on priority of the animation.
     * Low priorities should be for animations that will act as default animations, or animations that will very
     * commonly run.
     */
    private var animationsByPriority = Array(EventPriority.values().size) { mutableListOf<AnimationRegistry<T>>() }

    /** The current animation that is running. If this is null, there is currently no animation for the model and it will t-pose. */
    var currentAnimation: ModelAnimation<EntityModel<T>>? = null
        private set
    private var currentAnimationFrame = 0

    /**
     * Ticks the animation controller for a given entity.
     * If given an animation, it will force the animation onto the entity, ticking it if necessary.
     * Otherwise, this will determine the appropriate animation based on the priority sets.
     */
    fun tick(entity: T, animation: ModelAnimation<EntityModel<T>>? = null) {
        val animationToRun = animation ?: getAnimationFromPrioritySet(entity)
        if (currentAnimation == animationToRun && animationToRun != null) {
            currentAnimationFrame++
        } else {
            currentAnimationFrame = 0
            currentAnimation = animationToRun
        }
        // TODO: Run the animation frame
    }

    private fun getAnimationFromPrioritySet(entity: T): ModelAnimation<EntityModel<T>>? {
        for (prioritySet in animationsByPriority) {
            for (animationRegistry in prioritySet) {
                if (animationRegistry.predicate(entity)) {
                    return animationRegistry.animation
                }
            }
        }
        return null
    }

    fun registerAnimation(priority: EventPriority, animation: ModelAnimation<EntityModel<T>>, animationPredicate: (T) -> Boolean) {
        animationsByPriority[priority.ordinal].add(AnimationRegistry(animationPredicate, animation))
    }

}