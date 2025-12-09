package com.vinurl.client;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;

import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;
import com.vinurl.Mod;
import com.vinurl.util.MusicDescriptionFormatter;

public final class SoundDescriptionManager {

	// Configuration

	public static final boolean ABBREVIATE_AUDIO_TITLES = false;

	// State

	private static final ConcurrentHashMap<String, String> cachedDescriptionsByFileName = new ConcurrentHashMap<>();

	// Logic

	public static String getDescription(String fileName) {
		return cachedDescriptionsByFileName.computeIfAbsent(fileName, key -> {
			return makeDescriptionFromFile(fileName);
		});
	}

	public static void cacheDescription(String fileName) {
		cachedDescriptionsByFileName.put(fileName, makeDescriptionFromFile(fileName));
	}

	private static String makeDescriptionFromFile(String fileName) {
		var properties = propertiesFromAudioMetadata(fileName);
		var artist = properties.artist();
		var title = properties.title();

		if (ABBREVIATE_AUDIO_TITLES) {
			var maxLength = MusicDescriptionFormatter.DEFAULT_MAX_LENGTH;
			return MusicDescriptionFormatter.abbreviateNameFromComponents(artist, title, maxLength);
		}

		return title;
	}

	private static AudioMetadataProperties propertiesFromAudioMetadata(String fileName) {
		var artist = new AtomicReference<String>("");
		var title = new AtomicReference<String>("");

		withAudioFile(fileName, (audioFile, metadata) -> {
			var commentArtist = readAudioFileCommentAttributeFromMetadata(metadata, "artist");
			var commentTitle = readAudioFileCommentAttributeFromMetadata(metadata, "title");

			artist.set(commentArtist);
			title.set(commentTitle);
		});

		return new AudioMetadataProperties(artist.get(), title.get());
	}

	private static void withAudioFile(String fileName, BiConsumer<VorbisFile, String> block) {
		VorbisFile audioFile = null;

		try {
			audioFile = new VorbisFile(SoundDownloadManager.getAudioFile(fileName).toString());
			var metadata = audioFile.getComment(0).toString();

			block.accept(audioFile, metadata);
		} catch (JOrbisException e) {
			return;
		} finally {
			if (audioFile != null) {
				try {
					audioFile.close();
				} catch (IOException e) {
					Mod.LOGGER.error("Error closing vorbis audio file.", e);
				}
			}
		}
	}

	private static String readAudioFileCommentAttributeFromMetadata(String metadata, String attribute) {
		String filter = "Comment: " + attribute + "=";
		return Stream.of(metadata.split("\n")).filter(line -> line.startsWith(filter)).map(line -> line.substring(filter.length()))
				.findFirst().orElse("N/A");
	}

	// Description (URL)

	public static String hashURL(String url) {
		return (url == null || url.isEmpty()) ? "" : DigestUtils.sha256Hex(url);
	}

	// Library

	private static record AudioMetadataProperties(String artist, String title) {
	}

}
