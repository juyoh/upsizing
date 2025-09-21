package net.juyoh.scale.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.ItemHandlerBeltSegment;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
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

            Block blockPulled = level.getBlockState(pullPos).getBlock();

            if (blockPulled == AllBlocks.BELT.get() && state.getValue(BlockStateProperties.FACING) == Direction.DOWN) {
                BeltBlockEntity belt = BeltHelper.getSegmentBE(level, pullPos);
                BeltBlockEntity controller = belt.getControllerBE();

                controller.getInventory().applyToEachWithin(belt.index + .5f, .55f, (transportedItemStack) -> {
                    Upsizing.LOGGER.info("index: {}", belt.index);
                    ItemStack input = transportedItemStack.stack;
                    Upsizing.LOGGER.info("input: {}", input.toString());

                    Upsizing.LOGGER.info("belt pos: {}", transportedItemStack.beltPosition);

                    Optional<RecipeHolder<StretchingRecipe>> recipeHolder = level.getServer().getRecipeManager().getRecipeFor(
                            ModRecipes.STRETCHING_TYPE.get(), new StretchingRecipeInput(input), level);
                    if (recipeHolder.isPresent() && Math.abs(transportedItemStack.beltPosition % 1) < 0.7f) {
                        ItemStack out = recipeHolder.get().value().output();
                        out.setCount(input.getCount());
                        Upsizing.LOGGER.info("output: {}", out.toString());
                        level.playSound(null, pullPos, SoundEvents.SLIME_JUMP_SMALL, SoundSource.MASTER, 1f, 1f);
                        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pullPos, null);
                        handler.insertItem(0, out, false);
                        belt.notifyUpdate();
                        return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
                    }

                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                });


            } else if (blockPulled == AllBlocks.DEPOT.get() && state.getValue(BlockStateProperties.FACING) == Direction.DOWN) {
                DepotBlockEntity depot = (DepotBlockEntity) level.getBlockEntity(pullPos);

                ItemStack input = depot.getHeldItem();
                Upsizing.LOGGER.info("input: {}", input.toString());
                Optional<RecipeHolder<StretchingRecipe>> recipeHolder = level.getServer().getRecipeManager().getRecipeFor(
                        ModRecipes.STRETCHING_TYPE.get(), new StretchingRecipeInput(input), level);
                if (recipeHolder.isPresent()) {
                    ItemStack out = recipeHolder.get().value().output();
                    out.setCount(input.getCount());
                    Upsizing.LOGGER.info("output: {}", out.toString());
                    level.playSound(null, pullPos, SoundEvents.SLIME_JUMP_SMALL, SoundSource.MASTER, 1f, 1f);
                    depot.setHeldItem(recipeHolder.get().value().output());
                    depot.notifyUpdate();
                }


            }

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
