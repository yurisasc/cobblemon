package com.cablemc.pokemoncobbled.common.api

interface LevelCurve {
    fun getExperienceForLevel(level: Int): Int
    fun getLevelForExperience(experience: Int): Int
}

class CachedLevelThresholds(
    val levelLimit: Int = 1000,
    val experienceToLevel: (Int) -> Int
) {
    val savedThresholds = mutableListOf<Int>()
    fun getLevelForExperience(experience: Int): Int {
        var level = 1
        while (level <= savedThresholds.size) {
            val threshold = savedThresholds[level - 1]
            if (experience < threshold) {
                return level - 1
            }
            level++
        }
        while (level < levelLimit) {
            val threshold = experienceToLevel(level)
            savedThresholds.add(threshold)
            if (experience < threshold) {
                return level - 1
            }
            level++
        }
        return 1
    }
}