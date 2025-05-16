package net.juyoh.scale.network;

import com.simibubi.create.Create;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum Packets implements BasePacketPayload.PacketTypeProvider{
    RESIZER_CONFIG(ResizerConfigSyncPayload.class, ResizerConfigSyncPayload.STREAM_CODEC)
    ;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> Packets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Create.asResource(name)),
                clazz, codec
        );
    }

    @Override
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return  (CustomPacketPayload.Type<T>) this.type.type();
    }
}
