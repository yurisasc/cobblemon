package com.cablemc.pokemoncobbled.common.api.storage.player

import com.cablemc.pokemoncobbled.common.api.storage.player.PlayerDataExtension.Companion.NAME_KEY
import com.cablemc.pokemoncobbled.common.api.storage.player.adapter.JsonPlayerData
import com.google.gson.JsonObject
import java.util.UUID

fun main() {
    val jpd = JsonPlayerData()
    TestExtraData.register()

    val data = jpd.load(UUID.fromString("d269f367-31fa-409f-9d17-6ff149cdba23"))
    for (extras in data.extraData) {
        if (extras.value is TestExtraData) {
            println("Input ${(extras.value as TestExtraData).input}")
        }
    }
    val extra = TestExtraData("KEKW")
    data.starterSelected = true
    data.starterUUID = UUID.randomUUID()
    data.extraData[extra.name()] = extra
    jpd.save(data)
}

class TestExtraData(val input: String) : PlayerDataExtension {

    constructor() : this("")

    companion object {
        fun register() = PlayerDataExtensionRegistry.register("test", TestExtraData::class.java)
    }

    override fun name() = "test"

    override fun serialize(): JsonObject {
        val json = JsonObject()
        json.addProperty(NAME_KEY, name())
        json.addProperty("testData", input)
        return json
    }

    override fun deserialize(json: JsonObject): PlayerDataExtension {
        return TestExtraData(json.get("testData").asString)
    }

}

class PlayerDataStoreManager {
}