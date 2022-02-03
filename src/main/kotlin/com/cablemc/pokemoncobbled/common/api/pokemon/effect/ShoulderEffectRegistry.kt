package com.cablemc.pokemoncobbled.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.common.pokemon.activestate.ShoulderedState
import com.cablemc.pokemoncobbled.common.pokemon.effects.HighJumpEffect
import com.cablemc.pokemoncobbled.common.pokemon.effects.LightSourceEffect
import com.cablemc.pokemoncobbled.common.pokemon.effects.SlowFallEffect
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

/**
 * Registry object for ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
object ShoulderEffectRegistry {
    init {
        MinecraftForge.EVENT_BUS.register(this)
    }
    private val effects = mutableMapOf<String, Class<out ShoulderEffect>>()

    // Effects - START
    val LIGHT_SOURCE = register("light_source", LightSourceEffect::class.java)
    val SLOW_FALL = register("slow_fall", SlowFallEffect::class.java)
    // Effects - END

    fun register(name: String, effect: Class<out ShoulderEffect>) = effect.also { effects[name] = it }

    fun unregister(name: String) = effects.remove(name)

    fun getName(clazz: Class<out ShoulderEffect>) = effects.firstNotNullOf { if (it.value == clazz) it.key else null }

    fun get(name: String): Class<out ShoulderEffect>? = effects[name]

    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.player as ServerPlayer
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

    @SubscribeEvent
    fun onPlayerLeave(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.player as ServerPlayer
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