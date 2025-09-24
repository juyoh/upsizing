package net.juyoh.scale.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.TooltipArea;
import net.createmod.catnip.platform.CatnipServices;
import net.juyoh.scale.Upsizing;
import net.juyoh.scale.item.ResizerItem;
import net.juyoh.scale.item.component.ModItemComponents;
import net.juyoh.scale.item.component.ResizerAmountComponent;
import net.juyoh.scale.network.ResizerConfigSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;

public class ResizerScreen extends Screen {
    ResizerItem item;
    ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Upsizing.MODID, "textures/gui/resizer.png");

    int textureWidth = 182;
    int textureHeight = 103;

    InteractionHand hand;

    float resizeAmount;

    public ResizerScreen(ResizerItem item, InteractionHand usedHand) {
        super(Component.literal("Resizer"));
        this.item = item;
        this.hand = usedHand;
    }
    public ResizerScreen(ResizerItem item, InteractionHand usedHand, float resizeAmount) {
        super(Component.literal("Resizer"));
        this.item = item;
        this.hand = usedHand;
        this.resizeAmount = resizeAmount;
    }

    @Override
    protected void init() {
        super.init();

        int x = (width - textureWidth) / 2;
        int y = (height - textureHeight) / 2;

        Upsizing.LOGGER.info(x + ", " + y);

        TooltipArea sizeTooltip = new TooltipArea(x - 2, y + 32, 32, 32)
                .withTooltip(List.of(Component.translatable("message.upsizing.scale")));

        ScrollInput scrollInput = new ScrollInput(x + 32, y + 48, 110, 21)
                .withRange(0, 128)
                .calling(integer -> {
                    resizeAmount = integer;
                    Minecraft.getInstance()
                            .getSoundManager()
                            .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(),
                                    1.5f + 0.1f * (resizeAmount) / 128));
                });
        IconButton confirm = new IconButton(x + textureWidth - 33, y + textureHeight - 24, AllIcons.I_CONFIRM)
                .withCallback(this::onClose);


        addRenderableWidget(scrollInput);
        addRenderableWidget(confirm);
        addRenderableWidget(sizeTooltip);
    }

    @Override
    protected void renderMenuBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, int width, int height) {
        int x = (width - textureWidth) / 2;
        int y = (height - textureHeight) / 2;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, x, y, 0, 0, textureWidth, textureHeight);

        super.renderMenuBackground(guiGraphics, mouseX, mouseY, width, height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = (width - textureWidth) / 2;
        int y = (height - textureHeight) / 2;

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawInputBox(guiGraphics, x + 32, y + 48);

        Component scaleText = Component.translatable("message.upsizing.scale");

        drawItem(guiGraphics, x, y);
        guiGraphics.drawString(this.font, this.title, x + textureWidth / 2 - font.width(title) / 2, y + 4, 4210752, false);
        guiGraphics.drawString(this.font, scaleText.getString(), x + 8, y + 36, 16777215);
        drawPlayerScale(guiGraphics, x, y);
        drawScaleOrb(guiGraphics, x, y);
        drawButtons(guiGraphics, mouseX, mouseY, x, y);
    }

    private void drawButtons(GuiGraphics guiGraphics, int mouseX, int mouseY, int windowX, int windowY) {
        int upX = windowX + 135;
        int upY = windowY + 55;
        int downX = windowX + 135;
        int downY = windowY + 58;
        int buttonWidth = 8;
        int buttonHeight = 4;
        if (mouseX >= upX && mouseX <= upX + buttonWidth && mouseY >= upY && mouseY <= upY + buttonHeight) {
            guiGraphics.blit(TEXTURE, upX, upY, 111, 103, buttonWidth, buttonHeight);
        }
        if (mouseX >= downX && mouseX <= downX + buttonWidth && mouseY >= downY && mouseY <= downY + buttonHeight) {
            guiGraphics.blit(TEXTURE, downX, downY - 1, 111, 107, buttonWidth, buttonHeight);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int windowX = (width - textureWidth) / 2;
        int windowY = (height - textureHeight) / 2;
        int upX = windowX + 135;
        int upY = windowY + 55;
        int downX = windowX + 135;
        int downY = windowY + 59;
        int buttonWidth = 8;
        int buttonHeight = 4;
        if (mouseX >= upX && mouseX <= upX + buttonWidth && mouseY >= upY && mouseY <= upY + buttonHeight) {
            if (resizeAmount < 127f) {
                resizeAmount = Upsizing.roundToOneDecimalPlace(resizeAmount + 0.0999f);
            }
        }
        if (mouseX >= downX && mouseX <= downX + buttonWidth && mouseY >= downY && mouseY <= downY + buttonHeight) {
            if (resizeAmount > 0.1f) {
                resizeAmount = Upsizing.roundToOneDecimalPlace(resizeAmount - 0.10001f); //shush, don't tell anyone :P
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void drawItem(GuiGraphics guiGraphics, int windowX, int windowY) {
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(windowX + textureWidth + 16, windowY + 64, 0);

        guiGraphics.pose().scale(4, 4, 4);

        guiGraphics.renderFakeItem(new ItemStack(item), 0, 0);

        guiGraphics.pose().popPose();
    }
    public void drawInputBox(GuiGraphics guiGraphics, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, x, y, 0, 103, 110, 21);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(String.valueOf(resizeAmount)), x + 10, y + 7, 4210752, false);
    }
    public void drawScaleOrb(GuiGraphics guiGraphics, int windowX, int windowY) {
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(windowX + 2, windowY + 40, 0);

        guiGraphics.pose().scale(2, 2, 2);

        guiGraphics.renderFakeItem(new ItemStack(Upsizing.ORB_OF_SCALE.get()), 0, 0);

        guiGraphics.pose().popPose();
    }
    public void drawPlayerScale(GuiGraphics guiGraphics, int windowX, int windowY) {
        //guiGraphics.pose().pushPose();
        //guiGraphics.pose().scale(2, 2, 2);
        String scale = String.valueOf(Upsizing.roundToOneDecimalPlace(ScaleTypes.BASE.getScaleData(Minecraft.getInstance().player).getTargetScale()));
        guiGraphics.drawString(this.font, scale,
                windowX + 124 - this.font.width(scale), windowY + 84, 4210752, false);

        //guiGraphics.pose().popPose();
    }


    @Override
    public @Nullable Music getBackgroundMusic() {
        return Musics.CREATIVE;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        CatnipServices.NETWORK.sendToServer(new ResizerConfigSyncPayload(resizeAmount, hand == InteractionHand.MAIN_HAND));
        Minecraft.getInstance().player.getItemInHand(hand).set(ModItemComponents.RESIZER_COMPONENT.get(), new ResizerAmountComponent(resizeAmount));

        super.onClose();
    }
}
