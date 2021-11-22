//package com.cablemc.kotlinmon.client.render
//
//fun getSpriteLocation(species: PokemonSpecies, form: PokemonForm, texture: String? = null): ResourceLocation {
//    val texturePath = texture ?: form.name
//    val path = "pokemon/${species.name}/forms/${form.name}/sprites/$texturePath.png".toLowerCase()
//    return ResourceLocation(Kotlinmon.MOD_ID, path)
//}
//
//fun getModelTextureLocation(species: PokemonSpecies, form: PokemonForm, texture: String? = null): ResourceLocation {
//    val texturePath = texture ?: form.name
//    val path = "pokemon/${species.name}/forms/${form.name}/model/textures/$texturePath.png".toLowerCase()
//    return ResourceLocation(Kotlinmon.MOD_ID, path)
//}
//
//fun getModelLocation(species: PokemonSpecies, form: PokemonForm): ResourceLocation {
//    val path = "pokemon/${species.name}/forms/${form.name}/model/${form.name}.pqc".toLowerCase()
//    return ResourceLocation(Kotlinmon.MOD_ID, path)
//}