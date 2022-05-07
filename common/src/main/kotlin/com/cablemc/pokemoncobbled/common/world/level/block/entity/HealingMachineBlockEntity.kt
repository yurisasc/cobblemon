package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.text.green
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.*
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class HealingMachineBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState
) : BlockEntity(CobbledBlockEntities.HEALING_MACHINE.get(), blockPos, blockState) {
    var currentUser: UUID? = null
        private set
    var pokeBalls: MutableList<PokeBall> = mutableListOf()
        private set
    private var healTimeLeft: Int = 0
    var healingCharge: Float = 0.0f
    val isInUse: Boolean
        get() = currentUser != null

    fun setUser(user: UUID) {
        this.clearData()

        val player = user.getPlayer() ?: return
        val party = player.party()

        pokeBalls.clear()
        pokeBalls.addAll(party.map { it.caughtBall })
        this.currentUser = user
        this.healTimeLeft = 60

        markUpdated()
    }

    fun canHeal(player: ServerPlayerEntity): Boolean {
        if (PokemonCobbled.config.infiniteHealerCharge) {
            return true
        }
        val neededHealthPercent = player.party().getHealingRemainderPercent()
        return this.healingCharge >= neededHealthPercent
    }

    fun activate(player: ServerPlayerEntity) {
        if (!PokemonCobbled.config.infiniteHealerCharge) {
            val neededHealthPercent = player.party().getHealingRemainderPercent()
            this.healingCharge -= neededHealthPercent
        }
        this.setUser(player.uuid)
    }

    fun completeHealing() {
        val player = this.currentUser?.getPlayer() ?: return clearData()
        val party = player.party()

        party.heal()
        player.sendServerMessage(lang("healingmachine.healed").green())
        this.clearData()
    }

    private fun clearData() {
        this.currentUser = null
        this.pokeBalls.clear()
        this.healTimeLeft = 0
        markUpdated()
    }

    override fun readNbt(compoundTag: NbtCompound) {
        super.readNbt(compoundTag)

        this.pokeBalls.clear()

        if (compoundTag.containsUuid(DataKeys.HEALER_MACHINE_USER)) {
            this.currentUser = compoundTag.getUuid(DataKeys.HEALER_MACHINE_USER)
        }
        if (compoundTag.contains(DataKeys.HEALER_MACHINE_POKEBALLS)) {
            val pokeBallsTag = compoundTag.getCompound(DataKeys.HEALER_MACHINE_POKEBALLS)
            for (key in pokeBallsTag.keys) {
                val pokeBallId = pokeBallsTag.getString(key)
                if (pokeBallId.isEmpty()) {
                    continue
                }

                val pokeBall = PokeBalls.getPokeBall(Identifier(pokeBallId))
                if (pokeBall != null) {
                    this.pokeBalls.add(pokeBall)
                }
            }
        }
        if (compoundTag.contains(DataKeys.HEALER_MACHINE_TIME_LEFT)) {
            this.healTimeLeft = compoundTag.getInt(DataKeys.HEALER_MACHINE_TIME_LEFT)
        }
        if (compoundTag.contains(DataKeys.HEALER_MACHINE_CHARGE)) {
            this.healingCharge = compoundTag.getFloat(DataKeys.HEALER_MACHINE_CHARGE)
        }
    }

    override fun writeNbt(compoundTag: NbtCompound) {
        super.writeNbt(compoundTag)

        if (this.currentUser != null) {
            compoundTag.putUuid(DataKeys.HEALER_MACHINE_USER, this.currentUser!!)
        } else {
            compoundTag.remove(DataKeys.HEALER_MACHINE_USER)
        }

        if (pokeBalls.isNotEmpty()) {
            val pokeBallsTag = NbtCompound()
            var ballIndex = 1

            for (pokeBall in this.pokeBalls) {
                pokeBallsTag.putString("Pokeball$ballIndex", pokeBall.name.toString())
                ballIndex++
            }
            compoundTag.put(DataKeys.HEALER_MACHINE_POKEBALLS, pokeBallsTag)
        } else {
            compoundTag.remove(DataKeys.HEALER_MACHINE_POKEBALLS)
        }

        compoundTag.putInt(DataKeys.HEALER_MACHINE_TIME_LEFT, this.healTimeLeft)
        compoundTag.putFloat(DataKeys.HEALER_MACHINE_CHARGE, this.healingCharge)
    }

    override fun toUpdatePacket() =  BlockEntityUpdateS2CPacket.create(this)
    override fun toInitialChunkDataNbt(): NbtCompound {
        return super.createNbtWithIdentifyingData()
    }

    private fun markUpdated() {
        this.markDirty()
        world!!.updateListeners(pos, this.cachedState, this.cachedState, 3)
    }

    companion object : BlockEntityTicker<HealingMachineBlockEntity> {
        override fun tick(world: World, blockPos: BlockPos, blockState: BlockState, tileEntity: HealingMachineBlockEntity) {
            if (world.isClient) {
                return
            }

            // Healing progression
            if (tileEntity.isInUse) {
                if (tileEntity.healTimeLeft > 0) {
                    tileEntity.healTimeLeft--
                } else {
                    tileEntity.completeHealing()
                }
            } else {
                // Recharging
                val maxCharge = PokemonCobbled.config.maxHealerCharge
                if (tileEntity.healingCharge < maxCharge) {
                    tileEntity.healingCharge = (tileEntity.healingCharge + PokemonCobbled.config.chargeGainedPerTick).coerceAtMost(maxCharge)
                }
            }
        }
    }
}