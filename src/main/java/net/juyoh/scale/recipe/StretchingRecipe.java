package net.juyoh.scale.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record StretchingRecipe(Ingredient input, ItemStack output) implements Recipe<StretchingRecipeInput> {
    @Override
    public boolean matches(StretchingRecipeInput stretchingRecipeInput, Level level) {
        if (level.isClientSide) {
            return false;
        }

        return input.test(stretchingRecipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(StretchingRecipeInput stretchingRecipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.STRETCHING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.STRETCHING_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    public static class Serializer implements RecipeSerializer<StretchingRecipe> {
        public static final MapCodec<StretchingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(StretchingRecipe::input),
                ItemStack.CODEC.fieldOf("result").forGetter(StretchingRecipe::output)
        ).apply(inst, StretchingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StretchingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, StretchingRecipe::input,
                        ItemStack.STREAM_CODEC, StretchingRecipe::output,
                        StretchingRecipe::new);

        @Override
        public MapCodec<StretchingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, StretchingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
