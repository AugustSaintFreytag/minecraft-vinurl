package com.vinurl.net;

import java.util.List;

import com.vinurl.Mod;
import com.vinurl.ModInputListener;
import com.vinurl.ModNetworking;
import com.vinurl.client.SoundDescriptionManager;
import com.vinurl.client.SoundDownloadManager;
import com.vinurl.client.SoundManager;
import com.vinurl.exe.Executable;
import com.vinurl.gui.URLScreen;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ModClientEvents {

	public static void register() {
		// Client event for playing sounds
		ModNetworking.NETWORK_CHANNEL.registerClientbound(PlaySoundRecord.class, (payload, context) -> {
			var client = context.runtime();
			var player = client.player;
			var url = payload.url();

			if (player == null || url.isEmpty()) {
				return;
			}

			var position = payload.position().toCenterPos();
			var fileName = SoundDescriptionManager.hashURL(url);
			var showOverlay = payload.showOverlay();

			SoundManager.addSound(fileName, position);

			if (Executable.YT_DLP.isProcessRunning(fileName + "/download")) {
				SoundDownloadManager.queueSound(fileName, position);
				return;
			}

			if (SoundDownloadManager.getAudioFile(fileName).exists()) {
				SoundManager.playSound(position);
				return;
			}

			if (Mod.CONFIG.enableDownloads) {
				List<String> whitelist = List.of(Mod.CONFIG.sourceWhitelist.split(","));
				String baseURL = SoundDownloadManager.getBaseURL(url);

				if (whitelist.stream().anyMatch(url::startsWith)) {
					SoundDownloadManager.downloadSound(url, fileName, showOverlay);
					SoundDownloadManager.queueSound(fileName, position);
					return;
				}

				player.sendMessage(Text.literal("Press ").append(Text.literal(ModInputListener.getHotKey()).formatted(Formatting.YELLOW))
						.append(" to whitelist ").append(Text.literal(baseURL).formatted(Formatting.YELLOW)), true);

				ModInputListener.waitForKeyPress().thenAccept(confirmed -> {
					if (confirmed) {
						SoundDownloadManager.downloadSound(url, fileName, showOverlay);
						SoundDownloadManager.queueSound(fileName, position);
					}
				});
			}
		});

		// Client event for stopping sounds
		ModNetworking.NETWORK_CHANNEL.registerClientbound(StopSoundRecord.class, (payload, context) -> {
			Vec3d position = payload.position().toCenterPos();
			String id = SoundDescriptionManager.hashURL(payload.url()) + "/download";
			SoundManager.stopSound(position);

			if (Executable.YT_DLP.isProcessRunning(id)) {
				Executable.YT_DLP.getProcessStream(id).unsubscribe(position.toString());
				if (payload.canceled() && Executable.YT_DLP.getProcessStream(id).subscriberCount() <= 1) {
					Executable.YT_DLP.killProcess(id);
				}
			}
		});

		// Client event to open record ui
		ModNetworking.NETWORK_CHANNEL.registerClientbound(GUIRecord.class, (payload, context) -> {
			context.runtime().setScreen(new URLScreen(payload.url(), payload.duration(), payload.rewritable()));
		});
	}

	public record PlaySoundRecord(BlockPos position, String url, boolean showOverlay) {
	}

	public record StopSoundRecord(BlockPos position, String url, boolean canceled) {
	}

	public record GUIRecord(String url, int duration, boolean rewritable) {
	}
}
