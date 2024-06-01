package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.google.gson.InstanceCreator
import java.lang.reflect.Type

/**
 * An instance creator for a [PosableModel] that allows for the model to be constructed from a JSON object. This
 * is used to maintain a reference to the model so that other type adapters can call back to it.
 *
 * @author Hiroku
 * @since October 18th, 2022
 */
class JsonModelAdapter<T : PosableModel>(private val constructor: (Bone) -> T) : InstanceCreator<T> {
    var modelPart: Bone? = null
    var model: T? = null
    override fun createInstance(type: Type): T {
        return constructor(modelPart!!).also {
            model = it
            it.loadAllNamedChildren(modelPart!!)
        }
    }
}