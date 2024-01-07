package com.cobblemon.mod.common.block.chest

import com.cobblemon.mod.common.api.scheduling.SchedulingTracker
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import net.minecraft.entity.Entity

class GildedState : PoseableEntityState<Entity>() {
    var totalPartialTicks = 0F
    override fun getEntity() = null

    override fun updatePartialTicks(partialTicks: Float) {
        this.totalPartialTicks += partialTicks
        currentPartialTicks += partialTicks
    }

    override val schedulingTracker = SchedulingTracker()
}