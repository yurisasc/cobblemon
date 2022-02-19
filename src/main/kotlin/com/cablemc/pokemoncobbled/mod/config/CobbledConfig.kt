package com.cablemc.pokemoncobbled.mod.config

import com.cablemc.pokemoncobbled.mod.config.value.BooleanValue
import com.cablemc.pokemoncobbled.mod.config.value.DoubleValue
import com.cablemc.pokemoncobbled.mod.config.value.IntValue
import com.cablemc.pokemoncobbled.mod.config.value.LongValue
import com.cablemc.pokemoncobbled.mod.config.value.StringValue
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object CobbledConfig {

    /** Example usages:
    @Configurable(category="Numbers", name="Example Int", description = "This is an example Int")
    @IntValue(100)
    var testInt : ConfigValue<Int>? = null

    @Configurable(category="Numbers", name="Example Long", description = "This is an example Long")
    @LongValue(100000000000000)
    var testLong : ConfigValue<Long>? = null

    @Configurable(category="Numbers", name="Example Double", description = "This is an example Double")
    @DoubleValue(3.4)
    var testDouble : ConfigValue<Double>? = null

    @Configurable(category="Non Numbers", name="Example String", description = "This is an example String")
    @StringValue("Hello World!")
    var testString : ConfigValue<String>? = null

    @Configurable(category="Non Numbers", name="Example Boolean", description = "This is an example Boolean")
    @BooleanValue(false)
    var testBoolean : ConfigValue<Boolean>? = null
     */

    @Configurable(category = "Showdown", name = "Auto Update", description = "Should the mods showdown get auto updated for you?")
    @BooleanValue(true)
    lateinit var autoUpdateShowdown : ConfigValue<Boolean>

    @Configurable(category = "Spawning", name = "Max Vertical Space", description = "The largest possible vertical diameter for a spawning location. If too low, some tall Pokémon might be unable to spawn.")
    @IntValue(defaultValue = 10, min = 1)
    lateinit var maxVerticalSpace: ConfigValue<Int>

    @Configurable(category = "Spawning", name = "Max Horizontal Space", description = "The largest possible horizontal diameter for a spawning location. If too low, some wide Pokémon might be unable to spawn.")
    @IntValue(defaultValue = 10, min = 1)
    lateinit var maxHorizontalSpace: ConfigValue<Int>

    @Configurable(category = "Spawning", name = "Max Nearby Blocks Range", description = "The longest distance to look for particular blocks as a condition for spawning.")
    @IntValue(defaultValue = 6, min = 1)
    lateinit var maxNearbyBlocksRange: ConfigValue<Int>

    @Configurable(category = "Spawning", name = "World Slice Diameter", description = "The block diameter (horizontal) of spawning slices. Reduce this value for improved performance but harder to spawn large Pokémon.")
    @IntValue(defaultValue = 12, min = 1)
    lateinit var worldSliceDiameter: ConfigValue<Int>

    @Configurable(category = "Spawning", name = "World Slice Height", description = "The block height (vertical) of spawning slices. Reduce this value for improved performance but harder to spawn large Pokémon.")
    @IntValue(defaultValue = 8, min = 1)
    lateinit var worldSliceHeight: ConfigValue<Int>

    @Configurable(category = "Spawning", name = "Player Motion Factor", description = "The multiplier applied to a player's velocity to move where the center of a spawn attempt is.")
    @DoubleValue(defaultValue = 10.0, min = 0.0)
    lateinit var playerMotionFactor: ConfigValue<Double>

    @Configurable(category = "Spawning", name = "Minimum Distance Between Entities", description = "The minimum distance that will be forced between spawns and existing entities. Decreasing this clusters Pokémon together.")
    @DoubleValue(defaultValue = 8.0, min = 0.0)
    lateinit var minimumDistanceBetweenEntities: ConfigValue<Double>

    @Configurable(category = "Spawning", name = "Export Spawns To Config", description = "Whether or not spawn files will be extracted so that they can be edited from the config directory.")
    @BooleanValue(defaultValue = false)
    lateinit var exportSpawnsToConfig: ConfigValue<Boolean>

    var spec: ForgeConfigSpec

    init {
        val memberMap = mutableMapOf<ConfigurableNode, KProperty1<CobbledConfig, ConfigValue<*>>>()

        CobbledConfig::class.memberProperties.forEach {
            // Check for annotations
            if (it.annotations.isEmpty() || it.annotations.size != 2) return@forEach

            // Look for first annotation
            if (it.annotations[0] is Configurable) {
                memberMap[ConfigurableNode(it.annotations[0] as Configurable, it.annotations[1])] = it as KMutableProperty1<CobbledConfig, ConfigValue<*>> // TODO: proper safety checking
            }
        }

        val builder = ForgeConfigSpec.Builder()
        val sortedMap = memberMap.toSortedMap(ConfigurableComparator())
        var currentCategory: String? = null

        sortedMap.forEach { (node, member) ->
            val configurable = node.configurable

            // For first entry
            if (currentCategory == null) {
                builder.push(configurable.category)
                currentCategory = configurable.category
            }

            // Current category has changed, so pop and start the next
            if (currentCategory != configurable.category) {
                builder.pop()
                builder.push(configurable.category)
                currentCategory = configurable.category
            }

            builder.comment(configurable.description)

            when (node.valueNode) {
                is StringValue -> {
                    (member as KMutableProperty1<CobbledConfig, ConfigValue<String>>)
                        .set(this, builder.define(configurable.name, node.valueNode.defaultValue))
                }
                is BooleanValue -> {
                    (member as KMutableProperty1<CobbledConfig, ConfigValue<Boolean>>)
                        .set(this, builder.define(configurable.name, node.valueNode.defaultValue))
                }
                is IntValue -> {
                    (member as KMutableProperty1<CobbledConfig, ConfigValue<Int>>)
                        .set(this, builder.defineInRange(configurable.name, node.valueNode.defaultValue, node.valueNode.min, node.valueNode.max))
                }
                is LongValue -> {
                    (member as KMutableProperty1<CobbledConfig, ConfigValue<Long>>)
                        .set(this, builder.defineInRange(configurable.name, node.valueNode.defaultValue, node.valueNode.min, node.valueNode.max))
                }
                is DoubleValue -> {
                    (member as KMutableProperty1<CobbledConfig, ConfigValue<Double>>)
                        .set(this, builder.defineInRange(configurable.name, node.valueNode.defaultValue, node.valueNode.min, node.valueNode.max))
                }
            }
        }

        spec = builder.build()
    }

}