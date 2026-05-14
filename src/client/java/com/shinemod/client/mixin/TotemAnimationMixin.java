package com.shinemod.client.mixin;

import com.shinemod.Shinemod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayNetworkHandler.class)
public class TotemAnimationMixin {

    @ModifyArg(
            method = "onEntityStatus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;showFloatingItem(Lnet/minecraft/item/ItemStack;)V"
            ),
            index = 0
    )
    private ItemStack replaceTotem(ItemStack original) {

        if (original.isOf(Items.TOTEM_OF_UNDYING)) {
            return new ItemStack(Shinemod.SHINE_ITEM);
        }

        return original;
    }
}