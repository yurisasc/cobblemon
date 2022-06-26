package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.registry.CompletableRegistry
import com.cablemc.pokemoncobbled.common.world.level.block.entity.HealingMachineBlockEntity
import dev.architectury.registry.registries.RegistrySupplier
import java.util.function.Supplier
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry

object CobbledBlockEntities : CompletableRegistry<BlockEntityType<*>>(Registry.BLOCK_ENTITY_TYPE_KEY) {
    private fun <T : BlockEntityType<*>> register(name: String, blockEntityType: Supplier<T>) : RegistrySupplier<T> {
        return queue(name, blockEntityType)
    }

    val HEALING_MACHINE = register("healing_machine") { BlockEntityType.Builder.create(::HealingMachineBlockEntity, CobbledBlocks.HEALING_MACHINE.get()).build(null) }
    val PC = register("pc") { BlockEntityType.Builder.create(::PCBlockEntity, CobbledBlocks.PC.get()).build(null) }
}