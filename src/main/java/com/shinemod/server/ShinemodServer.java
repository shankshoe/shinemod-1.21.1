package com.shinemod.server;

import com.shinemod.network.InputOrderPacket;
import com.shinemod.network.ShineServerNetworking;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ShinemodServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ShineServerNetworking.register();
    }
}