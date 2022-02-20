package com.cablemc.pokemoncobbled.common.pokemon.activestate

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.isPokemonEntity
import com.cablemc.pokemoncobbled.common.util.party
import com.cablemc.pokemoncobbled.common.util.playSoundServer
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.Level
import java.util.UUID

sealed class PokemonState {
    companion object {
        val states = mapOf(
            "inactive" to InactivePokemonState::class.java,
            "sent-out" to SentOutState::class.java,
            "shouldered" to ShoulderedState::class.java
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
        get() = PokemonCobbled.getLevel(dimension)?.getEntity(entityId) as? PokemonEntity

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
        dimension = ResourceKey.create(ResourceKey.createRegistryKey(dimension.location()), ResourceLocation(buffer.readUtf()))
        return this
    }

    override fun recall() {
        entity?.discard()
    }
}

class ShoulderedState() : ActivePokemonState() {
    var isLeftShoulder = false
    lateinit var playerUUID: UUID
    lateinit var pokemonUUID: UUID
    var stateId = UUID.randomUUID()

    constructor(playerUUID: UUID, isLeftShoulder: Boolean, pokemonUUID: UUID): this() {
        this.isLeftShoulder = isLeftShoulder
        this.playerUUID = playerUUID
        this.pokemonUUID = pokemonUUID
    }

    override val entity: PokemonEntity? = null
    override fun writeToNBT(nbt: CompoundTag): CompoundTag {
        super.writeToNBT(nbt)
        nbt.putBoolean(DataKeys.POKEMON_STATE_SHOULDER, isLeftShoulder)
        nbt.putUUID(DataKeys.POKEMON_STATE_PLAYER_UUID, playerUUID)
        nbt.putUUID(DataKeys.POKEMON_STATE_ID, stateId)
        nbt.putUUID(DataKeys.POKEMON_STATE_POKEMON_UUID, pokemonUUID)
        return nbt
    }

    override fun readFromNBT(nbt: CompoundTag): PokemonState {
        super.readFromNBT(nbt)
        isLeftShoulder = nbt.getBoolean(DataKeys.POKEMON_STATE_SHOULDER)
        playerUUID = nbt.getUUID(DataKeys.POKEMON_STATE_PLAYER_UUID)
        stateId = nbt.getUUID(DataKeys.POKEMON_STATE_ID)
        pokemonUUID = nbt.getUUID(DataKeys.POKEMON_STATE_POKEMON_UUID)
        return this
    }

    override fun writeToJSON(json: JsonObject): JsonObject? {
        super.writeToJSON(json)
        json.addProperty(DataKeys.POKEMON_STATE_SHOULDER, isLeftShoulder)
        json.addProperty(DataKeys.POKEMON_STATE_PLAYER_UUID, playerUUID.toString())
        json.addProperty(DataKeys.POKEMON_STATE_ID, stateId.toString())
        json.addProperty(DataKeys.POKEMON_STATE_POKEMON_UUID, pokemonUUID.toString())
        return json
    }

    override fun readFromJSON(json: JsonObject): PokemonState {
        super.readFromJSON(json)
        isLeftShoulder = json.get(DataKeys.POKEMON_STATE_SHOULDER).asBoolean
        playerUUID = UUID.fromString(json.get(DataKeys.POKEMON_STATE_PLAYER_UUID).asString)
        stateId = UUID.fromString(json.get(DataKeys.POKEMON_STATE_ID).asString)
        pokemonUUID = UUID.fromString(json.get(DataKeys.POKEMON_STATE_POKEMON_UUID).asString)
        return this
    }

    override fun writeToBuffer(buffer: FriendlyByteBuf) {
        super.writeToBuffer(buffer)
        buffer.writeBoolean(isLeftShoulder)
        buffer.writeUUID(playerUUID)
        buffer.writeUUID(stateId)
        buffer.writeUUID(pokemonUUID)
    }

    override fun readFromBuffer(buffer: FriendlyByteBuf): PokemonState {
        super.readFromBuffer(buffer)
        isLeftShoulder = buffer.readBoolean()
        playerUUID = buffer.readUUID()
        stateId = buffer.readUUID()
        pokemonUUID = buffer.readUUID()
        return this
    }

    override fun recall() {
        val player = getServer()!!.playerList.getPlayer(playerUUID) ?: return
        val shoulderNBT = if (isLeftShoulder) player.shoulderEntityLeft else player.shoulderEntityRight
        if (shoulderNBT.isPokemonEntity() && shoulderNBT.getCompound(DataKeys.POKEMON).getCompound(DataKeys.POKEMON_STATE).getUUID(
                DataKeys.POKEMON_STATE_ID) == stateId) {
            player.level.playSoundServer(player.position(), SoundEvents.CANDLE_FALL)
            player.party().find { it.uuid == pokemonUUID }?.let { pkm ->
                pkm.form.shoulderEffects.forEach { it.removeEffect(pkm, player, isLeftShoulder) }
            }
            // Requires mixin to bypass access transformer not existing here
            if (isLeftShoulder) {
                player.shoulderEntityLeft = CompoundTag()
            } else {
                player.shoulderEntityRight = CompoundTag()
            }
        }
    }
}