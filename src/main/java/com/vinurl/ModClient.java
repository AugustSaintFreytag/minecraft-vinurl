package com.vinurl;

import com.vinurl.client.SoundDescriptionManager;
import com.vinurl.exe.Executable;
import com.vinurl.gui.ProgressOverlay;
import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.net.ModClientEvents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Downloads FFmpeg, FFprobe and YT-DLP if they do not exist and checks for updates.
		for (Executable executable : Executable.values()) {
			if (!executable.checkForExecutable()) {
				Mod.LOGGER.error("Failed to load executable {}", executable);
			}
		}

		ModInputListener.register();
		ModClientEvents.register();
		ModCommands.register();

		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (!stack.isOf(ModItems.CUSTOM_RECORD) && !stack.isOf(ModItems.CUSTOM_RECORD_REWRITABLE)) {
				return;
			}

			if (stack.isOf(ModItems.CUSTOM_RECORD)) {
				lines.remove(Text.translatable("item.vinurl.custom_record.desc").formatted(Formatting.GRAY));
			}

			if (stack.isOf(ModItems.CUSTOM_RECORD_REWRITABLE)) {
				lines.remove(Text.translatable("item.vinurl.custom_record_rewritable.desc").formatted(Formatting.GRAY));
			}

			if (Mod.CONFIG.addDescriptionToItemTooltip) {
				String fileName = SoundDescriptionManager.hashURL(stack.getOrCreateNbt().getString(CustomMusicDiscItem.DISC_URL_NBT_KEY));

				if (!fileName.isEmpty()) {
					lines.add(Text.literal(SoundDescriptionManager.getDescription(fileName)).formatted(Formatting.GRAY));
				}
			}
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			for (Executable executable : Executable.values()) {
				executable.killAllProcesses();
			}
		});

		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			var client = MinecraftClient.getInstance();
			ProgressOverlay.render(client, drawContext);
		});
	}
}
