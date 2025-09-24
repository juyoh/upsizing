package net.juyoh.scale.item;

import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.juyoh.scale.Config;
import net.juyoh.scale.Upsizing;
import net.juyoh.scale.item.component.ModItemComponents;
import net.juyoh.scale.screen.ResizerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;
import java.util.function.Consumer;

public class ResizerItem extends Item {
    public ResizerItem(Properties properties) {
        super(properties);
    }
    int lastUseTime = 0;


    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);


        if (level.isClientSide) {
            if (player.isCrouching()) {
                openGUI(this, player, usedHand);

                return InteractionResultHolder.success(stack);
            }  else {
                return InteractionResultHolder.fail(stack);
            }
        } else if (player.isCrouching()) {
            return InteractionResultHolder.fail(stack);
        }
        //If we need operator and don't have it
        if (Config.operatorRequired && !player.hasPermissions(player.getServer().getOperatorUserPermissionLevel())) {
            player.displayClientMessage(Component.translatable("message.upsizing.nopermission").withStyle(ChatFormatting.RED), true);

            return InteractionResultHolder.fail(stack);
        }
        if (BacktankUtil.canAbsorbDamage(player, 256)) {
            float changeAmount = (player.getXRot() < 0 ? stack.get(ModItemComponents.RESIZER_COMPONENT).scaleAmount() : -stack.get(ModItemComponents.RESIZER_COMPONENT).scaleAmount());

            ScaleData data = ScaleTypes.BASE.getScaleData(player);
            
            float currentScale = data.getTargetScale();

            if (currentScale + changeAmount < 512) {
                if (currentScale + changeAmount > 0) {
                    BacktankUtil.consumeAir(player, stack, ((int) Math.ceil(Math.abs(changeAmount))));

                    data.setTargetScale(currentScale + changeAmount);

                    lastUseTime = level.getServer().getTickCount();
                } else {
                    player.displayClientMessage(Component.translatable("message.upsizing.toosmall").withStyle(ChatFormatting.RED), true);
                }
                return InteractionResultHolder.success(stack);
            } else {
                player.displayClientMessage(Component.translatable("message.upsizing.toobig").withStyle(ChatFormatting.RED), true);
            }
        } else {
            player.displayClientMessage(Component.translatable("message.upsizing.nopressure").withStyle(ChatFormatting.RED), true);
        }
        return InteractionResultHolder.pass(stack);
    }
    @OnlyIn(Dist.CLIENT)
    private void openGUI(ResizerItem item, Player player, InteractionHand usedHand) {
        if (player instanceof LocalPlayer && player == Minecraft.getInstance().player) {
            ItemStack stack = player.getItemInHand(usedHand);

            if (stack.has(ModItemComponents.RESIZER_COMPONENT.get())) {
                Minecraft.getInstance().setScreen(new ResizerScreen(item, usedHand, stack.get(ModItemComponents.RESIZER_COMPONENT.get()).scaleAmount()));
            } else {
                Minecraft.getInstance().setScreen(new ResizerScreen(item, usedHand));
            }

        }
    }


    @Override
    public int getBarWidth(ItemStack stack) {
        return BacktankUtil.getBarWidth(stack, 256);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return BacktankUtil.isBarVisible(stack, 256);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BacktankUtil.getBarColor(stack, 256);
    }

    @Override
    public @NotNull Component getDescription() {
        List<Component> lines = TooltipHelper.cutStringTextComponent(Component.translatable("create.tooltip.holdForDescription").toString(),
                Upsizing.greyPalette.primary(), Upsizing.greyPalette.highlight(), 1);
        if (Screen.hasShiftDown()) {
            lines.addAll(TooltipHelper.cutStringTextComponent(Component.translatable("message.upsizing.resizer.tooltip.shift").toString(),
                    Upsizing.goldPalette.primary(), Upsizing.goldPalette.highlight(), 2));
        }
        return Upsizing.joinComponents(lines);
    }
    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new ResizerRenderer()));
    }
}
