package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.quirk

import net.minecraft.entity.Entity

class SimpleQuirkData<T : Entity>(name: String) : QuirkData<T>(name) {
    var nextOccurrenceSeconds = -1F
}