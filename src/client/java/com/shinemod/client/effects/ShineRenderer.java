package com.shinemod.client.effects;

import net.minecraft.client.MinecraftClient;
import com.shinemod.enums.ShineStateEnum;
import com.shinemod.state.ShineStateManager;
import com.shinemod.Shinemod;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;

public class ShineRenderer extends
        FeatureRenderer<AbstractClientPlayerEntity,
                PlayerEntityModel<AbstractClientPlayerEntity>> {

    private static final Identifier TEXTURE =
            Identifier.of("shinemod", "textures/effect/shine.png");

    public ShineRenderer(PlayerEntityRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity player,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {

        ShineStateEnum current = ShineStateManager.getCurrentState(player.getUuid());
        ShineStateEnum previous = ShineStateManager.getPreviousState(player.getUuid());


        //Shinemod.LOGGER.info("(CLIENT) current state: " + current + ", previous state : " + previous);
        if (current == ShineStateEnum.SHINE_ATTACK_REFLECT || current == ShineStateEnum.SHINE_REFLECT_ONLY) {
                
                matrices.push();

                // position slightly above feet
                matrices.translate(0, -0.1, 0);

                // billboard toward camera
                var camera = MinecraftClient.getInstance().gameRenderer.getCamera();
                matrices.multiply(camera.getRotation());        
                
                float size = 1.5f;

                MatrixStack.Entry entry = matrices.peek();

                VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));

                consumer.vertex(entry, -size, -size, 0)
                        .color(255,255,255,225)
                        .texture(0,1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(light)
                        .normal(entry,0,1,0);

                consumer.vertex(entry, size, -size, 0)
                        .color(255,255,255,225)
                        .texture(1,1)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(light)
                        .normal(entry,0,1,0);

                consumer.vertex(entry, size, size, 0)
                        .color(255,255,255,225)
                        .texture(1,0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(light)
                        .normal(entry,0,1,0);

                consumer.vertex(entry, -size, size, 0)
                        .color(255,255,255,225)
                        .texture(0,0)
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(light)
                        .normal(entry,0,1,0);

                matrices.pop();
        }
        return;
        }
}