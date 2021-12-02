package com.cablemc.pokemoncobbled.client.util

import com.cablemc.pokemoncobbled.common.util.runOnSide
import net.minecraftforge.fml.LogicalSide

fun <T> runOnClient(block: () -> T) = runOnSide(side = LogicalSide.CLIENT, block)