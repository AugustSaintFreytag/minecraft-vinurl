package com.vinurl.client;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.vinurl.Mod;
import com.vinurl.exe.Executable;
import com.vinurl.gui.ProgressOverlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class SoundDownloadManager {

	// Configuration

	public static final Path AUDIO_DIRECTORY = Mod.PATH.resolve("downloads");

	// Download

	public static void downloadSound(String url, String fileName, boolean showOverlay) {
		var client = MinecraftClient.getInstance();

		if (client.player == null) {
			return;
		}

		if (showOverlay) {
			ProgressOverlay.set(fileName, 0);
		}

		var postProcessorArguments = getPostProcessorArguments();
		var arguments = new String[] { url, "-x", "-q", "--progress", "--add-metadata", "--no-playlist", "--progress-template",
				"PROGRESS: %(progress._percent)d", "--newline", "--break-match-filter",
				"ext~=3gp|aac|flv|m4a|mov|mp3|mp4|ogg|wav|webm|opus", "--audio-format", "vorbis", "--audio-quality", "128k",
				postProcessorArguments[0], postProcessorArguments[1], "-P", AUDIO_DIRECTORY.toString(), "--ffmpeg-location",
				Executable.FFMPEG.DIRECTORY.toString(), "-o", fileName + ".%(ext)s" };

		Executable.YT_DLP.executeCommand(fileName + "/download", arguments).subscribe("main").onOutput(line -> {
			String type = line.substring(0, line.indexOf(':') + 1);
			String message = line.substring(type.length()).trim();

			switch (type) {
			case "PROGRESS:" -> ProgressOverlay.set(fileName, Integer.parseInt(message));
			case "WARNING:" -> Mod.LOGGER.warn(message);
			case "ERROR:" -> Mod.LOGGER.error(message);
			default -> Mod.LOGGER.info(line);
			}
		}).onError(error -> {
			ProgressOverlay.stopFailed(fileName);
			deleteSound(fileName);
		}).onComplete(() -> {
			ProgressOverlay.stop(fileName);
			SoundDescriptionManager.cacheDescription(fileName);
		}).start();
	}

	private static String[] getPostProcessorArguments() {
		if (Mod.CONFIG.degradeAudioQuality) {
			return new String[] { "--postprocessor-args",
					"ffmpeg:-af 'highpass=f=120, lowpass=f=9500, acompressor=threshold=-12dB:ratio=2.5:attack=10:release=200, acrusher=bits=10:mix=0.2, vibrato=f=4:d=0.003' -ac 1 -c:a libvorbis -q:a 3" };
		}

		return new String[] { "", "" };
	}

	// Queue

	public static void queueSound(String fileName, Vec3d position) {
		Executable.YT_DLP.getProcessStream(fileName + "/download").subscribe(position.toString()).onComplete(() -> {
			SoundManager.playSound(position);
		}).start();
	}

	// Delete

	public static void deleteSound(String fileName) {
		File[] filesToDelete = AUDIO_DIRECTORY.toFile().listFiles(file -> file.getName().contains(fileName));
		if (filesToDelete == null) {
			return;
		}

		for (File file : filesToDelete) {
			FileUtils.deleteQuietly(file);
		}
	}

	// Utility

	public static String getBaseURL(String url) {
		try {
			URI baseURL = new URI(url);
			return baseURL.getScheme() + "://" + baseURL.getHost();
		} catch (Exception e) {
			return "";
		}
	}

	public static File getAudioFile(String fileName) {
		return AUDIO_DIRECTORY.resolve(fileName + ".ogg").toFile();
	}

}
