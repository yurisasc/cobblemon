package com.cobblemon.mod.common.api.pokemon.breeding

import net.minecraft.util.Identifier

/**
 * Represents a pattern on an egg
 *
 * @author Apion
 * @since January 2nd, 2024
 */
data class EggPattern (
    val model: Identifier,
    val baseTexturePath: Identifier,
    val overlayTexturePath: Identifier?,
    val baseInvSpritePath: Identifier,
    val overlayInvSpritePath: Identifier?,
)