package com.cobblemon.mod.common.client.render.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.item.interactive.PokerodItem
import net.minecraft.client.item.ClampedModelPredicateProvider
import net.minecraft.client.item.ModelPredicateProviderRegistry
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

object CobblemonModelPredicateRegistry {

    fun registerPredicates() {
        ModelPredicateProviderRegistry.register(CobblemonItems.AZURE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.CHERISH_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.CITRINE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.DIVE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.DREAM_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.DUSK_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.FAST_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.FRIEND_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.GREAT_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.HEAL_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.HEAVY_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.LEVEL_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.LOVE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.LURE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.LUXURY_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.MASTER_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.MOON_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.NEST_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.NET_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.PARK_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.POKE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.PREMIER_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.QUICK_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.REPEAT_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.ROSEATE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.SAFARI_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.SLATE_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.SPORT_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.TIMER_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.ULTRA_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })

        ModelPredicateProviderRegistry.register(CobblemonItems.VERDANT_ROD, Identifier("cast"), ClampedModelPredicateProvider { stack: ItemStack, world: ClientWorld?, entity: LivingEntity?, seed: Int ->
            if (entity == null) {
                return@ClampedModelPredicateProvider 0.0f
            } else {
                val bl = entity.mainHandStack == stack
                var bl2 = entity.offHandStack == stack
                if (entity.mainHandStack.item is PokerodItem) {
                    bl2 = false
                }

                return@ClampedModelPredicateProvider if ((bl || bl2) && entity is PlayerEntity && (entity.fishHook != null)) 1.0f else 0.0f
            }
        })
    }
}