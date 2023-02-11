/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.repository

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.TexturedModel
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.JsonPokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen6.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen7.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen8.*
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9.*
import com.cobblemon.mod.common.client.render.pokemon.ModelLayer
import com.cobblemon.mod.common.client.render.pokemon.RegisteredSpeciesRendering
import com.cobblemon.mod.common.client.render.pokemon.SpeciesVariationSet
import com.cobblemon.mod.common.client.util.exists
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Species
import com.cobblemon.mod.common.pokemon.aspects.SHINY_ASPECT
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.endsWith
import com.cobblemon.mod.common.util.fromJson
import java.io.File
import java.nio.charset.StandardCharsets
import net.minecraft.client.model.ModelPart
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

object PokemonModelRepository : ModelRepository<PokemonEntity>() {
    val posers = mutableMapOf<Identifier, (ModelPart) -> PokemonPoseableModel>()
    val renders = mutableMapOf<Identifier, RegisteredSpeciesRendering>()
    val texturedModels = mutableMapOf<Identifier, TexturedModel>()

    fun registerPosers(resourceManager: ResourceManager) {
        posers.clear()
        registerInBuiltPosers()
        registerJsonPosers(resourceManager)
    }

    fun registerInBuiltPosers() {
        inbuilt("bulbasaur", ::BulbasaurModel)
        inbuilt("ivysaur", ::IvysaurModel)
        inbuilt("venusaur", ::VenusaurModel)
        inbuilt("charmander", ::CharmanderModel)
        inbuilt("charmeleon", ::CharmeleonModel)
        inbuilt("charizard", ::CharizardModel)
        inbuilt("squirtle", ::SquirtleModel)
        inbuilt("wartortle", ::WartortleModel)
        inbuilt("blastoise", ::BlastoiseModel)
        inbuilt("caterpie", ::CaterpieModel)
        inbuilt("metapod", ::MetapodModel)
        inbuilt("butterfree", ::ButterfreeModel)
        inbuilt("weedle", ::WeedleModel)
        inbuilt("kakuna", ::KakunaModel)
        inbuilt("beedrill", ::BeedrillModel)
        inbuilt("rattata", ::RattataModel)
        inbuilt("raticate", ::RaticateModel)
        inbuilt("eevee", ::EeveeModel)
        inbuilt("magikarp", ::MagikarpModel)
        inbuilt("gyarados", ::GyaradosModel)
        inbuilt("pidgey", ::PidgeyModel)
        inbuilt("pidgeotto", ::PidgeottoModel)
        inbuilt("pidgeot", ::PidgeotModel)
        inbuilt("diglett", ::DiglettModel)
        inbuilt("dugtrio", ::DugtrioModel)
        inbuilt("zubat", ::ZubatModel)
        inbuilt("cleffa", ::CleffaModel)
        inbuilt("clefable", ::ClefableModel)
        inbuilt("clefairy", ::ClefairyModel)
        inbuilt("krabby", ::KrabbyModel)
        inbuilt("paras", ::ParasModel)
        inbuilt("parasect", ::ParasectModel)
        inbuilt("mankey", ::MankeyModel)
        inbuilt("primeape", ::PrimeapeModel)
        inbuilt("oddish", ::OddishModel)
        inbuilt("gloom", ::GloomModel)
        inbuilt("vileplume", ::VileplumeModel)
        inbuilt("bellossom", ::BellossomModel)
        inbuilt("voltorb", ::VoltorbModel)
        inbuilt("electrode", ::ElectrodeModel)
        inbuilt("lapras", ::LaprasModel)
        inbuilt("ekans", ::EkansModel)
        inbuilt("machop", ::MachopModel)
        inbuilt("machoke", ::MachokeModel)
        inbuilt("machamp", ::MachampModel)
        inbuilt("abra", ::AbraModel)
        inbuilt("aerodactyl", ::AerodactylModel)
        inbuilt("alakazam", ::AlakazamModel)
        inbuilt("arbok", ::ArbokModel)
        inbuilt("arcanine", ::ArcanineModel)
        inbuilt("articuno", ::ArticunoModel)
        inbuilt("bellsprout", ::BellsproutModel)
        inbuilt("chansey", ::ChanseyModel)
        inbuilt("cloyster", ::CloysterModel)
        inbuilt("crobat", ::CrobatModel)
        inbuilt("cubone", ::CuboneModel)
        inbuilt("dewgong", ::DewgongModel)
        inbuilt("ditto", ::DittoModel)
        inbuilt("dodrio", ::DodrioModel)
        inbuilt("doduo", ::DoduoModel)
        inbuilt("dragonair", ::DragonairModel)
        inbuilt("dragonite", ::DragoniteModel)
        inbuilt("dratini", ::DratiniModel)
        inbuilt("drowzee", ::DrowzeeModel)
        inbuilt("electabuzz", ::ElectabuzzModel)
        inbuilt("exeggcute", ::ExeggcuteModel)
        inbuilt("exeggutor", ::ExeggutorModel)
        inbuilt("farfetchd", ::FarfetchdModel)
        inbuilt("fearow", ::FearowModel)
        inbuilt("flareon", ::FlareonModel)
        inbuilt("gastly", ::GastlyModel)
        inbuilt("gengar", ::GengarModel)
        inbuilt("geodude", ::GeodudeModel)
        inbuilt("golbat", ::GolbatModel)
        inbuilt("goldeen", ::GoldeenModel)
        inbuilt("golduck", ::GolduckModel)
        inbuilt("golem", ::GolemModel)
        inbuilt("graveler", ::GravelerModel)
        inbuilt("grimer", ::GrimerModel)
        inbuilt("growlithe", ::GrowlitheModel)
        inbuilt("haunter", ::HaunterModel)
        inbuilt("hitmonchan", ::HitmonchanModel)
        inbuilt("hitmonlee", ::HitmonleeModel)
        inbuilt("horsea", ::HorseaModel)
        inbuilt("hypno", ::HypnoModel)
        inbuilt("jigglypuff", ::JigglypuffModel)
        inbuilt("jolteon", ::JolteonModel)
        inbuilt("jynx", ::JynxModel)
        inbuilt("kabuto", ::KabutoModel)
        inbuilt("kabutops", ::KabutopsModel)
        inbuilt("kadabra", ::KadabraModel)
        inbuilt("kangaskhan", ::KangaskhanModel)
        inbuilt("kingler", ::KinglerModel)
        inbuilt("koffing", ::KoffingModel)
        inbuilt("krabby", ::KrabbyModel)
        inbuilt("lickitung", ::LickitungModel)
        inbuilt("magmar", ::MagmarModel)
        inbuilt("magnemite", ::MagnemiteModel)
        inbuilt("magneton", ::MagnetonModel)
        inbuilt("marowak", ::MarowakModel)
        inbuilt("meowth", ::MeowthModel)
        inbuilt("mew", ::MewModel)
        inbuilt("mewtwo", ::MewtwoModel)
        inbuilt("moltres", ::MoltresModel)
        inbuilt("mrmime", ::MrmimeModel)
        inbuilt("muk", ::MukModel)
        inbuilt("nidoking", ::NidokingModel)
        inbuilt("nidoqueen", ::NidoqueenModel)
        inbuilt("nidoranf", ::NidoranfModel)
        inbuilt("nidoranm", ::NidoranmModel)
        inbuilt("nidorina", ::NidorinaModel)
        inbuilt("nidorino", ::NidorinoModel)
        inbuilt("ninetales", ::NinetalesModel)
        inbuilt("omanyte", ::OmanyteModel)
        inbuilt("omastar", ::OmastarModel)
        inbuilt("onix", ::OnixModel)
        inbuilt("persian", ::PersianModel)
        inbuilt("pikachu", ::PikachuModel)
        inbuilt("pinsir", ::PinsirModel)
        inbuilt("poliwag", ::PoliwagModel)
        inbuilt("poliwhirl", ::PoliwhirlModel)
        inbuilt("poliwrath", ::PoliwrathModel)
        inbuilt("politoed", ::PolitoedModel)
        inbuilt("ponyta", ::PonytaModel)
        inbuilt("porygon", ::PorygonModel)
        inbuilt("psyduck", ::PsyduckModel)
        inbuilt("raichu", ::RaichuModel)
        inbuilt("rapidash", ::RapidashModel)
        inbuilt("rhydon", ::RhydonModel)
        inbuilt("rhyhorn", ::RhyhornModel)
        inbuilt("sandshrew", ::SandshrewModel)
        inbuilt("sandslash", ::SandslashModel)
        inbuilt("scyther", ::ScytherModel)
        inbuilt("seadra", ::SeadraModel)
        inbuilt("seaking", ::SeakingModel)
        inbuilt("seel", ::SeelModel)
        inbuilt("shellder", ::ShellderModel)
        inbuilt("slowbro", ::SlowbroModel)
        inbuilt("slowpoke", ::SlowpokeModel)
        inbuilt("snorlax", ::SnorlaxModel)
        inbuilt("spearow", ::SpearowModel)
        inbuilt("starmie", ::StarmieModel)
        inbuilt("staryu", ::StaryuModel)
        inbuilt("steelix", ::SteelixModel)
        inbuilt("tangela", ::TangelaModel)
        inbuilt("tauros", ::TaurosModel)
        inbuilt("tentacool", ::TentacoolModel)
        inbuilt("tentacruel", ::TentacruelModel)
        inbuilt("vaporeon", ::VaporeonModel)
        inbuilt("venomoth", ::VenomothModel)
        inbuilt("venonat", ::VenonatModel)
        inbuilt("victreebel", ::VictreebelModel)
        inbuilt("vulpix", ::VulpixModel)
        inbuilt("weepinbell", ::WeepinbellModel)
        inbuilt("weezing", ::WeezingModel)
        inbuilt("wigglytuff", ::WigglytuffModel)
        inbuilt("zapdos", ::ZapdosModel)
        inbuilt("elekid", ::ElekidModel)
        inbuilt("igglybuff", ::IgglybuffModel)
        inbuilt("magby", ::MagbyModel)
        inbuilt("pichu", ::PichuModel)
        inbuilt("smoochum", ::SmoochumModel)
        inbuilt("tyrogue", ::TyrogueModel)
        inbuilt("hitmontop", ::HitmontopModel)
        inbuilt("electivire", ::ElectivireModel)
        inbuilt("glaceon", ::GlaceonModel)
        inbuilt("happiny", ::HappinyModel)
        inbuilt("leafeon", ::LeafeonModel)
        inbuilt("lickilicky", ::LickilickyModel)
        inbuilt("magmortar", ::MagmortarModel)
        inbuilt("magnezone", ::MagnezoneModel)
        inbuilt("mimejr", ::MimejrModel)
        inbuilt("munchlax", ::MunchlaxModel)
        inbuilt("porygon2", ::Porygon2Model)
        inbuilt("porygonz", ::PorygonzModel)
        inbuilt("rhyperior", ::RhyperiorModel)
        inbuilt("scizor", ::ScizorModel)
        inbuilt("tangrowth", ::TangrowthModel)
        inbuilt("sylveon", ::SylveonModel)
        inbuilt("umbreon", ::UmbreonModel)
        inbuilt("espeon", ::EspeonModel)
        inbuilt("blissey", ::BlisseyModel)
        inbuilt("kingdra", ::KingdraModel)
        inbuilt("piloswine", ::PiloswineModel)
        inbuilt("quagsire", ::QuagsireModel)
        inbuilt("slowking", ::SlowkingModel)
        inbuilt("swinub", ::SwinubModel)
        inbuilt("wooper", ::WooperModel)
        inbuilt("yanma", ::YanmaModel)
        inbuilt("blaziken", ::BlazikenModel)
        inbuilt("combusken", ::CombuskenModel)
        inbuilt("marshtomp", ::MarshtompModel)
        inbuilt("minun", ::MinunModel)
        inbuilt("mudkip", ::MudkipModel)
        inbuilt("plusle", ::PlusleModel)
        inbuilt("rayquaza", ::RayquazaModel)
        inbuilt("swampert", ::SwampertModel)
        inbuilt("torchic", ::TorchicModel)
        inbuilt("bibarel", ::BibarelModel)
        inbuilt("bidoof", ::BidoofModel)
        inbuilt("buneary", ::BunearyModel)
        inbuilt("empoleon", ::EmpoleonModel)
        inbuilt("lopunny", ::LopunnyModel)
        inbuilt("mamoswine", ::MamoswineModel)
        inbuilt("pachirisu", ::PachirisuModel)
        inbuilt("piplup", ::PiplupModel)
        inbuilt("prinplup", ::PrinplupModel)
        inbuilt("yanmega", ::YanmegaModel)
        inbuilt("basculin", ::BasculinModel)
        inbuilt("crustle", ::CrustleModel)
        inbuilt("dwebble", ::DwebbleModel)
        inbuilt("emolga", ::EmolgaModel)
        inbuilt("maractus", ::MaractusModel)
        inbuilt("bounsweet", ::BounsweetModel)
        inbuilt("dartrix", ::DartrixModel)
        inbuilt("decidueye", ::DecidueyeModel)
        inbuilt("incineroar", ::IncineroarModel)
        inbuilt("litten", ::LittenModel)
        inbuilt("mimikyu", ::MimikyuModel)
        inbuilt("naganadel", ::NaganadelModel)
        inbuilt("poipole", ::PoipoleModel)
        inbuilt("rowlet", ::RowletModel)
        inbuilt("steenee", ::SteeneeModel)
        inbuilt("torracat", ::TorracatModel)
        inbuilt("tsareena", ::TsareenaModel)
        inbuilt("centiskorch", ::CentiskorchModel)
        inbuilt("sizzlipede", ::SizzlipedeModel)
        inbuilt("kleavor", ::KleavorModel)
        inbuilt("pyukumuku", ::PyukumukuModel)
        inbuilt("deerling", ::DeerlingModel)
        inbuilt("sawsbuck", ::SawsbuckModel)
        inbuilt("sableye", ::SableyeModel)
        inbuilt("natu", ::NatuModel)
        inbuilt("xatu", ::XatuModel)
        inbuilt("wailmer", ::WailmerModel)
        inbuilt("wailord", ::WailordModel)
        inbuilt("murkrow", ::MurkrowModel)
        inbuilt("honchkrow", ::HonchkrowModel)
        inbuilt("charcadet", ::CharcadetModel)
        inbuilt("sprigatito", ::SprigatitoModel)
        inbuilt("fuecoco", :: FuecocoModel)
        inbuilt("quaxly", :: QuaxlyModel)
        inbuilt("nacli", :: NacliModel)
        inbuilt("naclstack", :: NaclstackModel)
        inbuilt("dhelmise", :: DhelmiseModel)
        inbuilt("alcremie", :: AlcremieModel)
        inbuilt("milcery", :: MilceryModel)
        inbuilt("turtwig", :: TurtwigModel)
        inbuilt("grotle", :: GrotleModel)
        inbuilt("torterra", :: TorterraModel)
        inbuilt("xerneas", :: XerneasModel)
        inbuilt("klink", :: KlinkModel)
        inbuilt("klang", :: KlangModel)
        inbuilt("klinklang", :: KlinklangModel)
        inbuilt("morelull", :: MorelullModel)
        inbuilt("shiinotic", :: ShiinoticModel)
        inbuilt("joltik", :: JoltikModel)
        inbuilt("galvantula", :: GalvantulaModel)
    }

