package com.cablemc.pokemoncobbled.common.util

fun lang(
    subKey: String,
    vararg objects: Any
) = "pokemoncobbled.$subKey".asTranslated(*objects)

fun battleLang(key: String, vararg objects: Any) = lang(subKey = "battle.$key", objects = objects)