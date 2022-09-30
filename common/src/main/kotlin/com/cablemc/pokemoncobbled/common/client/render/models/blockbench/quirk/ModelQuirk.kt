package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.quirk

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import net.minecraft.entity.Entity

abstract class ModelQuirk<T : Entity, D : QuirkData<T>>(val name: String) {
    abstract fun createData(): D
    protected abstract fun tick(state: PoseableEntityState<T>, data: D)
    fun tick(entity: T?, model: PoseableEntityModel<T>, state: PoseableEntityState<T>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, headYaw: Float, headPitch: Float) {
        val data = getOrCreateData(state)
        tick(state, data)
        data.run(entity, model, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
    }
    fun getOrCreateData(state: PoseableEntityState<T>): D {
        return state.quirks.getOrPut(this, this::createData) as D
    }
}