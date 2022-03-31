package com.cablemc.pokemoncobbled.common.config

import com.cablemc.pokemoncobbled.common.config.constraint.IntConstraint

class CobbledConfig {

    @NodeCategory(Category.Pokemon)
    @IntConstraint(min = 1, max = 1000)
    var maxPokemonLevel = 100

    // TODO new types of constraint

    @NodeCategory(Category.Spawning)
    var enableSpawning = true

    @NodeCategory(Category.Spawning)
    var minimumDistanceBetweenEntities = 6.0

    @NodeCategory(Category.Spawning)
    var maxNearbyBlocksRange = 8

    @NodeCategory(Category.Spawning)
    var maxHorizontalSpace = 6

    @NodeCategory(Category.Spawning)
    var maxVerticalSpace = 8

    @NodeCategory(Category.Spawning)
    var worldSliceDiameter = 8

    @NodeCategory(Category.Spawning)
    var worldSliceHeight = 8

    @NodeCategory(Category.Spawning)
    var minimumSliceDistanceFromPlayer = 16F

    @NodeCategory(Category.Spawning)
    var maximumSliceDistanceFromPlayer = 28F

    @NodeCategory(Category.Spawning)
    var exportSpawnsToConfig = false

    @NodeCategory(Category.Battles)
    var autoUpdateShowdown = true
}