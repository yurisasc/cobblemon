package com.cobblemon.mod.common.api.events.berry

import com.cobblemon.mod.common.api.berry.Berry

/**
 * The base of all Berry related events.
 *
 * @author Licious
 * @since November 28th, 2022
 */
interface BerryEvent {

    /**
     * The [Berry] related to the event trigger
     */
    val berry: Berry

}

