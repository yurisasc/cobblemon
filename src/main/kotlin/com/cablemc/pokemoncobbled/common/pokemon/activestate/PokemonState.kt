package com.cablemc.pokemoncobbled.common.pokemon.activestate

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

sealed class PokemonState {
    companion object {
        val states = mapOf(
            "inactive" to InactivePokemonState::class.java,
            "sent-out" to SentOutState::class.java
        )

        fun fromBuffer(buffer: FriendlyByteBuf): PokemonState {
            val type = buffer.readUtf()
            return states[type]?.newInstance()?.readFromBuffer(buffer) ?: InactivePokemonState()
        }
    }

    val name: String
        get() = states.entries.find { it.value == this::class.java }!!.key

    open fun writeToNBT(nbt: CompoundTag): CompoundTag? {
        nbt.putString(DataKeys.POKEMON_STATE_TYPE, name)
        return nbt
    }

    open fun readFromNBT(nbt: CompoundTag): PokemonState = this
    open fun writeToJSON(json: JsonObject): JsonObject? = json
    open fun readFromJSON(json: JsonObject): PokemonState = this
    open fun writeToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeUtf(name)
    }
    open fun readFromBuffer(buffer: FriendlyByteBuf): PokemonState = this
}

class InactivePokemonState : PokemonState() {
    override fun writeToNBT(nbt: CompoundTag) = null
}

sealed class ActivePokemonState : PokemonState() {
    abstract val entity: PokemonEntity?
    abstract fun recall()
}

class SentOutState() : ActivePokemonState() {
    private var entityId: Int = -1
    private var dimension = Level.OVERWORLD

    override val entity: PokemonEntity?
        get() = PokemonCobbledMod.getLevel(dimension)?.getEntity(entityId) as? PokemonEntity

    constructor(entity: PokemonEntity): this() {
        this.entityId = entity.id
        this.dimension = entity.level.dimension()
    }

    override fun writeToNBT(nbt: CompoundTag) = null
    override fun writeToJSON(json: JsonObject) = null

    override fun writeToBuffer(buffer: FriendlyByteBuf) {
        super.writeToBuffer(buffer)
        buffer.writeInt(entityId)
        buffer.writeUtf(dimension.location().toString())
    }

    override fun readFromBuffer(buffer: FriendlyByteBuf): SentOutState {
        super.readFromBuffer(buffer)
        entityId = buffer.readInt()
        dimension = ResourceKey.create(ResourceKey.createRegistryKey(dimension.registryName), ResourceLocation(buffer.readUtf()))
        return this
    }

    override fun recall() {

    }
}

class ShoulderedState() : ActivePokemonState() {
    var isLeftShoulder = false

    constructor(isLeftShoulder: Boolean): this() {
        this.isLeftShoulder = isLeftShoulder
    }

    override val entity: PokemonEntity? = null

    override fun recall() {
        TODO("Not yet implemented")
    }
}