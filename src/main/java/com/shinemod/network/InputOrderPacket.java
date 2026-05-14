package com.shinemod.network;

import com.shinemod.enums.InputOrder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record InputOrderPacket(InputOrder order)
        implements CustomPayload {

    public static final Id<InputOrderPacket> ID =
            new Id<>(Identifier.of("shinemod", "input_order"));

    public static final PacketCodec<RegistryByteBuf, InputOrderPacket> CODEC =
            PacketCodec.of(
                    (value, buf) ->
                            buf.writeEnumConstant(value.order()),

                    buf -> new InputOrderPacket(
                            buf.readEnumConstant(InputOrder.class)
                    )
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}