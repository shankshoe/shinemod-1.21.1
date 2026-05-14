package com.shinemod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.shinemod.sound.ShineSound;
import com.shinemod.item.ShineItem;
import com.shinemod.network.InputOrderPacket;
import com.shinemod.network.ShineServerNetworking;
import com.shinemod.network.ShineStatePacket;
import com.shinemod.state.ShineActiveManager;
import com.shinemod.state.ShineStateManager;
import com.shinemod.enums.ShineStateEnum;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Shinemod implements ModInitializer {
	public static final String MOD_ID = "shinemod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// ---- ITEM ----
	public static final Item SHINE_ITEM = new ShineItem(
			new Item.Settings().maxCount(1)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("The year is 20XX");

		registerItems();
		registerDeathReset();
		registerPackets();
		ShineSound.register();
		ShineServerNetworking.register();
		ShineActiveManager.init();
		setjoin();
	}

	private void registerItems() {
		Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "shine_item"), SHINE_ITEM);

		// optional: add to creative inventory
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
			entries.add(SHINE_ITEM);
		});
	}

	private void registerDeathReset() {
		ServerPlayerEvents.AFTER_RESPAWN.register(
				(oldPlayer, newPlayer, alive) -> {

			if (alive) return;

			ShineStateManager.reset(newPlayer);

			LOGGER.info("Reset Shine state for player: " + newPlayer.getName().getString() + " -> SHINE_INCAPABLE");
		});
	}

	private void registerPackets() {
		PayloadTypeRegistry.playC2S().register(InputOrderPacket.ID, InputOrderPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ShineStatePacket.ID, ShineStatePacket.CODEC);
	}

	private void setjoin() {
		ServerPlayConnectionEvents.JOIN.register(
			(handler, sender, server) -> {

				ServerPlayerEntity joining = handler.getPlayer();

				//LOGGER.info("Setting state for joining player: {}", joining.getName().getString());

				// initialize state
				ShineStateManager.reset(joining);

				// sync next tick
				server.execute(() -> {
					ShineServerNetworking.syncShineState(joining, ShineStateEnum.SHINE_INCAPABLE);
				});
			}
		);
	}
}