package com.vinurl;

import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.util.Identifier;

public final class VinURLNetwork {

	public static OwoNetChannel NETWORK_CHANNEL;

	public static void initialize() {
		NETWORK_CHANNEL = OwoNetChannel.create(Identifier.of(VinURL.MOD_ID, "main"));
	}

}
