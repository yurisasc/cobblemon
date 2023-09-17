package com.cobblemon.mod.common.mixin.info;

import com.cobblemon.mod.common.CobblemonConstants;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public final class CrashReportMixin {

    @Inject(method = "addStackTrace", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SystemDetails;writeTo(Ljava/lang/StringBuilder;)V"))
    public void cobblemon$printCobblemonDetails(StringBuilder builder, CallbackInfo info) {
        CrashReportSection section = new CrashReportSection("Cobblemon");
        section.add("Version", CobblemonConstants.VERSION_FULL);
        section.add("Is Snapshot", CobblemonConstants.SNAPSHOT);
        section.add("Git Commit", CobblemonConstants.GIT_COMMIT);
        section.add("Build Number", CobblemonConstants.BUILD_NUMBER);
        section.add("Timestamp", CobblemonConstants.TIMESTAMP);

        section.addStackTrace(builder);
        builder.append("\n\n");
    }

}
