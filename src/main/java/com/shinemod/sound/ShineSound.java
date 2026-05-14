package com.shinemod.sound;

import com.shinemod.Shinemod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ShineSound {

    public static final Identifier SHINE_ID = Identifier.of(Shinemod.MOD_ID, "shine");

    public static final SoundEvent SHINE = SoundEvent.of(SHINE_ID);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, SHINE_ID, SHINE);
    }
}