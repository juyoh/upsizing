package net.juyoh.scale.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.juyoh.scale.Upsizing;
import net.juyoh.scale.recipe.ModRecipes;
import net.juyoh.scale.recipe.StretchingRecipe;
import net.juyoh.scale.recipe.StretchingRecipeInput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Mixin(PistonBaseBlock.class)
public class PistonPullMixin {
    @Shadow @Final private boolean isSticky;

    @Inject(method = "triggerEvent", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;onPistonMovePost(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)V"))
    public void render(BlockState state, Level level, BlockPos pos, int id, int param, CallbackInfoReturnable<Boolean> cir, @Local(name = "direction") Direction direction) {
        if (id != 0 && this.isSticky && !level.isClientSide) {
            BlockPos pullPos = pos.offset(direction.getStepX() * 2, direction.getStepY() * 2, direction.getStepZ() * 2);

            List<ItemEntity> items = level.getEntities(EntityTypeTest.forClass(ItemEntity.class),
                    AABB.encapsulatingFullBlocks(pullPos, pullPos), (itemEntity -> true));

            for (ItemEntity item : items) {
                Optional<RecipeHolder<StretchingRecipe>> recipeHolder = level.getServer().getRecipeManager().getRecipeFor(
                        ModRecipes.STRETCHING_TYPE.get(), new StretchingRecipeInput(item.getItem()), level);

                recipeHolder.ifPresent(stretchingRecipeRecipeHolder ->
                        Upsizing.stretchItem(item, stretchingRecipeRecipeHolder.value().output().getItem()));
            }
        }
    }

}
