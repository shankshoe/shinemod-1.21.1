package com.shinemod.network;

import java.util.UUID;

import com.shinemod.enums.ShineStateEnum;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.util.Identifier;

public record ShineStatePacket(UUID playerUuid, ShineStateEnum state)
        implements CustomPayload {

    public static final Id<ShineStatePacket> ID =
            new Id<>(Identifier.of("shinemod", "shine_state"));

    public static final PacketCodec<RegistryByteBuf, ShineStatePacket> CODEC =
        PacketCodec.of(
                (value, buf) -> {
                    buf.writeUuid(value.playerUuid());
                    buf.writeEnumConstant(value.state());
                },

                buf -> new ShineStatePacket(
                        buf.readUuid(),
                        buf.readEnumConstant(ShineStateEnum.class)
        )
        );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}