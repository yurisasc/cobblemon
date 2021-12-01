package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.util.NbtKeys
import com.cablemc.pokemoncobbled.common.util.pokemonStatsOf
import net.minecraft.nbt.CompoundTag
import java.util.UUID

class Pokemon {
    var uuid: UUID = UUID.randomUUID()
    var species: Species = PokemonSpecies.EEVEE
    var form: PokemonForm = species.forms.first()

    var health = 10
    var level = 5
    var stats = pokemonStatsOf(
        Stats.HP to 20,
        Stats.ATTACK to 10,
        Stats.DEFENCE to 10,
        Stats.SPECIAL_ATTACK to 10,
        Stats.SPECIAL_DEFENCE to 10,
        Stats.SPEED to 15
    )
    var scaleModifier = 1f

    fun save(nbt: CompoundTag): CompoundTag {
        nbt.putUUID(NbtKeys.POKEMON_UUID, uuid)
        nbt.putShort(NbtKeys.POKEMON_SPECIES_DEX, species.nationalPokedexNumber.toShort())
        nbt.putString(NbtKeys.POKEMON_FORM_ID, form.name)
        nbt.putShort(NbtKeys.POKEMON_LEVEL, level.toShort())
        nbt.putShort(NbtKeys.POKEMON_HEALTH, health.toShort())
        nbt.put(NbtKeys.POKEMON_STATS, stats.save(CompoundTag()))
        nbt.putFloat(NbtKeys.POKEMON_SCALE_MODIFIER, scaleModifier)
        return nbt
    }

    fun load(nbt: CompoundTag): Pokemon {
        uuid = nbt.getUUID(NbtKeys.POKEMON_UUID)
        species = PokemonSpecies.getByPokedexNumber(nbt.getInt(NbtKeys.POKEMON_SPECIES_DEX))
            ?: throw IllegalStateException("Tried to read a species with national pokedex number ${nbt.getInt(NbtKeys.POKEMON_SPECIES_DEX)}")
        form = species.forms.find { it.name == nbt.getString(NbtKeys.POKEMON_FORM_ID) } ?: species.forms.first()
        level = nbt.getShort(NbtKeys.POKEMON_LEVEL).toInt()
        health = nbt.getShort(NbtKeys.POKEMON_HEALTH).toInt()
        stats = PokemonStats().load(nbt.getCompound(NbtKeys.POKEMON_STATS))
        scaleModifier = nbt.getFloat(NbtKeys.POKEMON_SCALE_MODIFIER)
        return this
    }

    fun generateIntrinsics() {

    }

    fun generateSpecies() {

    }


}