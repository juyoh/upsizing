package net.juyoh.scale.recipe;

import net.juyoh.scale.Upsizing;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Upsizing.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Upsizing.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<StretchingRecipe>> STRETCHING_SERIALIZER =
            SERIALIZERS.register("stretching", StretchingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<StretchingRecipe>> STRETCHING_TYPE =
            TYPES.register("stretching", () -> new RecipeType<StretchingRecipe>() {
                @Override
                public String toString() {
                    return "stretching";
                }
            });


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
