/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.repository

import com.cablemc.pokemod.common.Pokemod
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.JsonPokemonPoseableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.AbraModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.AerodactylModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.AlakazamModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ArbokModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ArcanineModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ArticunoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.BeedrillModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.BellsproutModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.BlastoiseModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.BulbasaurModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ButterfreeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.CaterpieModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ChanseyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.CharizardModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.CharmanderModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.CharmeleonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ClefableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ClefairyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.CloysterModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.CuboneModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DewgongModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DiglettModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DittoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DodrioModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DoduoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DragonairModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DragoniteModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DratiniModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DrowzeeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.DugtrioModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.EeveeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.EkansModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ElectabuzzModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ElectrodeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ExeggcuteModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ExeggutorModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.FarfetchdModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.FearowModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.FlareonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GastlyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GengarModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GeodudeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GloomModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GolbatModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GoldeenModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GolduckModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GolemModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GravelerModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GrimerModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GrowlitheModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.GyaradosModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.HaunterModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.HitmonchanModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.HitmonleeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.HorseaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.HypnoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.IvysaurModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.JigglypuffModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.JolteonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.JynxModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KabutoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KabutopsModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KadabraModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KakunaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KangaskhanModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KinglerModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KoffingModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.KrabbyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.LaprasModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.LickitungModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MachampModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MachokeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MachopModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MagikarpModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MagmarModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MagnemiteModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MagnetonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MankeyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MarowakModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MeowthModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MetapodModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MewModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MewtwoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MoltresModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MrmimeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.MukModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NidokingModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NidoqueenModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NidoranfModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NidoranmModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NidorinaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NidorinoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.NinetalesModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.OddishModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.OmanyteModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.OmastarModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.OnixModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ParasModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ParasectModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PersianModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PidgeotModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PidgeottoModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PidgeyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PikachuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PinsirModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PoliwagModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PoliwhirlModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PoliwrathModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PonytaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PorygonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PrimeapeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.PsyduckModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.RaichuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.RapidashModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.RaticateModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.RattataModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.RhydonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.RhyhornModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SandshrewModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SandslashModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ScytherModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SeadraModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SeakingModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SeelModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ShellderModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SlowbroModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SlowpokeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SnorlaxModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SpearowModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.SquirtleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.StarmieModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.StaryuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.TangelaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.TaurosModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.TentacoolModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.TentacruelModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VaporeonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VenomothModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VenonatModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VenusaurModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VictreebelModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VileplumeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VoltorbModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.VulpixModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.WartortleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.WeedleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.WeepinbellModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.WeezingModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.WigglytuffModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ZapdosModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1.ZubatModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.BellossomModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.BlisseyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.CleffaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.ElekidModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.EspeonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.HitmontopModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.IgglybuffModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.KingdraModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.MagbyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.PichuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.PiloswineModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.PolitoedModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.Porygon2Model
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.QuagsireModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.ScizorModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.SlowkingModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.SmoochumModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.SteelixModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.SwinubModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.TyrogueModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.UmbreonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.WooperModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.YanmaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.BlazikenModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.CombuskenModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.MarshtompModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.MinunModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.MudkipModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.PlusleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.RayquazaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.SwampertModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.TorchicModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.BibarelModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.BidoofModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.BunearyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.ElectivireModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.EmpoleonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.GlaceonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.HappinyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.LeafeonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.LickilickyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.LopunnyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MagmortarModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MagnezoneModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MamoswineModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MimejrModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MunchlaxModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.PachirisuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.PiplupModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.PorygonzModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.PrinplupModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.RhyperiorModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.TangrowthModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.YanmegaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen5.BasculinModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen5.CrustleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen5.DwebbleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen5.EmolgaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen5.MaractusModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen6.SylveonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.BounsweetModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.DartrixModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.DecidueyeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.IncineroarModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.LittenModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.MimikyuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.NaganadelModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.PoipoleModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.RowletModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.SteeneeModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.TorracatModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen7.TsareenaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen8.CentiskorchModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen8.KleavorModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen8.SizzlipedeModel
import com.cablemc.pokemod.common.client.render.pokemon.ModelLayer
import com.cablemc.pokemod.common.client.render.pokemon.RegisteredSpeciesRendering
import com.cablemc.pokemod.common.client.render.pokemon.SpeciesAssetResolver
import com.cablemc.pokemod.common.client.util.exists
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.pokemon.Species
import com.cablemc.pokemod.common.pokemon.aspects.SHINY_ASPECT
import com.cablemc.pokemod.common.util.endsWith
import com.cablemc.pokemod.common.util.pokemodResource
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.io.path.Path
import kotlin.io.path.pathString
import net.minecraft.client.model.ModelPart
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

object PokemonModelRepository : ModelRepository<PokemonEntity>() {
    val posers = mutableMapOf<Identifier, (ModelPart) -> PokemonPoseableModel>()
    val renders = mutableMapOf<Identifier, RegisteredSpeciesRendering>()

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
    }

    fun inbuilt(name: String, model: (ModelPart) -> PokemonPoseableModel) {
        posers[pokemodResource(name)] = model
    }

    fun registerJsonPosers(resourceManager: ResourceManager) {
        resourceManager.findResources(Path("bedrock/posers").pathString) { path -> path.endsWith(".json") }.forEach { identifier, resource ->
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
        resourceManager.findResources(Path("bedrock/species").pathString) { path -> path.endsWith(".json") }.forEach { identifier, resource ->
            resource.inputStream.use { stream ->
                val json = String(stream.readAllBytes(), StandardCharsets.UTF_8)
                val resolvedIdentifier = Identifier(identifier.namespace, File(identifier.path).nameWithoutExtension)
                renders[resolvedIdentifier] = RegisteredSpeciesRendering(
                    resolvedIdentifier,
                    SpeciesAssetResolver.GSON.fromJson(json, SpeciesAssetResolver::class.java)
                )
            }
        }
    }

    override fun registerAll() {
    }

    override fun reload(resourceManager: ResourceManager) {
        Pokemod.LOGGER.info("Initializing Pokémon models")
        this.renders.clear()
        this.posers.clear()
        registerPosers(resourceManager)
        registerSpeciesAssetResolvers(resourceManager)
        initializeModelLayers()
    }

    var brokenOnes = mutableListOf<Species>()

    fun getPoser(species: Species, aspects: Set<String>): PokemonPoseableModel {
        try {
            val poser = this.renders[species.resourceIdentifier]?.getPoser(aspects)
            if (poser != null) {
                return poser
            }
        } catch(e: IllegalStateException) {
//            e.printStackTrace()
        }
        return this.renders[pokemodResource("substitute")]!!.getPoser(aspects)
    }

    fun getTexture(species: Species, aspects: Set<String>): Identifier {
        try {
            val texture = this.renders[species.resourceIdentifier]?.getTexture(aspects)
            if (texture != null) {
                if (texture.exists()) {
                    return texture
                } else if (SHINY_ASPECT.aspect in aspects) {
                    // If the shiny texture doesn't exist, try parsing again but without the shiny - it doesn't seem to be implemented.
                    return getTexture(species, aspects - SHINY_ASPECT.aspect)
                }
            }
        } catch(_: IllegalStateException) { }
        return this.renders[pokemodResource("substitute")]!!.getTexture(aspects)
    }

    fun getLayers(species: Species, aspects: Set<String>): List<ModelLayer> {
        try {
            val layers = this.renders[species.resourceIdentifier]?.getLayers(aspects)
            if (layers != null) {
                return layers
            }
        } catch(_: IllegalStateException) { }
        return this.renders[pokemodResource("substitute")]!!.getLayers(aspects)
    }
}