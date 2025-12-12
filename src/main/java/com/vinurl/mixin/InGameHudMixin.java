package com.vinurl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(method = "setRecordPlayingOverlay", at = @At("HEAD"), cancellable = true)
	private void disableRecordOverlay(Text description, CallbackInfo callbackInfo) {
		if (description.equals(Text.translatable("item.vinurl.custom_record.desc"))) {
			callbackInfo.cancel();
		}
	}
}
