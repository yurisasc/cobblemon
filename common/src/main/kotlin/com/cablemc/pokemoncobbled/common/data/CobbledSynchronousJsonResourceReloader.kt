package com.cablemc.pokemoncobbled.common.data

import com.cablemc.pokemoncobbled.common.api.data.SynchronousJsonResourceReloader
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.util.Identifier
import java.nio.file.Path

internal class CobbledSynchronousJsonResourceReloader<T>(
    override val gson: Gson,
    override val resourcePath: Path,
    override val typeToken: TypeToken<T>,
    private val dataConsumer: (data: Map<Identifier, T>) -> Unit
) : SynchronousJsonResourceReloader<T> {

    override fun processData(data: Map<Identifier, T>) = this.dataConsumer.invoke(data)

}