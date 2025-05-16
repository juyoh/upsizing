package net.juyoh.scale.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.juyoh.scale.Upsizing;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItemComponents {
    // Basic codec
    public static final Codec<ResizerAmountComponent> EXPLOSIVE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("amount").forGetter(ResizerAmountComponent::scaleAmount)
            ).apply(instance, ResizerAmountComponent::new)
    );

    public static final StreamCodec<ByteBuf, ResizerAmountComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ResizerAmountComponent::scaleAmount,
            ResizerAmountComponent::new
    );


    // In another class
// The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Upsizing.MODID);


    // No data will be synced across the network
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResizerAmountComponent>> RESIZER_COMPONENT = REGISTRAR.registerComponentType(
            "resizer_amount",
            builder -> builder
                    .persistent(EXPLOSIVE_CODEC)
                    // Note we use a unit stream codec here
                    .networkSynchronized(STREAM_CODEC)
    );
    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
