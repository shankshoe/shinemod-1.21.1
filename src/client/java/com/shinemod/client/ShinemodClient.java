package com.shinemod.client;

import com.shinemod.Shinemod;
import com.shinemod.client.effects.ShineEffectsClient;
import com.shinemod.client.effects.ShineRenderer;
import com.shinemod.client.input.ShineInputHandler;
import com.shinemod.client.input.ShineKeybind;
import com.shinemod.network.ShineStatePacket;
import com.shinemod.state.ShineStateManager;

import com.shinemod.Shinemod;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;

public class ShinemodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ShineKeybind.register();
        ShineInputHandler.register();

        ClientPlayNetworking.registerGlobalReceiver(
            ShineStatePacket.ID,
            (payload, context) -> {

                context.client().execute(() -> {

                    ShineStateManager.setState(payload.playerUuid(), payload.state());

                    //Shinemod.LOGGER.info("Received shine state {} for player {}", payload.state(), payload.playerUuid());
                });
            }
        );
        
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (entityType, renderer, helper, context) -> {

                    if (renderer instanceof PlayerEntityRenderer playerRenderer) {
                        helper.register(new ShineRenderer(playerRenderer));
                    }
                }
        );

        ShineEffectsClient.register();
    }
}