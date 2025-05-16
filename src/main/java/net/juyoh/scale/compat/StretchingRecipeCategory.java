package net.juyoh.scale.compat;

import com.simibubi.create.compat.jei.category.animations.AnimatedDeployer;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.juyoh.scale.Upsizing;
import net.juyoh.scale.recipe.StretchingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class StretchingRecipeCategory implements IRecipeCategory<StretchingRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Upsizing.MODID, "stretching");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Upsizing.MODID,
            "textures/gui/stretching_gui.png");

    public static final RecipeType<StretchingRecipe> STRETCHING_RECIPE_TYPE =
            new RecipeType<>(UID, StretchingRecipe.class);

    private final IDrawableBuilder background;
    private final IDrawable icon;
    private AnimatedPiston animatedPiston = new AnimatedPiston();
    private AnimatedDeployer animatedDeployer = new AnimatedDeployer();

    final int textureWidth = 194;
    final int textureHeight = 54;

    //final int textureWidth = 231;
    //final int textureHeight = 218;


    // IT'S THE IMAGE FILE THAT'S BREAKING THINGS!!!!!

    public StretchingRecipeCategory(IGuiHelper helper) {
        this.background = helper.drawableBuilder(TEXTURE,0 ,0, textureWidth, textureHeight).setTextureSize(textureWidth, textureHeight);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.STICKY_PISTON));
    }

    @Override
    public int getWidth() {
        return textureWidth;
    }

    @Override
    public int getHeight() {
        return textureHeight;
    }

    @Override
    public RecipeType<StretchingRecipe> getRecipeType() {
        return STRETCHING_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("misc.upsizing.stretching");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background.build();
    }

    @Override
    public void draw(StretchingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);

        animatedPiston.draw(guiGraphics, 42, 42);
        //animatedDeployer.draw(guiGraphics, getBackground().getWidth() / 2 - 13, 22);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StretchingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 69, 23).addIngredients(recipe.getIngredients().get(0));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 159, 21).addItemStack(recipe.getResultItem(null));
    }
}
