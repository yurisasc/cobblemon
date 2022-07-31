package com.cablemc.pokemoncobbled.common.battles.dispatch

/**
 * A dispatch that holds the battle until the condition is met.
 *
 * @author Hiroku
 * @since July 31st, 2022
 */
class UntilDispatch(val condition: () -> Boolean) : DispatchResult {
    override fun canProceed() = condition()
}