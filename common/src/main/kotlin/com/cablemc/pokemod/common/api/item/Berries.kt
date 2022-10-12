/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.item

import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.util.Identifier

/**
 * Registry for all Berry types
 * Get or register berry types
 *
 * @author Deltric
 * @since January 13th, 2022
 */
object Berries {
    private val allBerries = mutableListOf<Berry>()

    val CHERI = registerBerry(Berry(pokemodResource("cheri"), 10, 0, 0, 0, 0))
    val CHESTO = registerBerry(Berry(pokemodResource("chesto"), 0, 10, 0, 0, 0))
    val PECHA = registerBerry(Berry(pokemodResource("pecha"), 0, 0, 10, 0, 0))
    val RAWST = registerBerry(Berry(pokemodResource("rawst"), 0, 0, 0, 10, 0))
    val ASPEAR = registerBerry(Berry(pokemodResource("aspear"), 0, 0, 0, 0, 10))
    val LEPPA = registerBerry(Berry(pokemodResource("leppa"), 10, 0, 10, 10, 10))
    val ORAN = registerBerry(Berry(pokemodResource("oran"), 10, 10, 0, 10, 10))
    val PERSIM = registerBerry(Berry(pokemodResource("persim"), 10, 10, 10, 0, 10))
    val LUM = registerBerry(Berry(pokemodResource("lum"), 10, 10, 10, 10, 0))
    val SITRUS = registerBerry(Berry(pokemodResource("sitrus"), 0, 10, 10, 10, 10))
    val FIGY = registerBerry(Berry(pokemodResource("figy"), 15, 0, 0, 0, 0))
    val WIKI = registerBerry(Berry(pokemodResource("wiki"), 0, 15, 0, 0, 0))
    val MAGO = registerBerry(Berry(pokemodResource("mago"), 0, 0, 15, 0, 0))
    val AGUAV = registerBerry(Berry(pokemodResource("aguav"), 0, 0, 0, 15, 0))
    val IAPAPA = registerBerry(Berry(pokemodResource("iapapa"), 0, 0, 0, 0, 15))
    val RAZZ = registerBerry(Berry(pokemodResource("razz"), 10, 10, 0, 0, 0))
    val BLUK = registerBerry(Berry(pokemodResource("bluk"), 0, 10, 10, 0, 0))
    val NANAB = registerBerry(Berry(pokemodResource("nanab"), 0, 0, 10, 10, 0))
    val WEPEAR = registerBerry(Berry(pokemodResource("wepear"), 0, 0, 0, 10, 10))
    val PINAP = registerBerry(Berry(pokemodResource("pinap"), 10, 0, 0, 0, 10))
    val POMEG = registerBerry(Berry(pokemodResource("pomeg"), 10, 0, 10, 10, 0))
    val KELPSY = registerBerry(Berry(pokemodResource("kelpsy"), 0, 10, 0, 10, 10))
    val QUALOT = registerBerry(Berry(pokemodResource("qualot"), 10, 0, 10, 0, 10))
    val HONDEW = registerBerry(Berry(pokemodResource("hondew"), 10, 10, 0, 10, 0))
    val GREPA = registerBerry(Berry(pokemodResource("grepa"), 0, 10, 10, 0, 10))
    val TAMATO = registerBerry(Berry(pokemodResource("tamato"), 20, 10, 0, 0, 0))
    val CORNN = registerBerry(Berry(pokemodResource("cornn"), 0, 20, 10, 0, 0))
    val MAGOST = registerBerry(Berry(pokemodResource("magost"), 0, 0, 20, 10, 0))
    val RABUTA = registerBerry(Berry(pokemodResource("rabuta"), 0, 0, 0, 20, 10))
    val NOMEL = registerBerry(Berry(pokemodResource("nomel"), 10, 0, 0, 0, 20))
    val SPELON = registerBerry(Berry(pokemodResource("spelon"), 30, 10, 0, 0, 0))
    val PAMTRE = registerBerry(Berry(pokemodResource("pamtre"), 0, 30, 10, 0, 0))
    val WATMEL = registerBerry(Berry(pokemodResource("watmel"), 0, 0, 30, 10, 0))
    val DURIN = registerBerry(Berry(pokemodResource("durin"), 0, 0, 0, 30, 10))
    val BELUE = registerBerry(Berry(pokemodResource("belue"), 10, 0, 0, 0, 30))
    val OCCA = registerBerry(Berry(pokemodResource("occa"), 15, 0, 10, 0, 0))
    val PASSHO = registerBerry(Berry(pokemodResource("passho"), 0, 15, 0, 10, 0))
    val WACAN = registerBerry(Berry(pokemodResource("wacan"), 0, 0, 15, 0, 10))
    val RINDO = registerBerry(Berry(pokemodResource("rindo"), 10, 0, 0, 15, 0))
    val YACHE = registerBerry(Berry(pokemodResource("yache"), 0, 10, 0, 0, 15))
    val CHOPLE = registerBerry(Berry(pokemodResource("chople"), 15, 0, 0, 10, 0))
    val KEBIA = registerBerry(Berry(pokemodResource("kebia"), 0, 15, 0, 0, 10))
    val SHUCA = registerBerry(Berry(pokemodResource("shuca"), 10, 0, 15, 0, 0))
    val COBA = registerBerry(Berry(pokemodResource("coba"), 0, 10, 0, 15, 0))
    val PAYAPA = registerBerry(Berry(pokemodResource("payapa"), 0, 0, 10, 0, 15))
    val TANGA = registerBerry(Berry(pokemodResource("tanga"), 20, 0, 0, 0, 10))
    val CHARTI = registerBerry(Berry(pokemodResource("charti"), 10, 20, 0, 0, 0))
    val KASIB = registerBerry(Berry(pokemodResource("kasib"), 0, 10, 20, 0, 0))
    val HABAN = registerBerry(Berry(pokemodResource("haban"), 0, 0, 10, 20, 0))
    val COLBUR = registerBerry(Berry(pokemodResource("colbur"), 0, 0, 0, 10, 20))
    val BABIRI = registerBerry(Berry(pokemodResource("babiri"), 25, 10, 0, 0, 0))
    val CHILAN = registerBerry(Berry(pokemodResource("chilan"), 0, 25, 10, 0, 0))
    val LIECHI = registerBerry(Berry(pokemodResource("liechi"), 30, 10, 30, 0, 0))
    val GANLON = registerBerry(Berry(pokemodResource("ganlon"), 0, 30, 10, 30, 0))
    val SALAC = registerBerry(Berry(pokemodResource("salac"), 0, 0, 30, 10, 30))
    val PETAYA = registerBerry(Berry(pokemodResource("petaya"), 30, 0, 0, 30, 10))
    val APICOT = registerBerry(Berry(pokemodResource("apicot"), 10, 30, 0, 0, 30))
    val LANSAT = registerBerry(Berry(pokemodResource("lansat"), 30, 10, 30, 10, 30))
    val STARF = registerBerry(Berry(pokemodResource("starf"), 30, 10, 30, 10, 30))
    val ENIGMA = registerBerry(Berry(pokemodResource("enigma"), 40, 10, 0, 0, 0))
    val MICLE = registerBerry(Berry(pokemodResource("micle"), 0, 40, 10, 0, 0))
    val CUSTAP = registerBerry(Berry(pokemodResource("custap"), 0, 0, 40, 10, 0))
    val JABOCA = registerBerry(Berry(pokemodResource("jaboca"), 0, 0, 0, 40, 10))
    val ROWAP = registerBerry(Berry(pokemodResource("rowap"), 10, 0, 0, 0, 40))
    val ROSELI = registerBerry(Berry(pokemodResource("roseli"), 0, 0, 25, 10, 0))
    val KEE = registerBerry(Berry(pokemodResource("kee"), 30, 30, 10, 10, 10))
    val MARANGA = registerBerry(Berry(pokemodResource("maranga"), 10, 10, 30, 30, 10))

    /**
     * Registers a new berry type
     */
    fun registerBerry(berry: Berry): Berry {
        allBerries.add(berry)
        return berry
    }

    /**
     * Gets a berry by registry name
     * @return a berry type or null
     */
    fun getBerry(name: Identifier): Berry? {
        return allBerries.find { berry -> berry.name == name }
    }
}