/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.battle

import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.client.sound.battle.BattleMusicController
import com.cobblemon.mod.common.client.sound.battle.BattleMusicInstance
import com.cobblemon.mod.common.net.messages.client.battle.BattleMusicPacket
import net.minecraft.client.MinecraftClient

/**
 * The handler for [BattleMusicPacket]s. Interfaces with [BattleMusicController] to change battle music.
 *
 * @author Segfault Guy
 * @since April 22nd, 2023
 */
object BattleMusicHandler : ClientNetworkPacketHandler<BattleMusicPacket> {

    override fun handle(packet: BattleMusicPacket, client: MinecraftClient) {
        val soundManager = client.soundManager
        val newMusic = packet.music?.let { BattleMusicInstance(it, packet.volume, packet.pitch) }
        val currMusic = BattleMusicController.music

        if (newMusic == null)
            BattleMusicController.endMusic()
        else if (!soundManager.isPlaying(currMusic))
            BattleMusicController.initializeMusic(newMusic)
        else
            BattleMusicController.switchMusic(newMusic)
    }

}
