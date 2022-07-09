package com.cablemc.pokemoncobbled.common.net.messages.client.battle

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonPropertyExtractor
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stat
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.status.PersistentStatus
import com.cablemc.pokemoncobbled.common.util.readMapK
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeMapK
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import java.util.UUID
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

/**
 * Initializes the client's understanding of a battle. This can be for a participant or for a spectator.
 *
 * Handled by [com.cablemc.pokemoncobbled.common.client.net.battle.BattleInitializeHandler].
 *
 * @author Hiroku
 * @since May 10th, 2022
 */
class BattleInitializePacket() : NetworkPacket {

    lateinit var battleId: UUID
    lateinit var battleFormat: BattleFormat

    lateinit var side1: BattleSideDTO
    lateinit var side2: BattleSideDTO

    constructor(battle: PokemonBattle): this() {
        battleId = battle.battleId
        battleFormat = battle.format
        val sides = arrayOf(battle.side1, battle.side2).map { side ->
            BattleSideDTO(
                actors = side.actors.map { actor ->
                    BattleActorDTO(
                        uuid = actor.uuid,
                        showdownId = actor.showdownId,
                        displayName = actor.getName(),
                        activePokemon = actor.activePokemon.map { it.battlePokemon?.let(ActiveBattlePokemonDTO::fromPokemon) },
                        type = actor.type
                    )
                }
            )
        }
        side1 = sides[0]
        side2 = sides[1]
    }

    override fun encode(buffer: PacketByteBuf) {
        buffer.writeUuid(battleId)
        battleFormat.saveToBuffer(buffer)
        for (side in arrayOf(side1, side2)) {
            buffer.writeSizedInt(IntSize.U_BYTE, side.actors.size)
            for (actor in side.actors) {
                buffer.writeUuid(actor.uuid)
                buffer.writeText(actor.displayName)
                buffer.writeString(actor.showdownId)
                buffer.writeSizedInt(IntSize.U_BYTE, actor.activePokemon.size)
                for (activePokemon in actor.activePokemon) {
                    buffer.writeBoolean(activePokemon != null)
                    activePokemon?.saveToBuffer(buffer)
                }
                buffer.writeSizedInt(IntSize.U_BYTE, actor.type.ordinal)
            }
        }
    }

    override fun decode(buffer: PacketByteBuf) {
        battleId = buffer.readUuid()
        battleFormat = BattleFormat.loadFromBuffer(buffer)
        val sides = mutableListOf<BattleSideDTO>()
        repeat(times = 2) {
            val actors = mutableListOf<BattleActorDTO>()
            repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
                val uuid = buffer.readUuid()
                val displayName = buffer.readText().copy()
                val showdownId = buffer.readString()
                val activePokemon = mutableListOf<ActiveBattlePokemonDTO?>()
                repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) {
                    if (buffer.readBoolean()) {
                        activePokemon.add(ActiveBattlePokemonDTO.loadFromBuffer(buffer))
                    } else {
                        activePokemon.add(null)
                    }
                }
                val type = ActorType.values()[buffer.readSizedInt(IntSize.U_BYTE)]
                actors.add(
                    BattleActorDTO(
                        uuid = uuid,
                        displayName = displayName,
                        showdownId = showdownId,
                        activePokemon = activePokemon,
                        type = type
                    )
                )
            }
            sides.add(BattleSideDTO(actors))
        }
        side1 = sides[0]
        side2 = sides[1]
    }

    data class BattleSideDTO(val actors: List<BattleActorDTO>)

    data class BattleActorDTO(
        val uuid: UUID,
        val displayName: MutableText,
        val showdownId: String,
        val activePokemon: List<ActiveBattlePokemonDTO?>,
        val type: ActorType
    )

    data class ActiveBattlePokemonDTO(
        val uuid: UUID,
        val displayName: MutableText,
        val properties: PokemonProperties,
        val status: PersistentStatus?,
        val hpRatio: Float,
        val statChanges: MutableMap<Stat, Int>
    ) {
        companion object {
            fun fromPokemon(battlePokemon: BattlePokemon) = with(battlePokemon.effectedPokemon) {
                ActiveBattlePokemonDTO(
                    uuid = uuid,
                    displayName = species.translatedName,
                    properties = createPokemonProperties(
                        PokemonPropertyExtractor.SPECIES,
                        PokemonPropertyExtractor.LEVEL,
                        PokemonPropertyExtractor.ASPECTS
                    ),
                    status = status?.status,
                    hpRatio = battlePokemon.health / battlePokemon.maxHealth.toFloat(),
                    statChanges = battlePokemon.statChanges
                )
            }

            fun loadFromBuffer(buffer: PacketByteBuf): ActiveBattlePokemonDTO {
                val uuid = buffer.readUuid()
                val pokemonDisplayName = buffer.readText().copy()
                val properties = PokemonProperties.parse(buffer.readString(), delimiter = " ")
                val status = if (buffer.readBoolean()) PersistentStatus(Identifier(buffer.readString())) else null
                val hpRatio = buffer.readFloat()
                val statChanges = mutableMapOf<Stat, Int>()
                buffer.readMapK(size = IntSize.U_BYTE, statChanges) {
                    val stat = Stats.getStat(buffer.readString())
                    val stages = buffer.readSizedInt(IntSize.BYTE)
                    stat to stages
                }
                return ActiveBattlePokemonDTO(
                    uuid = uuid,
                    displayName = pokemonDisplayName,
                    properties = properties,
                    status = status,
                    hpRatio = hpRatio,
                    statChanges = statChanges
                )
            }
        }

        fun saveToBuffer(buffer: PacketByteBuf): ActiveBattlePokemonDTO {
            buffer.writeUuid(uuid)
            buffer.writeText(displayName)
            buffer.writeString(properties.asString())
            buffer.writeBoolean(status != null)
            status?.let { buffer.writeString(it.name.toString()) }
            buffer.writeFloat(hpRatio)
            buffer.writeMapK(IntSize.U_BYTE, statChanges) { (stat, stages) ->
                buffer.writeString(stat.id)
                buffer.writeSizedInt(IntSize.BYTE, stages)
            }
            return this
        }
    }
}