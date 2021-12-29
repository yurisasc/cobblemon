package com.cablemc.pokemoncobbled.client.render.models.blockbench.repository

import com.cablemc.pokemoncobbled.client.render.models.blockbench.BlockBenchModelWrapper
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball.PokeBallModel
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import net.minecraft.resources.ResourceLocation

object PokeBallModelRepository : ModelRepository<PokeBallEntity>() {

    private val modelsByPokeBall: MutableMap<PokeBall, BlockBenchModelWrapper<PokeBallEntity>> = mutableMapOf()
    private val modelTexturesByPokeBall: MutableMap<PokeBall, ResourceLocation> = mutableMapOf()

    override fun registerAll() {
        registerModel(PokeBalls.POKE_BALL, BlockBenchModelWrapper(PokeBallModel.LAYER_LOCATION, PokeBallModel::createBodyLayer) { PokeBallModel(it) })
        registerModelTexture(PokeBalls.POKE_BALL, ResourceLocation(PokemonCobbled.MODID, "textures/pokemon/pokeball-base.png"))
    }

    private fun registerModel(pokeBall: PokeBall, model: BlockBenchModelWrapper<PokeBallEntity>) {
        modelsByPokeBall[pokeBall] = model
        addModel(model)
    }

    private fun registerModelTexture(pokeBall: PokeBall, texture: ResourceLocation) {
        modelTexturesByPokeBall[pokeBall] = texture
    }

    fun getModel(pokeBall: PokeBall): BlockBenchModelWrapper<PokeBallEntity> {
        return modelsByPokeBall[pokeBall] ?: throw IllegalStateException("pokeball has no appropriate model")
    }

    fun getModelTexture(pokeBall: PokeBall): ResourceLocation {
        return modelTexturesByPokeBall[pokeBall] ?: throw IllegalStateException("pokeball has no appropriate model texture")
    }

}