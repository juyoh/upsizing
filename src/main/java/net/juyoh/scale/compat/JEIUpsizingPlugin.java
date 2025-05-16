package net.juyoh.scale.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.juyoh.scale.Upsizing;
import net.juyoh.scale.recipe.ModRecipes;
import net.juyoh.scale.recipe.StretchingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIUpsizingPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Upsizing.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new StretchingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<StretchingRecipe> stretchingRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.STRETCHING_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(StretchingRecipeCategory.STRETCHING_RECIPE_TYPE, stretchingRecipes);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Items.STICKY_PISTON, StretchingRecipeCategory.STRETCHING_RECIPE_TYPE);
    }
}
