package com.cobblemon.mod.common.api.fossil

import com.cobblemon.mod.common.api.data.DataRegistry
import com.cobblemon.mod.common.api.data.JsonDataRegistry
import com.cobblemon.mod.common.api.reactive.SimpleObservable
import com.cobblemon.mod.common.util.adapters.IdentifierAdapter
import com.cobblemon.mod.common.util.cobblemonResource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.registry.Registries
import net.minecraft.resource.ResourceType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

object NaturalMaterials : JsonDataRegistry<NaturalMaterial>{
    override val id = cobblemonResource("natural_materials")
    override val type = ResourceType.SERVER_DATA
    override val observable = SimpleObservable<NaturalMaterials>()
    override val typeToken: TypeToken<NaturalMaterial> = TypeToken.get(NaturalMaterial::class.java)
    override val resourcePath = "natural_materials"
    override val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(Identifier::class.java, IdentifierAdapter::class.java)
        .create()

    val resourceData = mutableMapOf<Identifier, NaturalMaterial>()
    val containedItems = mutableSetOf<Identifier>()
    override fun sync(player: ServerPlayerEntity) {}

    override fun reload(data: Map<Identifier, NaturalMaterial>) {
        data.forEach {
            resourceData.remove(it.key)
            registerFromData(it.key, it.value)
        }
    }

    private fun registerFromData(identifier: Identifier, mat: NaturalMaterial) {
        resourceData[identifier] = mat
        containedItems.add(mat.item ?: return)
    }

    fun isNaturalMaterial(item: Identifier): Boolean {
        return item in containedItems
    }
}
