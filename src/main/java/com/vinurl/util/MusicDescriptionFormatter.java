package com.vinurl.util;

/**
 * Utility to keep song names readable by trimming common noise and shorten long titles for aesthetics.
 */
public final class MusicDescriptionFormatter {

	// Configuration
	
	public static final int DEFAULT_MAX_LENGTH = 42;

	private static final String MUSIC_COMPONENT_DELIMITER = " - ";
	private static final String MUSIC_NAME_ELLIPSIS = "...";
	
	private static final int MIN_TITLE_LENGTH_FOR_SPLIT = 8;


	// Formatting

	public static String abbreviateNameFromComponents(String artist, String title, int maxLength) {
		return abbreviateName(concatenateRawNameFromComponents(artist, title), maxLength);
	}

	public static String abbreviateName(String name, int maxLength) {
		var normalized = normalizeRawComponent(name);
		
		if (maxLength <= 0 || normalized.length() <= maxLength) {
			return normalized;
		}

		var base = stripBracketedSubcomponents(normalized);
		var withoutFeaturing = stripFeaturingSubcomponents(base);

		var candidate = findFirstDefinedSubcomponent(maxLength, withoutFeaturing, base, normalized);
		
		if (!candidate.isEmpty()) {
			return candidate;
		}

		var splitShort = abbreviateWithArtistSplit(withoutFeaturing, maxLength);
		
		if (!splitShort.isEmpty()) {
			return splitShort;
		}

		return ellipsizeName(normalized, maxLength);
	}

	private static String concatenateRawNameFromComponents(String artist, String title) {
		var normalizedArtist = normalizeRawComponent(artist);
		var normalizedTitle = normalizeRawComponent(title);

		if (normalizedArtist.isEmpty()) {return normalizedTitle;}
		if (normalizedTitle.isEmpty()) {return normalizedArtist;}

		return normalizedArtist + MUSIC_COMPONENT_DELIMITER + normalizedTitle;
	}

	private static String normalizeRawComponent(String value) {
		if (value == null) {
			return "";
		}

		return value
			.replaceAll("\\s+", " ")
			.replaceAll("\\p{Cntrl}", "")
			.replaceAll("[\\ufe00-\\ufe0f]", "")
			.trim();
	}

	private static String stripBracketedSubcomponents(String value) {
		return value.replaceAll("\\s*[\\[(\\{][^\\])}]*[\\])}]", "").trim();
	}

	private static String stripFeaturingSubcomponents(String value) {
		return value.replaceAll("(?i)\\s*\\b(feat\\.?|ft\\.?|featuring)\\b.*", "").trim();
	}

	private static String findFirstDefinedSubcomponent(int maxLength, String... subcomponents) {
		for (var option : subcomponents) {
			if (!option.isEmpty() && option.length() <= maxLength) {
				return option;
			}
		}

		return "";
	}

	private static String abbreviateWithArtistSplit(String value, int maxLength) {
		var dashIndex = value.indexOf(" - ");
		
		if (dashIndex <= 0) {
			return "";
		}

		var artist = value.substring(0, dashIndex).trim();
		var title = value.substring(dashIndex + 3).trim();

		var remaining = maxLength - artist.length() - 3;
		
		if (remaining < MIN_TITLE_LENGTH_FOR_SPLIT) {
			return "";
		}

		return artist + MUSIC_COMPONENT_DELIMITER + ellipsizeName(stripBracketedSubcomponents(stripFeaturingSubcomponents(title)), remaining);
	}

	private static String ellipsizeName(String value, int maxLength) {
		if (value.length() <= maxLength) {
			return value;
		}
		
		if (maxLength <= 3) {
			return value.substring(0, Math.max(0, maxLength)).trim();
		}
		
		return value.substring(0, maxLength - 3).trim() + MUSIC_NAME_ELLIPSIS;
	}
}
