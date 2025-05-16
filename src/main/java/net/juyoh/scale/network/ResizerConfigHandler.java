package net.juyoh.scale.network;

import net.juyoh.scale.Upsizing;
import net.juyoh.scale.item.component.ModItemComponents;
import net.juyoh.scale.item.component.ResizerAmountComponent;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class ResizerConfigHandler implements IPayloadHandler<ResizerConfigSyncPayload> {
    @Override
    public void handle(ResizerConfigSyncPayload resizerConfigSyncPayload, IPayloadContext iPayloadContext) {
        iPayloadContext.player().getItemInHand(resizerConfigSyncPayload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
                .set(ModItemComponents.RESIZER_COMPONENT, new ResizerAmountComponent(resizerConfigSyncPayload.scaleAmount()));
        Upsizing.LOGGER.info("recived packet scale: " + resizerConfigSyncPayload.scaleAmount() + " in main hand: " + resizerConfigSyncPayload.mainHand());
    }
}
