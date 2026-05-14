package com.shinemod.client.network;

import com.shinemod.enums.InputOrder;
import com.shinemod.network.InputOrderPacket;
import com.shinemod.network.ShineStatePacket;
import com.shinemod.state.ShineStateManager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ShineClientNetworking {

    public static void sendInput(InputOrder order) {
        ClientPlayNetworking.send(new InputOrderPacket(order));
    }
}