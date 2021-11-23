package com.cablemc.pokemoncobbled.client.render.pokemon

import com.cablemc.pokemoncobbled.client.render.blockbench.EeveeModel
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class EeveeRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<PokemonEntity, EeveeModel>(context, EeveeModel(context.bakeLayer(EeveeModel.LAYER_LOCATION)), 0.5f) {

    override fun getTextureLocation(pEntity: PokemonEntity) = ResourceLocation(PokemonCobbled.MODID, "textures/pokemon/eevee-base.png")

}