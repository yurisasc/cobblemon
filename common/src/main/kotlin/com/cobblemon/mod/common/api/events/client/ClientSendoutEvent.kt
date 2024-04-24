package com.cobblemon.mod.common.api.events.client

import com.cobblemon.mod.common.api.events.Cancelable
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f

data class ClientSendoutEvent(var sendoutOffset: Vec3d, var startPos: Vec3d, val pokemon: PokemonEntity)