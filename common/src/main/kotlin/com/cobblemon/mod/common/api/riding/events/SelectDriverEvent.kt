package com.cobblemon.mod.common.api.riding.events

import net.minecraft.entity.LivingEntity

data class SelectDriverEvent(val primary: DriverSuggestion, val options: Set<LivingEntity>) {

    private var result: DriverSuggestion = primary

    fun result(): LivingEntity {
        return this.result.entity
    }

    fun suggest(target: LivingEntity, priority: Int) {
        if(priority > this.result.priority) {
            this.result = DriverSuggestion(target, priority)
        }
    }

    data class DriverSuggestion(val entity: LivingEntity, val priority: Int)

}