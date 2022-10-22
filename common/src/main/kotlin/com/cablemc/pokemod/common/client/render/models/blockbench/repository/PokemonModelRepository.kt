/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.repository

//import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen3.*
//import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen5.*
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
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.CleffaModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.ElekidModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.EspeonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.IgglybuffModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.MagbyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.PichuModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.Porygon2Model
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.ScizorModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.SmoochumModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.SteelixModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.TyrogueModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2.UmbreonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.ElectivireModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.GlaceonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.HappinyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.LeafeonModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.LickilickyModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MagmortarModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MagnezoneModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MimejrModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.MunchlaxModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.PorygonzModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.RhyperiorModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen4.TangrowthModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen6.SylveonModel
import com.cablemc.pokemod.common.client.render.pokemon.ModelLayer
import com.cablemc.pokemod.common.client.render.pokemon.RegisteredSpeciesRendering
import com.cablemc.pokemod.common.client.render.pokemon.SpeciesAssetResolver
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.pokemon.Species
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
        inbuilt("bulbasaur") { BulbasaurModel(it) }
        inbuilt("ivysaur") { IvysaurModel(it) }
        inbuilt("venusaur") { VenusaurModel(it) }
        inbuilt("charmander") { CharmanderModel(it) }
        inbuilt("charmeleon") { CharmeleonModel(it) }
        inbuilt("charizard") { CharizardModel(it) }
        inbuilt("squirtle") { SquirtleModel(it) }
        inbuilt("wartortle") { WartortleModel(it) }
        inbuilt("blastoise") { BlastoiseModel(it) }
        inbuilt("caterpie") { CaterpieModel(it) }
        inbuilt("metapod") { MetapodModel(it) }
        inbuilt("butterfree") { ButterfreeModel(it) }
        inbuilt("weedle") { WeedleModel(it) }
        inbuilt("kakuna") { KakunaModel(it) }
        inbuilt("beedrill") { BeedrillModel(it) }
        inbuilt("rattata") { RattataModel(it) }
        inbuilt("raticate") { RaticateModel(it) }
        inbuilt("eevee") { EeveeModel(it) }
        inbuilt("magikarp") { MagikarpModel(it) }
        inbuilt("gyarados") { GyaradosModel(it) }
        inbuilt("pidgey") { PidgeyModel(it) }
        inbuilt("pidgeotto") { PidgeottoModel(it) }
        inbuilt("pidgeot") { PidgeotModel(it) }
        inbuilt("diglett") { DiglettModel(it) }
        inbuilt("dugtrio") { DugtrioModel(it) }
        inbuilt("zubat") { ZubatModel(it) }
        inbuilt("cleffa") { CleffaModel(it) }
        inbuilt("clefable") { ClefableModel(it) }
        inbuilt("clefairy") { ClefairyModel(it) }
        inbuilt("krabby") { KrabbyModel(it) }
        inbuilt("paras") { ParasModel(it) }
        inbuilt("parasect") { ParasectModel(it) }
        inbuilt("mankey") { MankeyModel(it) }
        inbuilt("primeape") { PrimeapeModel(it) }
        inbuilt("oddish") { OddishModel(it) }
        inbuilt("gloom") { GloomModel(it) }
        inbuilt("vileplume") { VileplumeModel(it) }
        inbuilt("bellossom") { BellossomModel(it) }
        inbuilt("voltorb") { VoltorbModel(it) }
        inbuilt("electrode") { ElectrodeModel(it) }
        inbuilt("lapras") { LaprasModel(it) }
        inbuilt("ekans") { EkansModel(it) }
        inbuilt("machop") { MachopModel(it) }
        inbuilt("machoke") { MachokeModel(it) }
        inbuilt("machamp") { MachampModel(it) }
        inbuilt("abra") { AbraModel(it) }
        inbuilt("aerodactyl") { AerodactylModel(it) }
        inbuilt("alakazam") { AlakazamModel(it) }
        inbuilt("arbok") { ArbokModel(it) }
        inbuilt("arcanine") { ArcanineModel(it) }
        inbuilt("articuno") { ArticunoModel(it) }
        inbuilt("bellsprout") { BellsproutModel(it) }
        inbuilt("chansey") { ChanseyModel(it) }
        inbuilt("cloyster") { CloysterModel(it) }
        inbuilt("cubone") { CuboneModel(it) }
        inbuilt("dewgong") { DewgongModel(it) }
        inbuilt("ditto") { DittoModel(it) }
        inbuilt("dodrio") { DodrioModel(it) }
        inbuilt("doduo") { DoduoModel(it) }
        inbuilt("dragonair") { DragonairModel(it) }
        inbuilt("dragonite") { DragoniteModel(it) }
        inbuilt("dratini") { DratiniModel(it) }
        inbuilt("drowzee") { DrowzeeModel(it) }
        inbuilt("electabuzz") { ElectabuzzModel(it) }
        inbuilt("exeggcute") { ExeggcuteModel(it) }
        inbuilt("exeggutor") { ExeggutorModel(it) }
        inbuilt("farfetchd") { FarfetchdModel(it) }
        inbuilt("fearow") { FearowModel(it) }
        inbuilt("flareon") { FlareonModel(it) }
        inbuilt("gastly") { GastlyModel(it) }
        inbuilt("gengar") { GengarModel(it) }
        inbuilt("geodude") { GeodudeModel(it) }
        inbuilt("golbat") { GolbatModel(it) }
        inbuilt("goldeen") { GoldeenModel(it) }
        inbuilt("golduck") { GolduckModel(it) }
        inbuilt("golem") { GolemModel(it) }
        inbuilt("graveler") { GravelerModel(it) }
        inbuilt("grimer") { GrimerModel(it) }
        inbuilt("growlithe") { GrowlitheModel(it) }
        inbuilt("haunter") { HaunterModel(it) }
        inbuilt("hitmonchan") { HitmonchanModel(it) }
        inbuilt("hitmonlee") { HitmonleeModel(it) }
        inbuilt("horsea") { HorseaModel(it) }
        inbuilt("hypno") { HypnoModel(it) }
        inbuilt("jigglypuff") { JigglypuffModel(it) }
        inbuilt("jolteon") { JolteonModel(it) }
        inbuilt("jynx") { JynxModel(it) }
        inbuilt("kabuto") { KabutoModel(it) }
        inbuilt("kabutops") { KabutopsModel(it) }
        inbuilt("kadabra") { KadabraModel(it) }
        inbuilt("kangaskhan") { KangaskhanModel(it) }
        inbuilt("kingler") { KinglerModel(it) }
        inbuilt("koffing") { KoffingModel(it) }
        inbuilt("krabby") { KrabbyModel(it) }
        inbuilt("lickitung") { LickitungModel(it) }
        inbuilt("magmar") { MagmarModel(it) }
        inbuilt("magnemite") { MagnemiteModel(it) }
        inbuilt("magneton") { MagnetonModel(it) }
        inbuilt("marowak") { MarowakModel(it) }
        inbuilt("meowth") { MeowthModel(it) }
        inbuilt("mew") { MewModel(it) }
        inbuilt("mewtwo") { MewtwoModel(it) }
        inbuilt("moltres") { MoltresModel(it) }
        inbuilt("mrmime") { MrmimeModel(it) }
        inbuilt("muk") { MukModel(it) }
        inbuilt("nidoking") { NidokingModel(it) }
        inbuilt("nidoqueen") { NidoqueenModel(it) }
        inbuilt("nidoranf") { NidoranfModel(it) }
        inbuilt("nidoranm") { NidoranmModel(it) }
        inbuilt("nidorina") { NidorinaModel(it) }
        inbuilt("nidorino") { NidorinoModel(it) }
        inbuilt("ninetales") { NinetalesModel(it) }
        inbuilt("omanyte") { OmanyteModel(it) }
        inbuilt("omastar") { OmastarModel(it) }
        inbuilt("onix") { OnixModel(it) }
        inbuilt("persian") { PersianModel(it) }
        inbuilt("pikachu") { PikachuModel(it) }
        inbuilt("pinsir") { PinsirModel(it) }
        inbuilt("poliwag") { PoliwagModel(it) }
        inbuilt("poliwhirl") { PoliwhirlModel(it) }
        inbuilt("poliwrath") { PoliwrathModel(it) }
        inbuilt("ponyta") { PonytaModel(it) }
        inbuilt("porygon") { PorygonModel(it) }
        inbuilt("psyduck") { PsyduckModel(it) }
        inbuilt("raichu") { RaichuModel(it) }
        inbuilt("rapidash") { RapidashModel(it) }
        inbuilt("rhydon") { RhydonModel(it) }
        inbuilt("rhyhorn") { RhyhornModel(it) }
        inbuilt("sandshrew") { SandshrewModel(it) }
        inbuilt("sandslash") { SandslashModel(it) }
        inbuilt("scyther") { ScytherModel(it) }
        inbuilt("seadra") { SeadraModel(it) }
        inbuilt("seaking") { SeakingModel(it) }
        inbuilt("seel") { SeelModel(it) }
        inbuilt("shellder") { ShellderModel(it) }
        inbuilt("slowbro") { SlowbroModel(it) }
        inbuilt("slowpoke") { SlowpokeModel(it) }
        inbuilt("snorlax") { SnorlaxModel(it) }
        inbuilt("spearow") { SpearowModel(it) }
        inbuilt("starmie") { StarmieModel(it) }
        inbuilt("staryu") { StaryuModel(it) }
        inbuilt("steelix") { SteelixModel(it) }
        inbuilt("tangela") { TangelaModel(it) }
        inbuilt("tauros") { TaurosModel(it) }
        inbuilt("tentacool") { TentacoolModel(it) }
        inbuilt("tentacruel") { TentacruelModel(it) }
        inbuilt("vaporeon") { VaporeonModel(it) }
        inbuilt("venomoth") { VenomothModel(it) }
        inbuilt("venonat") { VenonatModel(it) }
        inbuilt("victreebel") { VictreebelModel(it) }
        inbuilt("vulpix") { VulpixModel(it) }
        inbuilt("weepinbell") { WeepinbellModel(it) }
        inbuilt("weezing") { WeezingModel(it) }
        inbuilt("wigglytuff") { WigglytuffModel(it) }
        inbuilt("zapdos") { ZapdosModel(it) }
        inbuilt("elekid") { ElekidModel(it) }
        inbuilt("igglybuff") { IgglybuffModel(it) }
        inbuilt("magby") { MagbyModel(it) }
        inbuilt("pichu") { PichuModel(it) }
        inbuilt("smoochum") { SmoochumModel(it) }
        inbuilt("tyrogue") { TyrogueModel(it) }
        inbuilt("electivire") { ElectivireModel(it) }
        inbuilt("glaceon") { GlaceonModel(it) }
        inbuilt("happiny") { HappinyModel(it) }
        inbuilt("leafeon") { LeafeonModel(it) }
        inbuilt("lickilicky") { LickilickyModel(it) }
        inbuilt("magmortar") { MagmortarModel(it) }
        inbuilt("magnezone") { MagnezoneModel(it) }
        inbuilt("mimejr") { MimejrModel(it) }
        inbuilt("munchlax") { MunchlaxModel(it) }
        inbuilt("porygon2") { Porygon2Model(it) }
        inbuilt("porygonz") { PorygonzModel(it) }
        inbuilt("rhyperior") { RhyperiorModel(it) }
        inbuilt("scizor") { ScizorModel(it) }
        inbuilt("tangrowth") { TangrowthModel(it) }
        inbuilt("sylveon") { SylveonModel(it) }
        inbuilt("umbreon") { UmbreonModel(it) }
        inbuilt("espeon") { EspeonModel(it) }
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
                return texture
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