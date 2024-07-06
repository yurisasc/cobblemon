/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution

import com.cobblemon.mod.common.api.pokemon.evolution.*
import com.cobblemon.mod.common.pokemon.evolution.controller.ClientEvolutionController
import com.cobblemon.mod.common.pokemon.evolution.controller.ServerEvolutionController

class CobblemonEvolutionProxy(private val clientSide: Boolean) : EvolutionProxy<EvolutionDisplay, Evolution> {

    private var controller = if (this.clientSide) ClientEvolutionController() else ServerEvolutionController()

    override fun isClient(): Boolean = this.clientSide

    override fun current(): EvolutionController<out EvolutionLike> = this.controller

    override fun client(): EvolutionController<EvolutionDisplay> {
        return this.controller as? EvolutionController<EvolutionDisplay> ?: throw ClassCastException("Cannot use the client implementation from the server side")
    }

    override fun server(): EvolutionController<Evolution> {
        return this.controller as? EvolutionController<Evolution> ?: throw ClassCastException("Cannot use the server implementation from the client side")
    }

    internal fun overrideController(newInstance: EvolutionController<out EvolutionLike>) {
        this.controller = newInstance
    }

}