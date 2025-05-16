package net.juyoh.scale.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.juyoh.scale.Upsizing;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class ResizerRenderer extends CustomRenderedItemModelRenderer {
    private static final PartialModel RESIZER = PartialModel.of(ResourceLocation.fromNamespaceAndPath(Upsizing.MODID, "item/resizer"));
    private static final PartialModel ORB_OF_SCALE = PartialModel.of(ResourceLocation.fromNamespaceAndPath(Upsizing.MODID, "item/orb_of_scale"));
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        renderer.render(RESIZER.get(), light);

        ms.pushPose();
        float renderTime = ((AnimationTickHolder.getRenderTime() / 10));
        float rotationY = renderTime * 3.0F;
        float scale = (float) Math.sin(renderTime / 6) / 6f - 1f;


        //poseStack.pushPose();
        ms.scale(scale / 2, scale / 2, scale / 2);

        ms.mulPose(Axis.YP.rotationDegrees(rotationY));

        ms.translate(0, 0.5, 0);

        renderer.render(ORB_OF_SCALE.get(), light);

        ms.popPose();
    }
}
