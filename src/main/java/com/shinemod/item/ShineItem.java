package com.shinemod.item;

import com.shinemod.enums.ShineStateEnum;
import com.shinemod.network.ShineServerNetworking;
import com.shinemod.state.ShineStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ShineItem extends Item {

    public ShineItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity player) {


            ShineStateEnum newState = ShineStateEnum.SHINE_CAPABLE;

            // set server state
            ShineStateManager.setState(player, newState);

            // sync to client
            ShineServerNetworking.syncShineState(player, newState);

            // Totem sound
            player.playSound(SoundEvents.ITEM_TOTEM_USE, 1.0F, 1.0F);

            // ---- CUSTOM TOTEM POP ITEM ----
            ItemStack original = stack.copy();

            // temporarily set active hand item
            player.setStackInHand(hand, new ItemStack(this));

            // send animation packet
            world.sendEntityStatus(player, (byte) 35);

            // restore original stack
            player.setStackInHand(hand, original);
            // -------------------------------

            // consume item
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}