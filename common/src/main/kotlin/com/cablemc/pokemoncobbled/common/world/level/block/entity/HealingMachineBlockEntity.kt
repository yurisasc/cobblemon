package com.cablemc.pokemoncobbled.common.world.level.block.entity

import com.cablemc.pokemoncobbled.common.CobbledBlockEntities
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.cablemc.pokemoncobbled.common.util.party
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceLocation
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

        pokeBalls.addAll(party.getAll().map { pokemon -> pokemon.caughtBall })
        this.currentUser = user
        this.healTimeLeft = 60

        markUpdated()
    }

    fun completeHealing() {
        val player = this.currentUser?.getPlayer() ?: return clearData()
        val party = player.party()

        // TODO: Trigger event

        party.heal()
        player.sendMessage(TextComponent("${ChatFormatting.GREEN}Your Pokemon have been healed!"), Util.NIL_UUID)
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
    }

    override fun saveAdditional(compoundTag: CompoundTag) {
        super.saveAdditional(compoundTag)

        compoundTag.putUUID("MachineUser", this.currentUser!!)

        val pokeBallsTag = CompoundTag()
        var ballIndex = 1

        for(pokeBall in this.pokeBalls) {
            pokeBallsTag.putString("Pokeball$ballIndex", pokeBall.name.toString())
            ballIndex++
        }
        compoundTag.put("MachinePokeBalls", pokeBallsTag)
        compoundTag.putInt("MachineTimeLeft", healTimeLeft)
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

            if(tileEntity.healTimeLeft > 0) {
                tileEntity.healTimeLeft--
                return
            }
            tileEntity.completeHealing()
        }
    }
}