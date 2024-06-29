/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.util

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.resource.ResourceFactory
import net.minecraft.resources.ResourceLocation

data class ShaderRegistryData(val resourceFactory: ResourceFactory, val shaderName: ResourceLocation, val vertexFormat: VertexFormat)