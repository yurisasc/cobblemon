package com.cablemc.pokemoncobbled.client.render.models.blockbench.pose

/**
 * The type of a pose. Used for normalizing pose swapping for all models.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
enum class PoseType {
    /** An on-ground stationary and moving pose type. */
    WALK,
    SLEEP,
    /** An air-borne stationary or moving pose type. */
    FLY,
    /** An underwater stationary or moving pose type. */
    SWIM,
    /** A pose for rendering statelessly on the shoulder. */
    SHOULDER,
    /** A simple type for non-living entities or errant cases. */
    NONE
}