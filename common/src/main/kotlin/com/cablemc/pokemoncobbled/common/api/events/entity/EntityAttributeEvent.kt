package com.cablemc.pokemoncobbled.common.api.events.entity

import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.DefaultAttributeContainer

/**
 * Event fired when we're registering entity attributes. This is really just a hack to get
 * attributes working on the Forge side because I'm a dumbass and have no idea how to fix
 * it properly.
 *
 * @author Hiroku
 * @since February 20th, 2022
 */
class EntityAttributeEvent(val entityType: EntityType<*>, val attributeSupplier: DefaultAttributeContainer.Builder)