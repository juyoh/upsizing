package net.juyoh.scale;

import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Upsizing.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue NEEDS_OPERATOR = BUILDER
            .comment("Whether Operator status is required for using the Resizer")
            .define("operatorRequired", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean operatorRequired;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        operatorRequired = NEEDS_OPERATOR.get();
    }
}
