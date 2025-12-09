package com.vinurl;

import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.util.Identifier;

public final class ModNetworking {

	public static OwoNetChannel NETWORK_CHANNEL;

	public static void initialize() {
		NETWORK_CHANNEL = OwoNetChannel.create(Identifier.of(Mod.MOD_ID, "main"));
	}

}
