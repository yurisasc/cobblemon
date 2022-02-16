package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pose

/**
 * The type of a pose. Used for normalizing pose swapping for all models.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
enum class PoseType {
    /** A grounded stationary and moving pose type. */
    WALK,
    SLEEP,
    /** An air-borne stationary or moving pose type. */
    FLY,
    /** An underwater stationary or moving pose type. */
    SWIM,
    /** A pose for rendering statelessly on the left shoulder. Stateless animations are given the player head yaw, pitch, and ageInTicks. */
    SHOULDER_LEFT,
    /** A pose for rendering statelessly on the right shoulder. Stateless animations are given the player head yaw, pitch, and ageInTicks. */
    SHOULDER_RIGHT,
    /** A pose for rendering in the SummaryUI */
    PROFILE,
    /** A simple type for non-living entities or errant cases. */
    NONE
}