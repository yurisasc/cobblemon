/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter

/**
 * TODO
 *
 * @author Licious
 * @since December 31st, 2022
 */
interface Effect {

    /**
     * TODO
     */
    val id: String

    /**
     * TODO
     */
    val type: Type

    /**
     * TODO
     */
    val rawData: String

    /**
     * TODO
     *
     * @property prefix
     *
     * @author Licious
     * @since December 31st, 2022
     */
    enum class Type(val prefix: String) {

        ABILITY("ability:"),
        ITEM("item:"),
        MOVE("move:"),
        PURE(""),

    }

    companion object {

        /**
         * TODO
         *
         * @param id
         * @param type
         * @param rawData
         * @return
         */
        private fun of(id: String, type: Type, rawData: String): Effect = CobblemonEffect(id, type, rawData)

        /**
         * TODO
         *
         * @param id
         * @param rawData
         * @return
         */
        fun ability(id: String, rawData: String): Effect = this.of(id, Type.ABILITY, rawData)

        /**
         * TODO
         *
         * @param id
         * @param rawData
         * @return
         */
        fun item(id: String, rawData: String): Effect = this.of(id, Type.ITEM, rawData)

        /**
         * TODO
         *
         * @param id
         * @param rawData
         * @return
         */
        fun move(id: String, rawData: String): Effect = this.of(id, Type.MOVE, rawData)

        /**
         * TODO
         *
         * @param id
         * @param rawData
         * @return
         */
        fun pure(id: String, rawData: String): Effect = this.of(id, Type.PURE, rawData)

        /**
         * TODO
         *
         * @param rawData
         * @return
         */
        fun parse(rawData: String): Effect? {
            if (rawData.isEmpty()) {
                return null
            }
            return when {
                rawData.startsWith(Type.ABILITY.prefix) -> this.ability(rawData.substringAfter(Type.ABILITY.prefix).replace(" ", ""), rawData)
                rawData.startsWith(Type.ITEM.prefix) -> this.item(rawData.substringAfter(Type.ITEM.prefix).replace(" ", ""), rawData)
                rawData.startsWith(Type.MOVE.prefix) -> this.move(rawData.substringAfter(Type.MOVE.prefix).replace(" ", ""), rawData)
                else -> this.pure(rawData.replace(" ", ""), rawData)
            }
        }

    }

}