    fun inbuilt(name: String, model: (ModelPart) -> PokemonPoseableModel) {
        posers[cobblemonResource(name)] = model
    }



    fun registerJsonPosers(resourceManager: ResourceManager) {
        resourceManager.findResources("bedrock/posers") { path -> path.endsWith(".json") }.forEach { identifier, resource ->
            resource.inputStream.use { stream ->
                val json = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                posers[resolvedIdentifier] = {
                    JsonPokemonPoseableModel.JsonPokemonPoseableModelAdapter.modelPart = it
                    JsonPokemonPoseableModel.gson.fromJson(json, JsonPokemonPoseableModel::class.java)
                }
            }
        }
    }

    fun registerSpeciesAssetResolvers(resourceManager: ResourceManager) {
        val speciesToSpeciesVariationSets = mutableMapOf<Identifier, MutableList<SpeciesVariationSet>>()
        resourceManager.findResources("bedrock/species") { path -> path.endsWith(".json") }.forEach { identifier, resource ->
            resource.inputStream.use { stream ->
                val json = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                val speciesVariationSet = RegisteredSpeciesRendering.GSON.fromJson<SpeciesVariationSet>(json)
                speciesToSpeciesVariationSets.getOrPut(speciesVariationSet.species) { mutableListOf() }.add(speciesVariationSet)
            }
        }

        for ((species, speciesVariationSets) in speciesToSpeciesVariationSets) {
            val variations = speciesVariationSets.sortedBy { it.order }.flatMap { it.variations }.toMutableList()
            renders[species] = RegisteredSpeciesRendering(species, variations)
        }

        renders.values.forEach(RegisteredSpeciesRendering::initialize)
    }

