/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.riding

import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.riding.controller.RideControllerDeserializer
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLandController
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLandControllerDeserializer
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLiquidController
import com.cobblemon.mod.common.pokemon.riding.controllers.GenericLiquidControllerAdapter
import com.cobblemon.mod.common.pokemon.riding.controllers.SwimDashController
import com.cobblemon.mod.common.pokemon.riding.controllers.SwimDashControllerDeserializer

object RidingModule {

    fun configure() {
        CobblemonEvents.REGISTER_RIDING_CONTROLLER_ADAPTER.subscribe {
            it.register(GenericLandController.KEY, GenericLandControllerDeserializer)
            it.register(GenericLiquidController.KEY, GenericLiquidControllerAdapter)
            it.register(SwimDashController.KEY, SwimDashControllerDeserializer)
        }
    }

}