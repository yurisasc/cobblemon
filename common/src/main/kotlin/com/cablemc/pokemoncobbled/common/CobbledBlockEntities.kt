package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.world.level.block.entity.HealingMachineBlockEntity
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object CobbledBlockEntities {
    private val registry = DeferredRegister.create(PokemonCobbled.MODID, Registry.BLOCK_ENTITY_TYPE_KEY)

    fun register() {
        registry.register()
    }

    private fun <T : BlockEntityType<*>> register(name: String, blockEntityType: Supplier<T>) : RegistrySupplier<T> {
        return registry.register(name, blockEntityType)
    }

    val HEALING_MACHINE = register("healing_machine") { BlockEntityType.Builder.create(::HealingMachineBlockEntity, CobbledBlocks.HEALING_MACHINE.get()).build(null) }
}