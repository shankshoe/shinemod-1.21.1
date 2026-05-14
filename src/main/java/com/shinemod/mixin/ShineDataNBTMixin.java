package com.shinemod.mixin;

import com.shinemod.state.ShineStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ShineDataNBTMixin {

    @Inject(
        method = "writeCustomDataToNbt",
        at = @At("TAIL")
    )
    private void saveShineData(NbtCompound nbt, CallbackInfo ci) {
        ShineStateManager.saveToNbt(
                (ServerPlayerEntity)(Object)this,
                nbt
        );
    }

    @Inject(
        method = "readCustomDataFromNbt",
        at = @At("TAIL")
    )
    private void loadShineData(NbtCompound nbt, CallbackInfo ci) {
        ShineStateManager.loadFromNbt(
                (ServerPlayerEntity)(Object)this,
                nbt
        );
    }
}