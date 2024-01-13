//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.entity.fishing.PokeRodFishingBobberEntity
import com.cobblemon.mod.common.item.CobblemonItem
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.FishingRodItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Item.Settings
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.stat.Stats
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class PokerodItem(settings: Settings?) : FishingRodItem(settings) {
    /*override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        val i: Int
        if (user.fishHook != null) { // if the bobber is out yet
            if (!world.isClient) {
                i = user.fishHook!!.use(itemStack)
                itemStack.damage(i, user) { p: PlayerEntity -> p.sendToolBreakStatus(hand) }
            }
            world.playSound(null as PlayerEntity?, user.x, user.y, user.z, SoundEvents.ENTITY_FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH)
        } else { // if the bobber is not out yet
            world.playSound(null as PlayerEntity?, user.x, user.y, user.z, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f))
            if (!world.isClient) {
                i = EnchantmentHelper.getLure(itemStack)
                val j = EnchantmentHelper.getLuckOfTheSea(itemStack)
                world.spawnEntity(FishingBobberEntity(user, world, j, i))
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this))
            user.emitGameEvent(GameEvent.ITEM_INTERACT_START)
        }
        return TypedActionResult.success(itemStack, world.isClient())
    }*/

    // todo Round 2
    /*override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)

        // Play the throw sound
        world.playSound(null as PlayerEntity?, user.x, user.y, user.z, SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f))

        // Spawn the FishingBobberEntity directly without checking for an existing one
        if (!world.isClient) {
            val i = EnchantmentHelper.getLure(itemStack)
            val j = EnchantmentHelper.getLuckOfTheSea(itemStack)
            world.spawnEntity(PokeBobberEntity(user, world, j, i))
        }


        // Record the usage of the item
        user.incrementStat(Stats.USED.getOrCreateStat(this))
        user.emitGameEvent(GameEvent.ITEM_INTERACT_START)

        return TypedActionResult.success(itemStack, world.isClient())
    }*/

    // todo Round 3
    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (!world.isClient) {
            val itemStack = player.getStackInHand(hand)

            // Calculate initial position and velocity for PokeBobberEntity
            val currentPose = player.pose
            val eyeHeight = player.getEyeHeight(currentPose)
            val spawnPos = player.pos.add(0.0, eyeHeight.toDouble(), 0.0)
            val direction = player.getRotationVec(1.0f)
            val velocity = direction.multiply(1.5)

            System.out.println("Spawn Position: " + spawnPos);
            System.out.println("Velocity: " + velocity);

            // Create and spawn PokeBobberEntity
            val pokeBobber = PokeRodFishingBobberEntity(player, world, 0, 0)

            pokeBobber.setPos(spawnPos.x, spawnPos.y, spawnPos.z)
            pokeBobber.setVelocity(velocity.x, velocity.y, velocity.z)
            //pokeBobber.use(ItemStack(CobblemonItems.POKEROD))
            world.spawnEntity(pokeBobber)
            //pokeBobber.use(ItemStack(Items.FISHING_ROD))
            pokeBobber.use(itemStack)

        }
        return TypedActionResult.success(player.getStackInHand(hand))
    }

    override fun getEnchantability(): Int {
        return 1
    }
}
