//package ca.landonjw.kotlinmon.client.render.models
//
//import com.cablemc.kotlinmon.client.render.models.smd.SmdModel
//import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.SmdAnimationLoader
//import com.cablemc.kotlinmon.client.render.models.smd.loaders.SmdModelLoader
//import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.SmdPQCLoader
//import com.cablemc.pokemoncobbled.client.render.models.smd.loaders.files.SmdAnimationFileLoader
//import com.cablemc.kotlinmon.client.render.models.smd.loaders.files.SmdModelFileLoader
//import com.cablemc.kotlinmon.client.render.models.smd.loaders.files.SmdPQCFileLoader
//import com.cablemc.kotlinmon.client.render.models.smd.renderer.SmdModelRenderer
//import com.cablemc.pokemoncobbled.client.render.models.smd.repository.CachedModelRepository
//import com.cablemc.pokemoncobbled.client.render.models.smd.repository.DefaultModelRepository
//import com.cablemc.pokemoncobbled.client.render.models.smd.repository.ModelRepository
//import com.google.common.cache.CacheBuilder
//import com.google.common.cache.CacheLoader
//import com.google.common.cache.LoadingCache
//import net.minecraft.resources.ResourceLocation
//import org.kodein.di.DI
//import org.kodein.di.bind
//import org.kodein.di.instance
//import org.kodein.di.singleton
//import java.util.concurrent.TimeUnit
//
//object SmdModelModule {
//
//    operator fun invoke() = DI.Module(name = "SMD Models") {
//        bind<SmdPQCFileLoader>() with singleton { SmdPQCFileLoader() }
//        bind<SmdModelFileLoader>() with singleton { SmdModelFileLoader() }
//        bind<SmdAnimationFileLoader>() with singleton { SmdAnimationFileLoader() }
//
//        bind<SmdPQCLoader>() with singleton { SmdPQCLoader(instance(), instance(), instance()) }
//        bind<SmdModelLoader>() with singleton { SmdModelLoader(instance()) }
//        bind<SmdAnimationLoader>() with singleton { SmdAnimationLoader(instance()) }
//
//        bind<ModelRepository>() with singleton { DefaultModelRepository(instance()) }
//        bind<ModelRepository>("cache") with singleton { CachedModelRepository(getModelCache(instance())) }
//
//        bind<SmdModelRenderer>() with singleton { SmdModelRenderer() }
//    }
//
//    private fun getModelCache(pqcLoader: SmdPQCLoader): LoadingCache<ResourceLocation, SmdModel> {
//        val loader = object : CacheLoader<ResourceLocation, SmdModel>() {
//            override fun load(key: ResourceLocation): SmdModel {
//                return pqcLoader.load(key)
//            }
//        }
//        return CacheBuilder.newBuilder()
//            .expireAfterAccess(60, TimeUnit.SECONDS)
//            .build(loader)
//    }
//
//}