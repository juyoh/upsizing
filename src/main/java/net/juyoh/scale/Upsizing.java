package net.juyoh.scale;

import net.createmod.catnip.lang.FontHelper;
import net.juyoh.scale.item.ResizerItem;
import net.juyoh.scale.item.component.ModItemComponents;
import net.juyoh.scale.network.ResizerConfigHandler;
import net.juyoh.scale.network.ResizerConfigSyncPayload;
import net.juyoh.scale.recipe.ModRecipes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Upsizing.MODID)
public class Upsizing
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "upsizing";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Items which will all be registered under the "upsizing" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "upsizing" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<Item> RESIZER = ITEMS.registerItem("resizer", ResizerItem::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    public static final DeferredItem<Item> ORB_OF_SCALE = ITEMS.registerSimpleItem("orb_of_scale", new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

    public static FontHelper.Palette goldPalette = FontHelper.Palette.STANDARD_CREATE;
    public static FontHelper.Palette greyPalette = FontHelper.Palette.GRAY_AND_WHITE;

    // Creates a creative tab with the id "upsizing:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> UPSIZING_TAB = CREATIVE_MODE_TABS.register("upsizing", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.upsizing")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RESIZER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(RESIZER.get());
                output.accept(ORB_OF_SCALE.get());// Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Upsizing(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        ModRecipes.register(modEventBus);
        ModItemComponents.register(modEventBus);


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Upsizing) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


    }
    public static Component joinComponents(List<Component> components) {
        MutableComponent component = MutableComponent.create(new PlainTextContents.LiteralContents(""));

        for (Component component1 : components) {
            component.append(component1);
        }
        return component;
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
    public static class ServerModEvents {
        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(
                    ResizerConfigSyncPayload.TYPE,
                    ResizerConfigSyncPayload.STREAM_CODEC,
                    new ResizerConfigHandler()
            );
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
        }
        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");
            registrar.playToServer(
                    ResizerConfigSyncPayload.TYPE,
                    ResizerConfigSyncPayload.STREAM_CODEC,
                    new ResizerConfigHandler()
            );
        }
    }
    public static void stretchItem(ItemEntity entity, Item output) {
        Level world = entity.level();
        entity.setItem(new ItemStack(output, entity.getItem().getCount()));
        world.playSound(entity, entity.blockPosition(), SoundEvents.SLIME_JUMP_SMALL, SoundSource.MASTER, 1f, 1f);

        RandomSource random = world.random;

        for (int i = 0; i < 12; i++) {
            world.addParticle(ParticleTypes.END_ROD, entity.getX(), entity.getY(), entity.getZ(),
                    random.nextDouble(), random.nextDouble(), random.nextDouble());
        }
    }
    public static float roundToOneDecimalPlace(float in) {
        return (float) (Math.ceil(in * 10) / 10);
    }
}
