package com.cobblemon.mod.common.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
@Mixin(StructureStart.class)
public class StructureStartMixin {

//    @Inject(method = "fromNbt", at = @At("HEAD"))
//    private static void onFromNbt(StructureContext context, NbtCompound nbt, long seed, CallbackInfoReturnable<StructureStart> cir) {
//        // mixin code goes here3
//        System.out.println("fromNBT START!");
//        System.out.println(context);
//        System.out.println(nbt);
//        String s = nbt.getString("id");
//        System.out.println(s);
//        NbtList listtag = nbt.getList("Children", NbtElement.COMPOUND_TYPE);
//        System.out.println(listtag);
//        StructurePiecesList piecescontainer = StructurePiecesList.fromNbt(listtag, context);
//        System.out.println(piecescontainer);
//        System.out.println(cir.getReturnValue().getChildren());
//        System.out.println("fromNBT END!");
//    }
}


