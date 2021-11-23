package com.cablemc.pokemoncobbled.common.util

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.io.Reader

inline fun <reified T> Gson.fromJson(reader: Reader) = fromJson(reader, T::class.java)
inline fun <reified T> Gson.fromJson(element: JsonElement) = fromJson(element, T::class.java)
inline fun <reified T> Gson.fromJson(string: String) = fromJson(string, T::class.java)

