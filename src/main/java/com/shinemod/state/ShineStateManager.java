package com.shinemod.state;

import com.shinemod.Shinemod;
import com.shinemod.enums.InputOrder;
import com.shinemod.enums.ShineStateEnum;
import com.shinemod.network.ShineServerNetworking;
import com.shinemod.network.ShineStatePacket;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class ShineStateManager {

    private static final HashMap<UUID, ShineStateEnum> CURRENT = new HashMap<>();
    private static final HashMap<UUID, ShineStateEnum> PREVIOUS = new HashMap<>();

    private static final HashMap<UUID, InputOrder> LAST_INPUT = new HashMap<>();

    
    private static final String SHINE_CURRENT_KEY = "ShineCurrentState";
    private static final String SHINE_PREVIOUS_KEY = "ShinePreviousState";

    public static void handleInput(ServerPlayerEntity player, InputOrder input) {

        UUID uuid = player.getUuid();

        InputOrder last = LAST_INPUT.getOrDefault(uuid, InputOrder.START);

        LAST_INPUT.put(uuid, input);

        ShineStateEnum current = getCurrentState(uuid);

        PREVIOUS.put(uuid, current);

        if (last == InputOrder.SNEAK && input == InputOrder.SHINEKEYPRESS && current == ShineStateEnum.SHINE_CAPABLE) {

            //Shinemod.LOGGER.info("VALID SHINE COMBO DETECTED");

            applyShineFunction(player);
        }
    }


    private static void applyShineFunction(ServerPlayerEntity player) {
        ShineActiveManager.startShine(player, 200); 
    }


    /**
     * SERVER SIDE
     * Updates hashmap + syncs packet
     */
    public static void setState(ServerPlayerEntity player, ShineStateEnum state) {

        UUID uuid = player.getUuid();

        ShineStateEnum previous = getCurrentState(uuid);

        if (previous != state){

            
            PREVIOUS.put(uuid, previous);
            CURRENT.put(uuid, state);

            // sync to client
            ShineServerNetworking.syncShineState(player, state);
            
            //Shinemod.LOGGER.info("(SERVER) Shine state updated: " + previous + " -> " + state + "for : " player.getName().getString());
        }
    }


    /**
     * CLIENT SIDE
     * Updates hashmap only
     */
    public static void setState(UUID uuid, ShineStateEnum state) {

        ShineStateEnum previous = getCurrentState(uuid);
        
        if (previous != state) {
            
            PREVIOUS.put(uuid, previous);
            CURRENT.put(uuid, state);
            
            //Shinemod.LOGGER.info("(CLIENT) Shine state updated: " + previous + " -> " + state);
        }

    }

    public static void syncPlayer(ServerPlayerEntity player) {

        ShineStateEnum state = ShineStateManager.getCurrentState(player.getUuid());

        ShineStatePacket packet = new ShineStatePacket(player.getUuid(), state);

        ServerPlayNetworking.send(player, packet);

        //Shinemod.LOGGER.info("Initial sync {} -> {}", player.getName().getString(), state);
    }

    public static ShineStateEnum getCurrentState(UUID uuid) {
        return CURRENT.getOrDefault(uuid, ShineStateEnum.SHINE_INCAPABLE);
    }


    public static ShineStateEnum getPreviousState(UUID uuid) {
        return PREVIOUS.getOrDefault(uuid, ShineStateEnum.SHINE_INCAPABLE);
    }


    public static void reset(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();

        CURRENT.put(uuid, ShineStateEnum.SHINE_INCAPABLE);

        PREVIOUS.put(uuid, ShineStateEnum.SHINE_INCAPABLE);

        LAST_INPUT.put(uuid, InputOrder.INVALID);

        ShineServerNetworking.syncShineState(player, ShineStateEnum.SHINE_INCAPABLE);

        //Shinemod.LOGGER.info("Reset ShineState for " + player.getName().getString());
    }

    public static void saveToNbt(ServerPlayerEntity player, NbtCompound nbt) {
        UUID uuid = player.getUuid();

        ShineStateEnum current = CURRENT.getOrDefault(uuid, ShineStateEnum.SHINE_INCAPABLE);

        ShineStateEnum previous = PREVIOUS.getOrDefault(uuid, ShineStateEnum.SHINE_INCAPABLE);

        nbt.putString(SHINE_CURRENT_KEY, current.name());
        nbt.putString(SHINE_PREVIOUS_KEY, previous.name());
    }
    public static void loadFromNbt(ServerPlayerEntity player, NbtCompound nbt) {
        UUID uuid = player.getUuid();

        ShineStateEnum current = ShineStateEnum.SHINE_INCAPABLE;
        ShineStateEnum previous = ShineStateEnum.SHINE_INCAPABLE;

        if (nbt.contains(SHINE_CURRENT_KEY)) {
            current = ShineStateEnum.valueOf(nbt.getString(SHINE_CURRENT_KEY));
        }

        if (nbt.contains(SHINE_PREVIOUS_KEY)) {
            previous = ShineStateEnum.valueOf(nbt.getString(SHINE_PREVIOUS_KEY));
        }

        CURRENT.put(uuid, current);
        PREVIOUS.put(uuid, previous);
    }

    public static boolean isHoldingShine(ServerPlayerEntity player) {

        UUID uuid = player.getUuid();
        InputOrder last = LAST_INPUT.getOrDefault(uuid, InputOrder.START);

        boolean sneakHeld = player.isSneaking(); 
        boolean shineHeld = (boolean) (last == InputOrder.SHINEKEYPRESS); 

        //Shinemod.LOGGER.info("sneak: " + sneakHeld + ", shine: " + shineHeld);
        return sneakHeld && shineHeld;
    }

}