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
    @JvmStatic
    val SERVER_STARTING = EventObservable<ServerEvent.Starting>()
    @JvmStatic
    val SERVER_STARTED = EventObservable<ServerEvent.Started>()
    @JvmStatic
    val SERVER_STOPPING = EventObservable<ServerEvent.Stopping>()
    @JvmStatic
    val SERVER_STOPPED = EventObservable<ServerEvent.Stopped>()

    @JvmStatic
    val SERVER_TICK_PRE = EventObservable<ServerTickEvent.Pre>()
    @JvmStatic
    val SERVER_TICK_POST = EventObservable<ServerTickEvent.Post>()

    @JvmStatic
    val CLIENT_TICK_PRE = EventObservable<ClientTickEvent.Pre>()
    @JvmStatic
    val CLIENT_TICK_POST = EventObservable<ClientTickEvent.Post>()

    @JvmStatic
    val SERVER_PLAYER_LOGIN = EventObservable<ServerPlayerEvent.Login>()
    @JvmStatic
    val SERVER_PLAYER_LOGOUT = EventObservable<ServerPlayerEvent.Logout>()
    @JvmStatic
    val CLIENT_PLAYER_LOGIN = EventObservable<ClientPlayerEvent.Login>()
    @JvmStatic
    val CLIENT_PLAYER_LOGOUT = EventObservable<ClientPlayerEvent.Logout>()
    @JvmStatic
    val PLAYER_DEATH = CancelableObservable<ServerPlayerEvent.Death>()
    @JvmStatic
    val RIGHT_CLICK_BLOCK = CancelableObservable<ServerPlayerEvent.RightClickBlock>()
    @JvmStatic
    val RIGHT_CLICK_ENTITY = CancelableObservable<ServerPlayerEvent.RightClickEntity>()

    @JvmStatic
    val CHANGE_DIMENSION = EventObservable<ChangeDimensionEvent>()
}