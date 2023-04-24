package com.cobblemon.mod.common.mixin.accessor;

import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicTracker.class)
public interface MusicTrackerAccessor {
    @Accessor("timeUntilNextSong")
    int timeUntilNextSong();

    @Accessor("timeUntilNextSong")
    void setTimeUntilNextSong(int timeUntilNextSong);
}
