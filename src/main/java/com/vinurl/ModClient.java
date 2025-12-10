package com.vinurl;

import com.vinurl.client.SoundDescriptionManager;
import com.vinurl.exe.Executable;
import com.vinurl.gui.ProgressOverlay;
import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.net.ModClientEvents;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.DyeableItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			if (stack.getItem() instanceof DyeableItem dyeableItem) {
				return scaleColor(dyeableItem.getColor(stack));
			}

			return 0xFFFFFF;
		}, ModItems.DISC_CORE, ModItems.DISC_SIDE, ModItems.DISC_LABEL);

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			var decoration = CustomMusicDiscItem.getDecoration(stack);

			return switch (tintIndex) {
			case 0 -> scaleColor(decoration.sideColor()); // bottom side
			case 1 -> scaleColor(decoration.coreColor()); // core
			case 2 -> scaleColor(decoration.sideColor()); // top side
			case 3 -> scaleColor(decoration.labelColor()); // label
			default -> 0xFFFFFF;
			};
		}, ModItems.CUSTOM_RECORD);

		ModelPredicateProviderRegistry.register(ModItems.CUSTOM_RECORD, Identifier.of(Mod.MOD_ID, "has_label"),
				(stack, world, entity, seed) -> CustomMusicDiscItem.getDecoration(stack).hasLabel() ? 1.0f : 0.0f);

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

	private static int scaleColor(int color) {
		var factor = 1.0f;

		var red = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
		var green = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
		var blue = Math.min(255, (int) ((color & 0xFF) * factor));

		return (red << 16) | (green << 8) | blue;
	}
}
