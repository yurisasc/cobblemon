package com.cablemc.pokemoncobbled.common.client.render.models.blockbench

import net.minecraft.client.model.geom.ModelPart

/**
 * An ear joint which focuses on a specific part and can only be rotated along a specific axis and range of motion.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
class EarJoint(val modelPart: ModelPart, val axis: Int, val rangeOfMotion: RangeOfMotion)