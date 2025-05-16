package net.juyoh.scale.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.juyoh.scale.Upsizing;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V", shift = At.Shift.AFTER))
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, BakedModel p_model, CallbackInfo ci) {
        if (itemStack.is(Upsizing.ORB_OF_SCALE) &&
                displayContext != ItemDisplayContext.FIRST_PERSON_LEFT_HAND && displayContext != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            float renderTime = ((AnimationTickHolder.getRenderTime() / 10));
            float rotationY = renderTime * 3.0F;
            float scale = (float) Math.sin(renderTime / 6) / 6f - 1f;

            //poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        }
    }

}
