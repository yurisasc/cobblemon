package com.cobblemon.mod.common.block.entity

//Using a similar approach to ComputerCraft, implement this client side
interface BlockEntityRenderState : AutoCloseable {
    var needsRebuild: Boolean
}