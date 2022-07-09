package com.cablemc.pokemoncobbled.common.api.spawning

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.entity.Despawner
import com.cablemc.pokemoncobbled.common.api.spawning.condition.AreaSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.BasicSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.GroundedSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.condition.SubmergedSpawningCondition
import com.cablemc.pokemoncobbled.common.api.spawning.context.*
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.GroundedSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.LavafloorSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SeafloorSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.UnderlavaSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.context.calculators.SubmergedSpawningContextCalculator
import com.cablemc.pokemoncobbled.common.api.spawning.detail.PokemonSpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnAction
import com.cablemc.pokemoncobbled.common.api.spawning.detail.SpawnDetail
import com.cablemc.pokemoncobbled.common.api.spawning.influence.SpawningInfluence
import com.cablemc.pokemoncobbled.common.api.spawning.preset.BasicSpawnDetailPreset
import com.cablemc.pokemoncobbled.common.api.spawning.preset.BestSpawnerConfig
import com.cablemc.pokemoncobbled.common.api.spawning.preset.PokemonSpawnDetailPreset
import com.cablemc.pokemoncobbled.common.api.spawning.preset.SpawnDetailPreset
import com.cablemc.pokemoncobbled.common.api.spawning.prospecting.SpawningProspector
import com.cablemc.pokemoncobbled.common.api.spawning.selection.SpawningSelector
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.AreaSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.FixedAreaSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.PlayerSpawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.PlayerSpawnerFactory
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.Spawner
import com.cablemc.pokemoncobbled.common.api.spawning.spawner.TickingSpawner
import com.cablemc.pokemoncobbled.common.entity.pokemon.CobbledAgingDespawner
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity

/**
 * A grouping of all the overarching behaviours of the Best Spawner system. This is a convenient accessor to
 * the configuration and many other properties used by the spawner.
 *
 * The Best Spawner (in world spawning) works in distinct stages that are:
 * - Prospecting (see: [SpawningProspector])
 * - Context resolving (see: [AreaContextResolver])
 * - Spawn selection (see: [SpawningSelector])
 * - Spawn action (see: [SpawnAction])
 *
 * In the case of more specialized use, the creation of a [SpawningContext] that motivates most of the spawn
 * process can be created manually, skipping the first two steps.
 *
 * An individually spawnable entity is defined as a [SpawnDetail]. A processor handling this process is a [Spawner].
 * Various subclasses exist for more specialized cases. A spawner that is constantly ticking and will spawn things
 * without prompts is a [TickingSpawner], and one of those which occurs within a defined area is a [AreaSpawner]. If
 * that area is unmoving then it is a [FixedAreaSpawner] whereas if it is actively following the player it is a
 * [PlayerSpawner].
 *
 * Spawning is coordinated and ticked using a [SpawnerManager], and all the current managers are accessible from
 * [BestSpawner.spawnerManagers].
 *
 * Spawners and contexts are often put under the effects of [SpawningInfluence]s which can be used to make temporary
 * or lasting changes to spawning for whatever component they are attached to (whether that is a spawner or a context).
 * This pairs strongly with edits to the influence builders inside the [PlayerSpawnerFactory]. The range of effects
 * an influence can have is significant.
 *
 * Broad configuration of this spawning system is found in [BestSpawner.config].
 *
 * @author Hiroku
 * @since July 8th, 2022
 */
object BestSpawner {
    var config = BestSpawnerConfig()
    var presets = mutableMapOf<String, SpawnDetailPreset>()
    val spawnerManagers = mutableListOf<SpawnerManager>(CobbledWorldSpawnerManager)
    var defaultPokemonDespawner: Despawner<PokemonEntity> = CobbledAgingDespawner(getAgeTicks = { it.ticksLived })

    fun loadConfig() {
        LOGGER.info("Starting the Best Spawner...")
        SpawningCondition.register(BasicSpawningCondition.NAME, BasicSpawningCondition::class.java)
        SpawningCondition.register(AreaSpawningCondition.NAME, AreaSpawningCondition::class.java)
        SpawningCondition.register(SubmergedSpawningCondition.NAME, SubmergedSpawningCondition::class.java)
        SpawningCondition.register(GroundedSpawningCondition.NAME, GroundedSpawningCondition::class.java)

        LOGGER.info("Loaded ${SpawningCondition.conditionTypes.size} spawning condition types.")
        SpawningContextCalculator.register(GroundedSpawningContextCalculator)
        SpawningContextCalculator.register(SeafloorSpawningContextCalculator)
        SpawningContextCalculator.register(LavafloorSpawningContextCalculator)
        SpawningContextCalculator.register(SubmergedSpawningContextCalculator)
        SpawningContextCalculator.register(UnderlavaSpawningContextCalculator)

        SpawningContext.register(name = "grounded", clazz = GroundedSpawningContext::class.java, defaultCondition = GroundedSpawningCondition.NAME)
        SpawningContext.register(name = "seafloor", clazz = SeafloorSpawningContext::class.java, defaultCondition = GroundedSpawningCondition.NAME)
        SpawningContext.register(name = "lavafloor", clazz = LavafloorSpawningContext::class.java, defaultCondition = GroundedSpawningCondition.NAME)
        SpawningContext.register(name = "submerged", clazz = SubmergedSpawningContext::class.java, defaultCondition = SubmergedSpawningCondition.NAME)

        LOGGER.info("Loaded ${SpawningContext.contexts.size} spawning context types.")

        SpawnDetail.registerSpawnType(name = PokemonSpawnDetail.TYPE, PokemonSpawnDetail::class.java)
        LOGGER.info("Loaded ${SpawnDetail.spawnDetailTypes.size} spawn detail types.")

        config = BestSpawnerConfig.load()

        SpawnDetailPreset.registerPresetType(BasicSpawnDetailPreset.NAME, BasicSpawnDetailPreset::class.java)
        SpawnDetailPreset.registerPresetType(PokemonSpawnDetailPreset.NAME, PokemonSpawnDetailPreset::class.java)
        presets = SpawnDetailPreset.load()
        LOGGER.info("Loaded ${presets.size} spawn detail presets.")
    }

    fun onServerStarted() {
        spawnerManagers.forEach(SpawnerManager::onServerStarted)
    }
}