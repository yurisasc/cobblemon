/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.battles.runner

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset

class StandardShowdownConnection(host: InetAddress, port: Int): ShowdownConnection {

    private lateinit var socket: Socket
    private lateinit var writer: OutputStreamWriter
    private lateinit var reader: BufferedReader
    private var data = ""
    private var closed: Boolean = false

    override fun open() {
        socket = Socket(InetAddress.getLocalHost(), 25567, InetAddress.getLocalHost(), 25566)
        socket.keepAlive = true
        writer = socket.getOutputStream().writer(charset = Charset.forName("ascii"))
        reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    override fun close() {
        socket.close()
        closed = true
    }

    override fun write(input: String) {
        writer.write(input + ShowdownConnection.LINE_END)
        writer.flush()
    }

    override fun read(messageHandler: (String) -> Unit) {
        try {
            while (reader.ready()) {
                val char = reader.read()
                if (char > -1) {
                    data += char.toChar()
                    if (data.endsWith(ShowdownConnection.LINE_END)) {
                        messageHandler(data.replace(ShowdownConnection.LINE_END, ""))
                        data = ""
                    }
                }
            }
        } catch (exception: IOException) {
            exception.printStackTrace()
        }
    }

    override fun isClosed(): Boolean {
        return closed
    }

    override fun isConnected(): Boolean {
        return socket.isConnected
    }
}