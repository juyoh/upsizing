package net.juyoh.scale.network;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.juyoh.scale.Upsizing;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ResizerConfigSyncPayload(float scaleAmount, boolean mainHand) implements ServerboundPacketPayload {
    public static final CustomPacketPayload.Type<ResizerConfigSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Upsizing.MODID, "resizer_sync"));
    public static final StreamCodec<ByteBuf, ResizerConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            ResizerConfigSyncPayload::scaleAmount,
            ByteBufCodecs.BOOL,
            ResizerConfigSyncPayload::mainHand,
            ResizerConfigSyncPayload::new
    );


    @Override
    public PacketTypeProvider getTypeProvider() {
        return Packets.RESIZER_CONFIG;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(ServerPlayer player) {

    }
}
