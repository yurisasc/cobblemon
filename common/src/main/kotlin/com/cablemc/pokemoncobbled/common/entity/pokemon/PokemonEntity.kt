package com.cablemc.pokemoncobbled.common.entity.pokemon

import com.cablemc.pokemoncobbled.common.CobbledEntities
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.entity.Despawner
import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents
import com.cablemc.pokemoncobbled.common.api.events.pokemon.ShoulderMountEvent
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.scheduling.afterOnMain
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes
import com.cablemc.pokemoncobbled.common.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.common.entity.EntityProperty
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.activestate.ShoulderedState
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getBitForByte
import com.cablemc.pokemoncobbled.common.util.setBitForByte
import dev.architectury.extensions.network.EntitySpawnExtension
import dev.architectury.networking.NetworkManager
import net.minecraft.block.BlockState
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityPose
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.goal.FollowOwnerGoal
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.ai.goal.LookAtEntityGoal
import net.minecraft.entity.ai.goal.WanderAroundGoal
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.passive.TameableShoulderEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

class PokemonEntity(
    level: World,
    pokemon: Pokemon = Pokemon(),
    type: EntityType<out PokemonEntity> = CobbledEntities.POKEMON_TYPE,
) : TameableShoulderEntity(type, level), EntitySpawnExtension {
    var pokemon: Pokemon = pokemon
        set(value) {
            field = value
            delegate.changePokemon(value)
        }
    var despawner: Despawner<PokemonEntity> = PokemonCobbled.defaultPokemonDespawner

    val delegate = if (level.isClient) {
        // Don't import because scanning for imports is a CI job we'll do later to detect errant access to client from server
        PokemonClientDelegate()
    } else {
        PokemonServerDelegate()
    }

    var ticksLived = 0
    val busyLocks = mutableListOf<Any>()
    val isBusy: Boolean
        get() = busyLocks.isNotEmpty()

    val entityProperties = mutableListOf<EntityProperty<*>>()

    val dexNumber = addEntityProperty(SPECIES_DEX, pokemon.species.nationalPokedexNumber)
    val shiny = addEntityProperty(SHINY, pokemon.shiny)
    val isMoving = addEntityProperty(MOVING, false)
    val behaviourFlags = addEntityProperty(BEHAVIOUR_FLAGS, 0)
    val phasingTargetId = addEntityProperty(PHASING_TARGET_ID, -1)
    val battleId = addEntityProperty(BATTLE_ID, Optional.empty())

    /**
     * 0 is do nothing,
     * 1 is appearing from a pokeball so needs to be small then grows,
     * 2 is being captured/recalling so starts large and shrinks.
     */
    val beamModeEmitter = addEntityProperty(BEAM_MODE, 0.toByte())
    // properties like the above are synced and can be subscribed to for changes on either side

    init {
        delegate.initialize(this)
        delegate.changePokemon(pokemon)
        calculateDimensions()

        battleId
            .subscribeIncludingCurrent {
                if (it.isPresent) {
                    busyLocks.add(BATTLE_LOCK)
                } else {
                    busyLocks.remove(BATTLE_LOCK)
                }
            }
            .unsubscribeWhen { isRemoved }
    }

    companion object {
        private val SPECIES_DEX = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        private val SHINY = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        private val MOVING = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        private val BEHAVIOUR_FLAGS = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BYTE)
        private val PHASING_TARGET_ID = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.INTEGER)
        private val BEAM_MODE = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.BYTE)
        private val BATTLE_ID = DataTracker.registerData(PokemonEntity::class.java, TrackedDataHandlerRegistry.OPTIONAL_UUID)


        const val BATTLE_LOCK = "battle"
    }

    override fun tick() {
        super.tick()
        // We will be handling idle logic ourselves thank you
        this.setDespawnCounter(0)
        entityProperties.forEach { it.checkForUpdate() }
        delegate.tick(this)
        ticksLived++
    }

    /**
     * Prevents water type Pokémon from taking drowning damage.
     */
    override fun canBreatheInWater(): Boolean {
        return ElementalTypes.WATER in pokemon.types
    }

    /**
     * Prevents fire type Pokémon from taking fire damage.
     */
    override fun isFireImmune(): Boolean {
        return ElementalTypes.FIRE in pokemon.types
    }

    /**
     * Prevents flying type Pokémon from taking fall damage.
     */
    override fun fall(pY: Double, pOnGround: Boolean, pState: BlockState, pPos: BlockPos) {
        if (ElementalTypes.FLYING in pokemon.types) {
            fallDistance = 0F
        } else {
            super.fall(pY, pOnGround, pState, pPos)
        }
    }

    override fun saveNbt(nbt: NbtCompound): Boolean {
        nbt.put(DataKeys.POKEMON, pokemon.saveToNBT(NbtCompound()))
        return super.saveNbt(nbt)
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        nbt.put(DataKeys.POKEMON, pokemon.saveToNBT(NbtCompound()))
        return super.writeNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        pokemon = Pokemon().loadFromNBT(nbt.getCompound(DataKeys.POKEMON))
        dexNumber.set(pokemon.species.nationalPokedexNumber)
        shiny.set(pokemon.shiny)
        speed = 0.35F
    }

    override fun createSpawnPacket(): Packet<*> {
        return NetworkManager.createAddEntityPacket(this)
    }

    public override fun initGoals() {
        goalSelector.clear()
        goalSelector.add(0, object : Goal() {
            override fun canStart() = this@PokemonEntity.phasingTargetId.get() != -1
            override fun getControls() = EnumSet.allOf(Control::class.java)
        })
        goalSelector.add(3, FollowOwnerGoal(this, 0.6, 8F, 2F, false))
        goalSelector.add(4, WanderAroundGoal(this, 0.33))
        goalSelector.add(5, LookAtEntityGoal(this, ServerPlayerEntity::class.java, 5F))
    }

    fun <T> addEntityProperty(accessor: TrackedData<T>, initialValue: T): EntityProperty<T> {
        val property = EntityProperty(
            dataTracker = dataTracker,
            accessor = accessor,
            initialValue = initialValue
        )
        entityProperties.add(property)
        return property
    }

    override fun createChild(level: ServerWorld, partner: PassiveEntity) = null

    override fun isReadyToSitOnPlayer(): Boolean {
        return pokemon.form.shoulderMountable
    }

    override fun interactMob(player: PlayerEntity, hand: Hand) : ActionResult {
        // TODO: Move to proper pokemon interaction menu
        if (player.isSneaking && hand == Hand.MAIN_HAND) {
            if (isReadyToSitOnPlayer && player is ServerPlayerEntity && !isBusy) {
                this.tryMountingShoulder(player)
            }
        }
        return super.interactMob(player, hand)
    }

    override fun getDimensions(pose: EntityPose): EntityDimensions {
        val scale = pokemon.form.baseScale * pokemon.scaleModifier
        return pokemon.form.hitbox.scaled(scale)
    }

    override fun saveAdditionalSpawnData(buffer: PacketByteBuf) {
        buffer.writeFloat(pokemon.scaleModifier)
        buffer.writeShort(pokemon.species.nationalPokedexNumber)
        buffer.writeString(pokemon.form.name)
        buffer.writeInt(phasingTargetId.get())
        buffer.writeByte(beamModeEmitter.get().toInt())
        buffer.writeBoolean(pokemon.shiny)
    }

    override fun loadAdditionalSpawnData(buffer: PacketByteBuf) {
        if (this.world.isClient) {
            pokemon.scaleModifier = buffer.readFloat()
            // TODO exception handling
            pokemon.species = PokemonSpecies.getByPokedexNumber(buffer.readUnsignedShort())!!
            // TODO exception handling
            pokemon.form = pokemon.species.forms.find { form -> form.name == buffer.readString() }!!
            phasingTargetId.set(buffer.readInt())
            beamModeEmitter.set(buffer.readByte())
            shiny.set(buffer.readBoolean())
        }
    }

    override fun shouldSave(): Boolean {
        return false
    }

    override fun checkDespawn() {
        if (pokemon.getOwnerUUID() == null && despawner.shouldDespawn(this)) {
            discard()
        }
    }

    fun setBehaviourFlag(flag: PokemonBehaviourFlag, on: Boolean) {
        behaviourFlags.set(setBitForByte(behaviourFlags.get(), flag.bit, on))
    }

    fun getBehaviourFlag(flag: PokemonBehaviourFlag): Boolean = getBitForByte(behaviourFlags.get(), flag.bit)

    fun canBattle(player: PlayerEntity): Boolean {
        if (isBusy) {
            return false
        }

        if (ownerUuid == player.uuid) {
            return false
        }

        return true
    }

    private fun tryMountingShoulder(player: ServerPlayerEntity) {
        if (this.pokemon.belongsTo(player) && this.hasRoomToMount(player)) {
            CobbledEvents.SHOULDER_MOUNT.postThen(ShoulderMountEvent(player, pokemon, isLeft = player.shoulderEntityLeft.isEmpty)) {
                val dirToPlayer = player.eyePos.subtract(pos).multiply(1.0, 0.0, 1.0).normalize()
                velocity = dirToPlayer.multiply(0.8).add(0.0, 0.5, 0.0)
                val lock = Any()
                busyLocks.add(lock)
                afterOnMain(seconds = 0.5F) {
                    busyLocks.remove(lock)
                    if (!isBusy && isAlive) {
                        val isLeft = player.shoulderEntityLeft.isEmpty
                        if (!isLeft || player.shoulderEntityRight.isEmpty) {
                            pokemon.state = ShoulderedState(player.uuid, isLeft, pokemon.uuid)
                            this.mountOnto(player)
                            this.pokemon.form.shoulderEffects.forEach { it.applyEffect(this.pokemon, player, isLeft) }
                        }
                    }
                }
            }
        }
    }

    // Copy and paste of how vanilla checks it, unfortunately no util method you can only add then wait for the result
    private fun hasRoomToMount(player: PlayerEntity): Boolean {
        return (player.shoulderEntityLeft.isEmpty || player.shoulderEntityRight.isEmpty)
                && !player.hasVehicle()
                && player.isOnGround
                && !player.isTouchingWater
                && !player.inPowderSnow
    }

}