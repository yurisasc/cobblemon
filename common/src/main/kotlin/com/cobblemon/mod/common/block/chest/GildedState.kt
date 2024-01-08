package com.cobblemon.mod.common.block.chest

import com.cobblemon.mod.common.api.scheduling.SchedulingTracker
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import net.minecraft.entity.Entity

class GildedState : PoseableEntityState<Entity>() {
    override fun getEntity() = null

    init {
        setPose("CLOSED")
    }

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks += partialTicks
    }

    override val schedulingTracker = SchedulingTracker()
}