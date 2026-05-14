package com.shinemod.client.effects;

import com.shinemod.enums.ShineStateEnum;
import com.shinemod.sound.ShineSound;
import com.shinemod.state.ShineStateManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ShineEffectsClient {

    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            ClientPlayerEntity player = client.player;

            if (player == null) return;

            ShineStateEnum current = ShineStateManager.getCurrentState(player.getUuid());
            ShineStateEnum previous = ShineStateManager.getPreviousState(player.getUuid());

            // entered shine this tick
            if (current == ShineStateEnum.SHINE_ATTACK_REFLECT && current != previous) {
                player.playSound(ShineSound.SHINE, 1.0f, 1.0f);
                return;
            }
        });
    }
}