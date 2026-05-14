package com.shinemod.state;

import com.shinemod.enums.ShineStateEnum;
import com.shinemod.Shinemod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class ShineActiveManager {

    private static class ActiveShine {
        int ticksLeft;
        boolean attackPhase; // true = damaging + reflecting, false = reflect only

        ActiveShine(int duration) {
            this.ticksLeft = duration;
            this.attackPhase = true;
        }
    }

    private static final HashMap<UUID, ActiveShine> ACTIVE = new HashMap<>();

    // call this once in your mod init
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tickPlayer(player);
            }
        });
    }

    public static void startShine(ServerPlayerEntity player, int durationTicks) {
        ACTIVE.put(player.getUuid(), new ActiveShine(durationTicks));
        ShineStateManager.setState(player, ShineStateEnum.SHINE_ATTACK_REFLECT);
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        ActiveShine shine = ACTIVE.get(uuid);

        if (shine == null) return;

        if (player.isDead() || !player.isAlive()) {
            ACTIVE.remove(uuid);
            return;
        }

        CancelIfnotHeld(player);
        World world = player.getWorld();

        // === HITBOX SIZE ===
        double scale = 2.0;

        Vec3d pos = player.getPos();

        Box hitbox = new Box(
                pos.x - scale,
                pos.y - 0.5,
                pos.z - scale,
                pos.x + scale,
                pos.y + 1.5,
                pos.z + scale
        );

        // === PHASE 1: DAMAGE + REFLECT ===
        if (shine.attackPhase) {

            // 1. INVULNERABILITY
            player.setInvulnerable(true);

            // 2. AOE DAMAGE + PROJECTILE REFLECT
            for (Entity entity : world.getOtherEntities(player, hitbox)) {

                // DAMAGE ENTITIES
                if (entity instanceof LivingEntity living) {
                    living.damage(world.getDamageSources().playerAttack(player), 8.0f);
                }

                // REFLECT PROJECTILES
                if (entity instanceof ProjectileEntity projectile) {
                    Vec3d reflectDir = entity.getPos().subtract(pos).normalize();

                    projectile.setVelocity(reflectDir.x, reflectDir.y + 0.2, reflectDir.z, 2.5f, 0.0f);
                    projectile.setOwner(player);
                }
            }

            shine.ticksLeft--;

            if (shine.ticksLeft <= 0) {
                shine.attackPhase = false;
                shine.ticksLeft = 40; // optional: reflect-only duration
                ShineStateManager.setState(player, ShineStateEnum.SHINE_REFLECT_ONLY);
            }
        }

        // === PHASE 2: REFLECT ONLY ===
        else {
            for (Entity entity : world.getOtherEntities(player, hitbox)) {
                if (entity instanceof ProjectileEntity projectile) {

                    Vec3d reflectDir = entity.getPos().subtract(pos).normalize();

                    projectile.setVelocity(reflectDir.x, reflectDir.y + 0.2, reflectDir.z, 2.5f, 0.0f);
                    projectile.setOwner(player);
                }
            }
            CancelIfnotHeld(player);
        }
    }


    private static void CancelIfnotHeld(ServerPlayerEntity player) {
    
        if (!ShineStateManager.isHoldingShine(player)){
            CancelShine(player);
        }
    
    }
    private static void CancelShine(ServerPlayerEntity player){
        
        //Shinemod.LOGGER.info("ending shine");
        player.setInvulnerable(false);
        ACTIVE.remove(player.getUuid());
        ShineStateManager.setState(player, ShineStateEnum.SHINE_CAPABLE);
    }
}