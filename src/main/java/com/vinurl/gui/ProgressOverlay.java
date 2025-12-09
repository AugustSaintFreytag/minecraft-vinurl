package com.vinurl.gui;

import java.util.LinkedHashMap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ProgressOverlay {
	// Configuration

	private static final int OFFSET_Y = 102;
	private static final int SPACING_Y = 10;

	private static final int BAR_SIZE = 20;

	private static int batchSize = 0;

	private static final LinkedHashMap<String, ProgressEntry> progressQueue = new LinkedHashMap<>();

	// Render

	public static void render(MinecraftClient client, DrawContext context) {
		if (progressQueue.isEmpty()) {
			return;
		}

		var now = System.currentTimeMillis();
		var firstEntry = progressQueue.entrySet().iterator().next();
		var currentId = firstEntry.getKey();
		var entry = firstEntry.getValue();

		if (entry.shouldRemove(now)) {
			stop(currentId);
			return;
		}

		Text progress = switch (entry.state) {
		case INTERRUPTED -> Text.literal(String.format("%d/%d ", batchSize - (progressQueue.size() - 1), batchSize))
				.append(createProgressText(20, Formatting.RED));
		case TRANSCODING -> {
			int animationStep = (int) ((now - entry.stateChangeTime) / 100) % BAR_SIZE;
			yield Text.literal(String.format("%d/%d ", batchSize - (progressQueue.size() - 1), batchSize))
					.append(createProgressText(animationStep, Formatting.GRAY)).append(createProgressText(1, Formatting.BLUE))
					.append(createProgressText(BAR_SIZE - 1 - animationStep, Formatting.GRAY));
		}
		default -> {
			int progressBars = BAR_SIZE * entry.progress / 100;
			yield Text.literal(String.format("%d/%d ", batchSize - (progressQueue.size() - 1), batchSize))
					.append(createProgressText(progressBars, Formatting.GREEN))
					.append(createProgressText(BAR_SIZE - progressBars, Formatting.GRAY));
		}
		};

		renderText(client, context, Text.literal(entry.state.toString()), OFFSET_Y);
		renderText(client, context, progress, OFFSET_Y - SPACING_Y);
	}

	private static void renderText(MinecraftClient client, DrawContext context, Text text, int offset) {
		var window = client.getWindow();
		var textRenderer = client.textRenderer;

		context.drawTextWithShadow(textRenderer, text, (window.getScaledWidth() - textRenderer.getWidth(text)) / 2,
				window.getScaledHeight() - offset, 0xFFFFFF);
	}

	// Update

	public static void set(String id, int progressPercent) {
		if (progressQueue.put(id, new ProgressEntry(progressPercent)) == null) {
			batchSize++;
		}
	}

	public static void stop(String id) {
		if (progressQueue.remove(id) != null && progressQueue.isEmpty()) {
			batchSize = 0;
		}
	}

	public static void stopFailed(String id) {
		progressQueue.put(id, new ProgressEntry(ProgressEntry.ERROR));
	}

	// Make

	private static Text createProgressText(int count, Formatting formatting) {
		return Text.literal("|".repeat(count)).formatted(formatting);
	}
}