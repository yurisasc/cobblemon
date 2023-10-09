package com.cobblemon.mod.common.api.storage

import net.minecraft.util.Identifier

/**
 * An exception when trying to deserialize a Pokemon instance with an identifier that doesn't seem to be in the registry.
 *
 * @author Hiroku
 * @since October 9th, 2023
 */
class InvalidSpeciesException(val identifier: Identifier) : IllegalStateException("Invalid species: $identifier")