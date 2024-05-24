package com.cobblemon.mod.common.api.spawning.fishing

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.fishing.FishingBait
import com.cobblemon.mod.common.api.fishing.FishingBaits
import com.cobblemon.mod.common.api.pokemon.Natures
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.spawning.context.FishingSpawningContext
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnAction
import com.cobblemon.mod.common.api.spawning.detail.SpawnAction
import com.cobblemon.mod.common.api.spawning.influence.SpawningInfluence
import com.cobblemon.mod.common.pokemon.Gender
import com.cobblemon.mod.common.pokemon.IVs
import com.cobblemon.mod.common.util.cobblemonResource
import kotlin.math.min

class FishingBaitInfluence : SpawningInfluence {
    override fun affectAction(action: SpawnAction<*>) {
        if (action !is PokemonSpawnAction || action.ctx !is FishingSpawningContext) return
        val bait = FishingBaits.getFromItemStack(action.ctx.baitStack) ?: return

        bait.effects.forEach { effect ->
            if (Math.random() > effect.chance) return
            when (effect.type) {
                FishingBait.Effects.SHINY_REROLL -> shinyReroll(action, effect)
                FishingBait.Effects.NATURE -> alterNatureAttempt(action, effect)
                FishingBait.Effects.IV -> alterIVAttempt(action, effect)
                FishingBait.Effects.GENDER_CHANCE -> alterGenderAttempt(action, effect)
                FishingBait.Effects.LEVEL_RAISE -> alterLevelAttempt(action, effect)
                FishingBait.Effects.TERA -> alterTeraAttempt(action, effect)
                FishingBait.Effects.HIDDEN_ABILITY_CHANCE -> alterHAAttempt(action, effect)
            }
        }
    }

    private fun shinyReroll(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        if (action.props.shiny != null) return

        val shinyOdds = Cobblemon.config.shinyRate.toInt()
        val randomNumber = kotlin.random.Random.nextInt(0, shinyOdds + 1)

        if (randomNumber <= (effect.value).toInt()) {
            action.props.shiny = true
        }
    }

    private fun alterNatureAttempt(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        if (action.props.nature != null) return

        val possibleNatures = Natures.all().filter { it.increasedStat?.identifier == effect.subcategory }
        if (possibleNatures.isEmpty()) return
        val takenNature = possibleNatures.random()

        action.props.nature = takenNature.name.namespace
    }

    private fun alterIVAttempt(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        if (effect.subcategory == null) return

        if (action.props.ivs == null) action.props.ivs = IVs.createRandomIVs()
        val targettedStat = Stats.getStat(effect.subcategory.path)

        action.props.ivs!![targettedStat] =
            min((action.props.ivs!![targettedStat] ?: 0) + effect.value.toInt(), IVs.MAX_VALUE)
    }

    private fun alterGenderAttempt(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        val gender = effect.subcategory ?: return
        if (action.props.gender != null) return

        if (action.props.gender != Gender.MALE && gender == cobblemonResource("male")) {
            action.props.gender = Gender.MALE
        } else if (action.props.gender != Gender.FEMALE && gender == cobblemonResource("female")) {
            action.props.gender = Gender.FEMALE
        }
    }

    private fun alterLevelAttempt(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        action.props.level =
            (action.props.level ?: action.detail.getDerivedLevelRange().random()) + effect.value.toInt()
    }

    private fun alterTeraAttempt(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        if (action.props.teraType != null) return

        if (effect.subcategory == null) return

        action.props.teraType = effect.subcategory.path
    }

    private fun alterHAAttempt(action: PokemonSpawnAction, effect: FishingBait.Effect) {
        if (action.props.ability != null) return

        val species = action.props.species?.let { PokemonSpecies.getByName(it) } ?: return
        val ability = species.abilities.mapping[Priority.LOW]?.first()?.template?.name ?: return

        action.props.ability = ability
    }
}