    override fun registerAll() {
    }

    fun registerModels(resourceManager: ResourceManager) {
        var models = 0
        resourceManager.findResources("bedrock/models") { path -> path.endsWith(".geo.json") }.forEach { identifier, resource ->
            resource.inputStream.use { stream ->
                val json = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                texturedModels[resolvedIdentifier] = TexturedModel.from(json)
                models++
            }
        }
        Cobblemon.LOGGER.info("Loaded $models Pokémon models.")
    }

    override fun reload(resourceManager: ResourceManager) {
        Cobblemon.LOGGER.info("Loading Pokémon models...")
        this.renders.clear()
        this.posers.clear()
        registerPosers(resourceManager)
        registerModels(resourceManager)
        registerSpeciesAssetResolvers(resourceManager)
        initializeModelLayers()
    }

    fun getPoser(species: Species, aspects: Set<String>): PokemonPoseableModel {
        try {
            val poser = this.renders[species.resourceIdentifier]?.getPoser(aspects)
            if (poser != null) {
                return poser
            }
        } catch(e: IllegalStateException) {
//            e.printStackTrace()
        }
        return this.renders[cobblemonResource("substitute")]!!.getPoser(aspects)
    }

    fun getTexture(species: Species, aspects: Set<String>, state: PoseableEntityState<PokemonEntity>?): Identifier {
        try {
            val texture = this.renders[species.resourceIdentifier]?.getTexture(aspects, state?.animationSeconds ?: 0F)
            if (texture != null) {
                if (texture.exists()) {
                    return texture
                } else if (SHINY_ASPECT.aspect in aspects) {
                    // If the shiny texture doesn't exist, try parsing again but without the shiny - it doesn't seem to be implemented.
                    return getTexture(species, aspects - SHINY_ASPECT.aspect, state)
                }
            }
        } catch(_: IllegalStateException) { }
        return this.renders[cobblemonResource("substitute")]!!.getTexture(aspects, state?.animationSeconds ?: 0F)
    }

    fun getLayers(species: Species, aspects: Set<String>): Iterable<ModelLayer> {
        try {
            val layers = this.renders[species.resourceIdentifier]?.getLayers(aspects)
            if (layers != null) {
                return layers
            }
        } catch(_: IllegalStateException) { }
        return this.renders[cobblemonResource("substitute")]!!.getLayers(aspects)
    }
}