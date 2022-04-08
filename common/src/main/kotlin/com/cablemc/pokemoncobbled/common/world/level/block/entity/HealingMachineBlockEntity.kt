package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.api.text.green
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.*
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import java.util.*

class HealingMachineBlockEntity(
    blockPos: BlockPos,
    blockState: BlockState) : BlockEntity(CobbledBlockEntities.HEALING_MACHINE.get(), blockPos, blockState
) {
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

    fun canHeal(player: ServerPlayer): Boolean {
        if(PokemonCobbled.config.infiniteHealerCharge) {
            return true
        }
        val neededHealthPercent = player.party().getHealingRemainderPercent()
        return this.healingCharge >= neededHealthPercent
    }

    fun activate(player: ServerPlayer) {
        if(!PokemonCobbled.config.infiniteHealerCharge) {
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

    override fun load(compoundTag: CompoundTag) {
        super.load(compoundTag)

        this.pokeBalls.clear()

        if(compoundTag.hasUUID(DataKeys.HEALER_MACHINE_USER)) {
            this.currentUser = compoundTag.getUUID(DataKeys.HEALER_MACHINE_USER)
        }
        if(compoundTag.contains(DataKeys.HEALER_MACHINE_POKEBALLS)) {
            val pokeBallsTag = compoundTag.getCompound(DataKeys.HEALER_MACHINE_POKEBALLS)
            for(key in pokeBallsTag.allKeys) {
                val pokeBallId = pokeBallsTag.getString(key)
                if(pokeBallId.isEmpty()) {
                    continue
                }

                val pokeBall = PokeBalls.getPokeBall(ResourceLocation(pokeBallId))
                if(pokeBall != null) {
                    this.pokeBalls.add(pokeBall)
                }
            }
        }
        if(compoundTag.contains(DataKeys.HEALER_MACHINE_TIME_LEFT)) {
            this.healTimeLeft = compoundTag.getInt(DataKeys.HEALER_MACHINE_TIME_LEFT)
        }
        if(compoundTag.contains(DataKeys.HEALER_MACHINE_CHARGE)) {
            this.healingCharge = compoundTag.getFloat(DataKeys.HEALER_MACHINE_CHARGE)
        }
    }

    override fun saveAdditional(compoundTag: CompoundTag) {
        super.saveAdditional(compoundTag)

        if(this.currentUser != null) {
            compoundTag.putUUID(DataKeys.HEALER_MACHINE_USER, this.currentUser!!)
        } else {
            compoundTag.remove(DataKeys.HEALER_MACHINE_USER)
        }

        if(pokeBalls.isNotEmpty()) {
            val pokeBallsTag = CompoundTag()
            var ballIndex = 1

            for(pokeBall in this.pokeBalls) {
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

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(): CompoundTag? {
        return saveWithoutMetadata()
    }

    private fun markUpdated() {
        this.setChanged()
        getLevel()!!.sendBlockUpdated(this.blockPos, blockState, blockState, 3)
    }

    companion object : BlockEntityTicker<HealingMachineBlockEntity> {
        override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, tileEntity: HealingMachineBlockEntity) {
            if(level.isClientSide) {
                return
            }

            // Healing progression
            if(tileEntity.isInUse) {
                if(tileEntity.healTimeLeft > 0) {
                    tileEntity.healTimeLeft--
                } else {
                    tileEntity.completeHealing()
                }
            } else {
                // Recharging
                val maxCharge = PokemonCobbled.config.maxHealerCharge
                if(tileEntity.healingCharge < maxCharge) {
                    tileEntity.healingCharge = (tileEntity.healingCharge + PokemonCobbled.config.chargeGainedPerTick).coerceAtMost(maxCharge)
                }
            }
        }
    }
}