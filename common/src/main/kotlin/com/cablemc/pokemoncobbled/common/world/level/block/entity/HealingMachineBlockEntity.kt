package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
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
    private var currentUser: UUID? = null
    private var pokeBalls: MutableList<PokeBall> = mutableListOf()
    private var healTimeLeft: Int = 0
    var healingCharge: Float = 0.0f

    fun currentUser(): UUID? {
        return this.currentUser
    }

    fun pokeBalls(): List<PokeBall> {
        return this.pokeBalls
    }

    fun isInUse(): Boolean {
        return this.currentUser != null
    }

    fun setUser(user: UUID) {
        this.clearData()

        val player = user.getPlayer() ?: return
        val party = player.party()

        pokeBalls.clear()
        pokeBalls.addAll(party.getAll().map { pokemon -> pokemon.caughtBall })
        this.currentUser = user
        this.healTimeLeft = 60

        markUpdated()
    }

    fun canHeal(player: ServerPlayer): Boolean {
        if(PokemonCobbled.config.infiniteHealerCharge) {
            return true
        }
        val neededHealthPercent = player.party().teamHealingPercent()
        return this.healingCharge >= neededHealthPercent
    }

    fun activate(player: ServerPlayer) {
        if(!PokemonCobbled.config.infiniteHealerCharge) {
            val neededHealthPercent = player.party().teamHealingPercent()
            this.healingCharge -= neededHealthPercent
        }
        this.setUser(player.uuid)
    }

    fun completeHealing() {
        val player = this.currentUser?.getPlayer() ?: return clearData()
        val party = player.party()

        party.heal()
        player.sendMessage(TranslatableComponent("healingmachine.healed").withStyle(ChatFormatting.GREEN), Util.NIL_UUID)
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

        if(compoundTag.hasUUID("MachineUser")) {
            this.currentUser = compoundTag.getUUID("MachineUser")
        }
        if(compoundTag.contains("MachinePokeBalls")) {
            val pokeBallsTag = compoundTag.getCompound("MachinePokeBalls")
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
        if(compoundTag.contains("MachineTimeLeft")) {
            this.healTimeLeft = compoundTag.getInt("MachineTimeLeft")
        }
        if(compoundTag.contains("MachineCharge")) {
            this.healingCharge = compoundTag.getFloat("MachineCharge")
        }
    }

    override fun saveAdditional(compoundTag: CompoundTag) {
        super.saveAdditional(compoundTag)

        if(this.currentUser != null) {
            compoundTag.putUUID("MachineUser", this.currentUser!!)
        } else {
            compoundTag.remove("MachineUser")
        }

        if(pokeBalls.isNotEmpty()) {
            val pokeBallsTag = CompoundTag()
            var ballIndex = 1

            for(pokeBall in this.pokeBalls) {
                pokeBallsTag.putString("Pokeball$ballIndex", pokeBall.name.toString())
                ballIndex++
            }
            compoundTag.put("MachinePokeBalls", pokeBallsTag)
        } else {
            compoundTag.remove("MachinePokeBalls")
        }

        compoundTag.putInt("MachineTimeLeft", this.healTimeLeft)
        compoundTag.putFloat("MachineCharge", this.healingCharge)
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
            if(tileEntity.healTimeLeft > 0) {
                tileEntity.healTimeLeft--
            } else {
                tileEntity.completeHealing()
            }

            // Recharging
            val maxCharge = PokemonCobbled.config.maxHealerCharge
            if(tileEntity.healingCharge < maxCharge) {
                tileEntity.healingCharge = (tileEntity.healingCharge + PokemonCobbled.config.chargeGainedPerTick).coerceAtMost(maxCharge)
            }
        }
    }
}