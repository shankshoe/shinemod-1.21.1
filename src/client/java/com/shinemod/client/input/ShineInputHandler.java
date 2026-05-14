package com.shinemod.client.input;

import com.shinemod.Shinemod;
import com.shinemod.enums.InputOrder;
import com.shinemod.client.network.ShineClientNetworking;
import com.shinemod.enums.ShineStateEnum;
import com.shinemod.state.ShineStateManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;

public class ShineInputHandler {


    public static void register() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null) return;

            ClientPlayerEntity player = client.player;

            if (ShineStateManager.getCurrentState(player.getUuid()) == ShineStateEnum.SHINE_INCAPABLE) return;

            boolean sneaking = client.options.sneakKey.isPressed();
            boolean shinePressed = ShineKeybind.SHINE_KEY_1.isPressed();
            
            if(sneaking) {ShineClientNetworking.sendInput(InputOrder.SNEAK);}
            if(shinePressed) {ShineClientNetworking.sendInput(InputOrder.SHINEKEYPRESS);}
            if(!sneaking && !shinePressed) {ShineClientNetworking.sendInput(InputOrder.START);}
            //Shinemod.LOGGER.info("should be sending someting");
        });
    }
}