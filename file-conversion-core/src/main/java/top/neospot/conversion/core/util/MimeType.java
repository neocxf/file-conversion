package top.neospot.conversion.core.util;

import java.util.EnumSet;

/**
 * utilities for customizing the MimeType
 */
public enum MimeType {
	PNG("image/png", "png"),
	JPEG("image/jpeg", "jpeg"),
	PDF("application/pdf", "pdf");

	private String type;
	private String extension;

	MimeType(String type, String extension) {
		this.type = type;
		this.extension = extension;
	}

	public String getType() {
		return type;
	}

	public String getExtension() {
		return extension;
	}

	public static MimeType get(String typeOrExtension) {
		for (MimeType m : EnumSet.allOf(MimeType.class)) {
			if (m.getType().equalsIgnoreCase(typeOrExtension) || m.getExtension().equalsIgnoreCase(typeOrExtension)) {
				return m;
			}
		}
		
		return MimeType.PNG;
	}
}