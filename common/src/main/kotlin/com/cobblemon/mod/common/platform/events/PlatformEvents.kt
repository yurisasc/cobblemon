/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.platform.events

import com.cobblemon.mod.common.api.reactive.CancelableObservable
import com.cobblemon.mod.common.api.reactive.EventObservable

/**
 * A class for our converted events from platform specific to a trigger on common.
 * If you're using this class as API it is recommended that you use the platform specific events instead.
 *
 * @author Licious
 * @since February 13th, 2023
 */
object PlatformEvents {

    val SERVER_STARTING = EventObservable<ServerEvent.Starting>()
    val SERVER_STARTED = EventObservable<ServerEvent.Started>()
    val SERVER_STOPPING = EventObservable<ServerEvent.Stopping>()
    val SERVER_STOPPED = EventObservable<ServerEvent.Stopped>()

    val SERVER_TICK_PRE = EventObservable<ServerTickEvent.Pre>()
    val SERVER_TICK_POST = EventObservable<ServerTickEvent.Post>()

    val CLIENT_TICK_PRE = EventObservable<ClientTickEvent.Pre>()
    val CLIENT_TICK_POST = EventObservable<ClientTickEvent.Post>()

    val SERVER_PLAYER_LOGIN = EventObservable<ServerPlayerEvent.Login>()
    val SERVER_PLAYER_LOGOUT = EventObservable<ServerPlayerEvent.Logout>()
    val CLIENT_PLAYER_LOGIN = EventObservable<ClientPlayerEvent.Login>()
    val CLIENT_PLAYER_LOGOUT = EventObservable<ClientPlayerEvent.Logout>()
    val PLAYER_DEATH = CancelableObservable<ServerPlayerEvent.Death>()
    val RIGHT_CLICK_BLOCK = CancelableObservable<ServerPlayerEvent.RightClickBlock>()
    val RIGHT_CLICK_ENTITY = CancelableObservable<ServerPlayerEvent.RightClickEntity>()
}