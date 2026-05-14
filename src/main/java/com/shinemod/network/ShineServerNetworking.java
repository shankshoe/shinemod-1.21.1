package com.shinemod.network;

import com.shinemod.Shinemod;
import com.shinemod.enums.ShineStateEnum;
import com.shinemod.state.ShineStateManager;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ShineServerNetworking {

    public static void register() {

        ServerPlayNetworking.registerGlobalReceiver(
                InputOrderPacket.ID,
                (payload, context) -> {

                    var player = context.player();

                    context.server().execute(() -> {
                        ShineStateManager.handleInput(player, payload.order());
                    });
                }
        );
    }

    public static void syncShineState(ServerPlayerEntity player, ShineStateEnum state) {

        ShineStatePacket packet = new ShineStatePacket(player.getUuid(), state);

        // send to EVERY tracking player + self
        for (ServerPlayerEntity target : PlayerLookup.world(player.getServerWorld())) {
            ServerPlayNetworking.send(target, packet);
        }
    }
}