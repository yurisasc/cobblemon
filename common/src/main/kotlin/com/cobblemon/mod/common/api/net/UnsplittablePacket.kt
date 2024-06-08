package com.cobblemon.mod.common.api.net

/**
 * This marks a packet as "unsplitable". This is used so neoforge doesnt destroy our bufferes when encoding/decoding
 * PokemonDTOs (We allocate buffers, and it kinda breaks when the same instance of PokemonDTO is encoded more than once
 *
 * Technically, even if Neo didnt encode the same packet twice, we wouldnt want to split PokemonDTOs.
 * What happens if half a featuresBuffer is in one packet and the other half is in the other?
 *
 * @author Apion
 * @since June 08, 2024
 */
interface UnsplittablePacket {
}