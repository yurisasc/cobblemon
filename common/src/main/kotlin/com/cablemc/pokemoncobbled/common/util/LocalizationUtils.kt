package com.cablemc.pokemoncobbled.common.util

fun lang(
    subKey: String,
    vararg objects: Any
) = "pokemoncobbled.$subKey".asTranslated(*objects)

fun commandLang(subKey: String, vararg objects: Any ) = lang("command.$subKey", *objects)
fun battleLang(key: String, vararg objects: Any) = lang("battle.$key", *objects)