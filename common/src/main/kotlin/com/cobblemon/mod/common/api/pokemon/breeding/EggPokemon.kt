package com.cobblemon.mod.common.api.pokemon.breeding

import com.cobblemon.mod.common.api.abilities.Abilities
import com.cobblemon.mod.common.api.abilities.Ability
import com.cobblemon.mod.common.api.moves.MoveSet
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.pokemon.FormData
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.pokemon.Nature
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

class EggPokemon(
    val IVs: IVs,
    val nature: Nature,
    val species: Species,
    val formData: FormData,
    val ability: Ability,
    val moveSet: MoveSet,
    val isShiny: Boolean
) {
    fun toNbt(): NbtCompound {
        val result = NbtCompound()
        val ivNbt = IVs.saveToNBT(NbtCompound())

        result.put(DataKeys.POKEMON_IVS, ivNbt)
        result.putString(DataKeys.POKEMON_NATURE, nature.name.toString())
        result.putString(DataKeys.POKEMON_SPECIES_IDENTIFIER, species.resourceIdentifier.toString())
        result.putString(DataKeys.POKEMON_FORM_ID, formData.formOnlyShowdownId())
        val abilityNBT = ability.saveToNBT(NbtCompound())
        result.put(DataKeys.POKEMON_ABILITY, abilityNBT)
        result.put(DataKeys.POKEMON_MOVESET, moveSet.getNBT())
        result.putBoolean(DataKeys.POKEMON_SHINY, isShiny)
        return result
    }

    companion object {
        // I heard you like casts so I put some casts in your casts
        fun fromNBT(nbt: NbtCompound): EggPokemon {
            val species = PokemonSpecies.getByIdentifier(Identifier.tryParse(DataKeys.POKEMON_SPECIES_IDENTIFIER)!!)!!
            val abilityNBT = nbt.getCompound(DataKeys.POKEMON_ABILITY) ?: NbtCompound()
            val abilityName = abilityNBT.getString(DataKeys.POKEMON_ABILITY_NAME).takeIf { it.isNotEmpty() } ?: "runaway"
            val moveSet = MoveSet()
            return EggPokemon(
                IVs().loadFromNBT(nbt.get(DataKeys.POKEMON_IVS) as NbtCompound) as IVs,
                Natures.getNature(nbt.getString(DataKeys.POKEMON_NATURE))!!,
                species,
                species.forms.first { it.formOnlyShowdownId() == nbt.getString(DataKeys.POKEMON_FORM_ID) },
                Abilities.getOrException(abilityName).create(abilityNBT),
                moveSet.loadFromNBT(nbt.get(DataKeys.POKEMON_MOVESET) as NbtCompound),
                nbt.getBoolean(DataKeys.POKEMON_SHINY)
            )
        }
    }
}
