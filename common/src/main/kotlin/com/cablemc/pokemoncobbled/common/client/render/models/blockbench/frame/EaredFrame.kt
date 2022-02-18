package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint

interface EaredFrame : ModelFrame {
    val leftEarJoint: EarJoint
    val rightEarJoint: EarJoint
}