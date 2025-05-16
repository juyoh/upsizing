package net.juyoh.scale.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;

public class AnimatedPiston extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        stack.translate(xOffset, yOffset, 100);
        stack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        stack.mulPose(Axis.YP.rotationDegrees(45f + 180));
        double cycle = (Math.sin(AnimationTickHolder.getRenderTime()) * -0.5) - 0.5;

        blockElement(Blocks.STICKY_PISTON.defaultBlockState()
                .setValue(BlockStateProperties.EXTENDED, true))

                .scale(24)
                .render(guiGraphics);
        blockElement(Blocks.PISTON_HEAD.defaultBlockState()
                .setValue(BlockStateProperties.PISTON_TYPE, PistonType.STICKY).setValue(BlockStateProperties.FACING, Direction.NORTH))

                .atLocal(0, 0, cycle)
                .scale(24)
                .render(guiGraphics);

        stack.popPose();
    }
}
