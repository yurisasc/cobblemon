package com.cablemc.pokemoncobbled.common.api.item

import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.resources.ResourceLocation

/**
 * Registry for all Berry types
 * Get or register berry types
 *
 * @author Deltric
 * @since January 13th, 2022
 */
object Berries {
    private val allBerries = mutableListOf<Berry>()

    val CHERI = registerBerry(Berry(cobbledResource("cheri"), 10, 0, 0, 0, 0))
    val CHESTO = registerBerry(Berry(cobbledResource("chesto"), 0, 10, 0, 0, 0))
    val PECHA = registerBerry(Berry(cobbledResource("pecha"), 0, 0, 10, 0, 0))
    val RAWST = registerBerry(Berry(cobbledResource("rawst"), 0, 0, 0, 10, 0))
    val ASPEAR = registerBerry(Berry(cobbledResource("aspear"), 0, 0, 0, 0, 10))
    val LEPPA = registerBerry(Berry(cobbledResource("leppa"), 10, 0, 10, 10, 10))
    val ORAN = registerBerry(Berry(cobbledResource("oran"), 10, 10, 0, 10, 10))
    val PERSIM = registerBerry(Berry(cobbledResource("persim"), 10, 10, 10, 0, 10))
    val LUM = registerBerry(Berry(cobbledResource("lum"), 10, 10, 10, 10, 0))
    val SITRUS = registerBerry(Berry(cobbledResource("sitrus"), 0, 10, 10, 10, 10))
    val FIGY = registerBerry(Berry(cobbledResource("figy"), 15, 0, 0, 0, 0))
    val WIKI = registerBerry(Berry(cobbledResource("wiki"), 0, 15, 0, 0, 0))
    val MAGO = registerBerry(Berry(cobbledResource("mago"), 0, 0, 15, 0, 0))
    val AGUAV = registerBerry(Berry(cobbledResource("aguav"), 0, 0, 0, 15, 0))
    val IAPAPA = registerBerry(Berry(cobbledResource("iapapa"), 0, 0, 0, 0, 15))
    val RAZZ = registerBerry(Berry(cobbledResource("razz"), 10, 10, 0, 0, 0))
    val BLUK = registerBerry(Berry(cobbledResource("bluk"), 0, 10, 10, 0, 0))
    val NANAB = registerBerry(Berry(cobbledResource("nanab"), 0, 0, 10, 10, 0))
    val WEPEAR = registerBerry(Berry(cobbledResource("wepear"), 0, 0, 0, 10, 10))
    val PINAP = registerBerry(Berry(cobbledResource("pinap"), 10, 0, 0, 0, 10))
    val POMEG = registerBerry(Berry(cobbledResource("pomeg"), 10, 0, 10, 10, 0))
    val KELPSY = registerBerry(Berry(cobbledResource("kelpsy"), 0, 10, 0, 10, 10))
    val QUALOT = registerBerry(Berry(cobbledResource("qualot"), 10, 0, 10, 0, 10))
    val HONDEW = registerBerry(Berry(cobbledResource("hondew"), 10, 10, 0, 10, 0))
    val GREPA = registerBerry(Berry(cobbledResource("grepa"), 0, 10, 10, 0, 10))
    val TAMATO = registerBerry(Berry(cobbledResource("tamato"), 20, 10, 0, 0, 0))
    val CORNN = registerBerry(Berry(cobbledResource("cornn"), 0, 20, 10, 0, 0))
    val MAGOST = registerBerry(Berry(cobbledResource("magost"), 0, 0, 20, 10, 0))
    val RABUTA = registerBerry(Berry(cobbledResource("rabuta"), 0, 0, 0, 20, 10))
    val NOMEL = registerBerry(Berry(cobbledResource("nomel"), 10, 0, 0, 0, 20))
    val SPELON = registerBerry(Berry(cobbledResource("spelon"), 30, 10, 0, 0, 0))
    val PAMTRE = registerBerry(Berry(cobbledResource("pamtre"), 0, 30, 10, 0, 0))
    val WATMEL = registerBerry(Berry(cobbledResource("watmel"), 0, 0, 30, 10, 0))
    val DURIN = registerBerry(Berry(cobbledResource("durin"), 0, 0, 0, 30, 10))
    val BELUE = registerBerry(Berry(cobbledResource("belue"), 10, 0, 0, 0, 30))
    val OCCA = registerBerry(Berry(cobbledResource("occa"), 15, 0, 10, 0, 0))
    val PASSHO = registerBerry(Berry(cobbledResource("passho"), 0, 15, 0, 10, 0))
    val WACAN = registerBerry(Berry(cobbledResource("wacan"), 0, 0, 15, 0, 10))
    val RINDO = registerBerry(Berry(cobbledResource("rindo"), 10, 0, 0, 15, 0))
    val YACHE = registerBerry(Berry(cobbledResource("yache"), 0, 10, 0, 0, 15))
    val CHOPLE = registerBerry(Berry(cobbledResource("chople"), 15, 0, 0, 10, 0))
    val KEBIA = registerBerry(Berry(cobbledResource("kebia"), 0, 15, 0, 0, 10))
    val SHUCA = registerBerry(Berry(cobbledResource("shuca"), 10, 0, 15, 0, 0))
    val COBA = registerBerry(Berry(cobbledResource("coba"), 0, 10, 0, 15, 0))
    val PAYAPA = registerBerry(Berry(cobbledResource("payapa"), 0, 0, 10, 0, 15))
    val TANGA = registerBerry(Berry(cobbledResource("tanga"), 20, 0, 0, 0, 10))
    val CHARTI = registerBerry(Berry(cobbledResource("charti"), 10, 20, 0, 0, 0))
    val KASIB = registerBerry(Berry(cobbledResource("kasib"), 0, 10, 20, 0, 0))
    val HABAN = registerBerry(Berry(cobbledResource("haban"), 0, 0, 10, 20, 0))
    val COLBUR = registerBerry(Berry(cobbledResource("colbur"), 0, 0, 0, 10, 20))
    val BABIRI = registerBerry(Berry(cobbledResource("babiri"), 25, 10, 0, 0, 0))
    val CHILAN = registerBerry(Berry(cobbledResource("chilan"), 0, 25, 10, 0, 0))
    val LIECHI = registerBerry(Berry(cobbledResource("liechi"), 30, 10, 30, 0, 0))
    val GANLON = registerBerry(Berry(cobbledResource("ganlon"), 0, 30, 10, 30, 0))
    val SALAC = registerBerry(Berry(cobbledResource("salac"), 0, 0, 30, 10, 30))
    val PETAYA = registerBerry(Berry(cobbledResource("petaya"), 30, 0, 0, 30, 10))
    val APICOT = registerBerry(Berry(cobbledResource("apicot"), 10, 30, 0, 0, 30))
    val LANSAT = registerBerry(Berry(cobbledResource("lansat"), 30, 10, 30, 10, 30))
    val STARF = registerBerry(Berry(cobbledResource("starf"), 30, 10, 30, 10, 30))
    val ENIGMA = registerBerry(Berry(cobbledResource("enigma"), 40, 10, 0, 0, 0))
    val MICLE = registerBerry(Berry(cobbledResource("micle"), 0, 40, 10, 0, 0))
    val CUSTAP = registerBerry(Berry(cobbledResource("custap"), 0, 0, 40, 10, 0))
    val JABOCA = registerBerry(Berry(cobbledResource("jaboca"), 0, 0, 0, 40, 10))
    val ROWAP = registerBerry(Berry(cobbledResource("rowap"), 10, 0, 0, 0, 40))
    val ROSELI = registerBerry(Berry(cobbledResource("roseli"), 0, 0, 25, 10, 0))
    val KEE = registerBerry(Berry(cobbledResource("kee"), 30, 30, 10, 10, 10))
    val MARANGA = registerBerry(Berry(cobbledResource("maranga"), 10, 10, 30, 30, 10))

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
    fun getBerry(name: ResourceLocation): Berry? {
        return allBerries.find { berry -> berry.name == name }
    }
}