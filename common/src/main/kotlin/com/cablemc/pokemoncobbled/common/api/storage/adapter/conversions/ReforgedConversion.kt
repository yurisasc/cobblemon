package com.cablemc.pokemoncobbled.common.api.storage.adapter.conversions

import com.cablemc.pokemoncobbled.common.api.moves.Moves
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.pokemon.Natures
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.pokemon.experience.SidemodExperienceSource
import com.cablemc.pokemoncobbled.common.api.pokemon.feature.SpeciesFeature
import com.cablemc.pokemoncobbled.common.api.pokemon.stats.Stats
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.adapter.CobbledAdapter
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.pokemon.EVs
import com.cablemc.pokemoncobbled.common.pokemon.Gender
import com.cablemc.pokemoncobbled.common.pokemon.IVs
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import net.minecraft.util.Identifier
import java.nio.file.Path
import java.util.UUID

class ReforgedConversion(val base: Path) : CobbledConverter<NbtCompound> {

    override fun root(): Path {
        return this.base.resolve("data").resolve("pokemon")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : StorePosition, T : PokemonStore<E>> load(storeClass: Class<T>, uuid: UUID): T? {
        val extension = if (storeClass.simpleName.lowercase() == "playerpartystore") "pk" else ("comp")
        val target = this.root().resolve("$uuid.$extension")

        if(!this.exists(target)) {
            return null
        }

        val nbt = NbtIo.read(target.toFile())
        if(nbt != null) {
            return (if (extension == "pk") party(uuid, nbt) else pc(uuid, nbt)) as T
        }

        return null
    }

    override fun party(user: UUID, nbt: NbtCompound) : PlayerPartyStore {
        val result = PlayerPartyStore(user)
        for(x in 0..5) {
            val key = "party$x"
            if(nbt.contains(key)) {
                result.add(this.translate(nbt.getCompound(key)))
            }
        }

        return result
    }

    override fun pc(user: UUID, nbt: NbtCompound) : PCStore {
        val result = PCStore(user)
        return result
    }

    override fun translate(nbt: NbtCompound) : Pokemon {
        val result = Pokemon()
        result.uuid = nbt.getUuid("UUID")
        result.species = PokemonSpecies.getByPokedexNumber(nbt.getInt("ndex"))
            ?: throw IllegalStateException("Failed to read a species with pokedex identifier ${nbt.getInt("ndex")}")
        result.form = result.species.forms.find { it.name == nbt.getString("Variant") } ?: result.species.standardForm


        result.gender = Gender.values()[nbt.getInt("Gender")]
        result.shiny = this.find(nbt, "IsShiny", NbtCompound::getBoolean) ?:
                        this.find(nbt, "palette", NbtCompound::getString)?.equals("shiny") ?: false
        result.level = nbt.getInt("Level")
        result.addExperience(SidemodExperienceSource("Reforged"), nbt.getInt("EXP"))
        result.friendship = nbt.getInt("Friendship")
        result.ability = (result.form.abilities.find { it.template.name == nbt.getString("Ability") } ?: result.form.abilities.first())
            .template.create()
        result.nature = Natures.getNature(Identifier(ReforgedNatures.values()[nbt.getInt("Nature")].name.lowercase())) ?: Natures.getRandomNature()
        result.mintedNature = Natures.getNature(Identifier(ReforgedNatures.values()[nbt.getInt("MintNature")].name.lowercase()))
        result.currentHealth = nbt.getInt("Health")

        // Stats
        val ivs = IVs()
        ivs[Stats.HP] = nbt.getInt("IVHP")
        ivs[Stats.ATTACK] = nbt.getInt("IVAttack")
        ivs[Stats.DEFENCE] = nbt.getInt("IVDefense")
        ivs[Stats.SPECIAL_ATTACK] = nbt.getInt("IVSpAtt")
        ivs[Stats.SPECIAL_DEFENCE] = nbt.getInt("IVSpDef")
        ivs[Stats.SPEED] = nbt.getInt("IVSpeed")

        val evs = EVs()
        evs[Stats.HP] = nbt.getInt("EVHP")
        evs[Stats.ATTACK] = nbt.getInt("EVAttack")
        evs[Stats.DEFENCE] = nbt.getInt("EVDefense")
        evs[Stats.SPECIAL_ATTACK] = nbt.getInt("EVSpecialAttack")
        evs[Stats.SPECIAL_DEFENCE] = nbt.getInt("EVSpecialDefense")
        evs[Stats.SPEED] = nbt.getInt("EVSpeed")

        result.evs = evs
        result.ivs = ivs

        for (move in nbt.getList("Moveset", 10)) {
            val compound = move as NbtCompound
            val id = compound.getString("MoveID").replace(Regex("[-\\s]", RegexOption.IGNORE_CASE), "")
            val pp = compound.getInt("MovePP")
            val level = compound.getInt("MovePPLevel")

            val template = Moves.getByNameOrDummy(id.lowercase())
            result.moveSet.add(template.create(pp, level))
        }

        // TODO - Nicknames and Original Trainer Data
        // result.nickname = this.find(nbt, "Nickname", NbtCompound::getString)

        val ball = this.find(nbt, "CaughtBall", NbtCompound::getString)
        result.caughtBall = if(ball != null) PokeBalls.getPokeBall(Identifier(ball)) ?: PokeBalls.POKE_BALL else PokeBalls.POKE_BALL

        return result
    }

    fun <T> find(nbt: NbtCompound, key: String, translator: Translator<T?>) : T? {
        if (nbt.contains(key)) {
            return translator.from(nbt, key)
        }

        return null
    }

    fun interface Translator<out R> {
        fun from(nbt: NbtCompound, key: String) : R?
    }

    enum class ReforgedNatures {
        HARDY,
        SERIOUS,
        DOCILE,
        BASHFUL,
        QUIRKY,
        LONELY,
        BRAVE,
        ADAMANT,
        NAUGHTY,
        BOLD,
        RELAXED,
        IMPISH,
        LAX,
        TIMID,
        HASTY,
        JOLLY,
        NAIVE,
        MODEST,
        MILD,
        QUIET,
        RASH,
        CALM,
        GENTLE,
        SASSY,
        CAREFUL,
    }

}