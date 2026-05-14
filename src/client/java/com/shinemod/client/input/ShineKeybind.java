package com.shinemod.client.input;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ShineKeybind {

    public static KeyBinding SHINE_KEY_1;

    public static void register() {

        SHINE_KEY_1 = new KeyBinding(
                "key.shinemod.shinekey1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.shinemod"
        );

        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(SHINE_KEY_1);
    }
}