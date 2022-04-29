package com.cablemc.pokemoncobbled.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.common.pokemon.activestate.ShoulderedState
import com.cablemc.pokemoncobbled.common.pokemon.effects.LightSourceEffect
import com.cablemc.pokemoncobbled.common.pokemon.effects.SlowFallEffect
import com.cablemc.pokemoncobbled.common.util.party
import dev.architectury.event.events.common.PlayerEvent.PLAYER_JOIN
import dev.architectury.event.events.common.PlayerEvent.PLAYER_QUIT
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Registry object for ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
object ShoulderEffectRegistry {
    private val effects = mutableMapOf<String, Class<out ShoulderEffect>>()

    // Effects - START
    val LIGHT_SOURCE = register("light_source", LightSourceEffect::class.java)
    val SLOW_FALL = register("slow_fall", SlowFallEffect::class.java)
    // Effects - END

    fun register() {
        PLAYER_JOIN.register { onPlayerJoin(it) }
        PLAYER_QUIT.register { onPlayerLeave(it) }
    }

    fun register(name: String, effect: Class<out ShoulderEffect>) = effect.also { effects[name] = it }

    fun unregister(name: String) = effects.remove(name)

    fun getName(clazz: Class<out ShoulderEffect>) = effects.firstNotNullOf { if (it.value == clazz) it.key else null }

    fun get(name: String): Class<out ShoulderEffect>? = effects[name]

    fun onPlayerJoin(player: ServerPlayerEntity) {
        player.party().filter { it.state is ShoulderedState }.forEach { pkm ->
            pkm.form.shoulderEffects.forEach {
                it.applyEffect(
                    pokemon = pkm,
                    player = player,
                    isLeft = (pkm.state as ShoulderedState).isLeftShoulder
                )
            }
        }
    }

    fun onPlayerLeave(player: ServerPlayerEntity) {
        player.party().filter { it.state is ShoulderedState }.forEach { pkm ->
            pkm.form.shoulderEffects.forEach {
                it.removeEffect(
                    pokemon = pkm,
                    player = player,
                    isLeft = (pkm.state as ShoulderedState).isLeftShoulder
                )
            }
        }
    }
}