package com.vinurl.client;

import static com.vinurl.client.VinURLClient.CLIENT;
import static com.vinurl.util.Constants.LOGGER;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;

import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;
import com.vinurl.util.MusicDescriptionFormatter;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class SoundManager {

	// Configuration

	public static final boolean ABBREVIATE_AUDIO_TITLES = false;

	// State

	private static final ConcurrentHashMap<Vec3d, FileSound> playingSounds = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<String, String> descriptionCache = new ConcurrentHashMap<>();

	// Playback

	public static void playSound(Vec3d position) {
		FileSound fileSound = playingSounds.get(position);
		if (fileSound != null) {
			CLIENT.getSoundManager().play(fileSound);
			CLIENT.inGameHud.setRecordPlayingOverlay(Text.literal(getDescription(fileSound.fileName)));
		}
	}

	public static void stopSound(Vec3d position) {
		CLIENT.getSoundManager().stop(playingSounds.remove(position));
	}

	public static void addSound(String fileName, Vec3d position, boolean loop) {
		playingSounds.put(position, new FileSound(fileName, position, loop));
	}

	// Utility (Description)

	public static String getDescription(String fileName) {
		return Optional.ofNullable(descriptionFromCache(fileName)).orElseGet(() -> descriptionToCache(fileName));
	}

	public static String descriptionToCache(String fileName) {
		descriptionCache.remove(fileName);
		return descriptionCache.compute(fileName, (k, v) -> {
			var artist = getOggAttribute(fileName, "artist");
			var title = getOggAttribute(fileName, "title");

			if (ABBREVIATE_AUDIO_TITLES) {
				var maxLength = MusicDescriptionFormatter.DEFAULT_MAX_LENGTH;
				return MusicDescriptionFormatter.abbreviateNameFromComponents(artist, title, maxLength);
			}

			return title;
		});
	}

	private static String getOggAttribute(String fileName, String attribute) {
		VorbisFile vorbisFile = null;
		try {
			vorbisFile = new VorbisFile(SoundDownloadManager.getAudioFile(fileName).toString());
			String metadata = vorbisFile.getComment(0).toString();

			String filter = "Comment: " + attribute + "=";
			return Stream.of(metadata.split("\n")).filter(line -> line.startsWith(filter)).map(line -> line.substring(filter.length()))
					.findFirst().orElse("N/A");
		} catch (JOrbisException e) {
			return "N/A";
		} finally {
			if (vorbisFile != null) {
				try {
					vorbisFile.close();
				} catch (IOException e) {
					LOGGER.error("Error closing vorbis file", e);
				}
			}
		}
	}

	public static String descriptionFromCache(String fileName) {
		return descriptionCache.get(fileName);
	}

	// Description (URL)

	public static String hashURL(String url) {
		return (url == null || url.isEmpty()) ? "" : DigestUtils.sha256Hex(url);
	}
}
