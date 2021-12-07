package com.cablemc.pokemoncobbled.client.render.models.blockbench

import net.minecraft.client.model.geom.ModelPart

class ModelPartChain(val parts : List<ModelPart>) {
    /**
     * Called by the setupAnim function in the model
     */
    fun setupAnim(partHandler : (ModelPart, Int) -> Unit) {
        parts.forEachIndexed { index, modelPart ->
            partHandler(modelPart, index+1)
        }
    }
}