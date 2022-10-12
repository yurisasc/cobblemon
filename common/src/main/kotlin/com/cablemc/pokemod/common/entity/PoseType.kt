/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.entity

/**
 * The type of a pose. Used for normalizing pose swapping for all models.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
enum class PoseType {
    STAND,
    WALK,
    SLEEP,
    HOVER,
    FLY,
    FLOAT,
    SWIM,
    /** A pose for rendering statelessly on the left shoulder. Stateless animations are given the player head yaw, pitch, and ageInTicks. */
    SHOULDER_LEFT,
    /** A pose for rendering statelessly on the right shoulder. Stateless animations are given the player head yaw, pitch, and ageInTicks. */
    SHOULDER_RIGHT,
    /** A pose for rendering in the SummaryUI */
    PROFILE,
    /** A pose for rendering in the party overlay and in minor spaces like the battle tiles. */
    PORTRAIT,
    /** A simple type for non-living entities or errant cases. */
    NONE
}