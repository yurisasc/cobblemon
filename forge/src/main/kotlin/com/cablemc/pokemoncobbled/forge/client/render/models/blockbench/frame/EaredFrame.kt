package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.EarJoint

interface EaredFrame : ModelFrame {
    val leftEarJoint: EarJoint
    val rightEarJoint: EarJoint
}