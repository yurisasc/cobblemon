package com.cablemc.pokemoncobbled.common.util

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Drawable

fun Drawable.scaleIt(value: Number) = (MinecraftClient.getInstance().window.scaleFactor * value.toFloat()).toInt()