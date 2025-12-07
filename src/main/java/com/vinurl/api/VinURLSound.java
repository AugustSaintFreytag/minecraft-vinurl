package com.vinurl.api;

import static com.vinurl.util.Constants.DISC_LOOP_NBT_KEY;
import static com.vinurl.util.Constants.DISC_URL_NBT_KEY;
import static com.vinurl.util.Constants.NETWORK_CHANNEL;

import com.vinurl.net.ClientEvent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VinURLSound {
	private static final int JUKEBOX_RANGE = 64;

	public static void play(World world, ItemStack stack, BlockPos position) {
		if (world == null || world.isClient) {
			return;
		}
		NbtCompound nbt = stack.getOrCreateNbt();
		for (PlayerEntity player : world.getPlayers()) {
			if (player.getPos().distanceTo(position.toCenterPos()) <= JUKEBOX_RANGE) {
				NETWORK_CHANNEL.serverHandle(player).send(
						new ClientEvent.PlaySoundRecord(position, nbt.getString(DISC_URL_NBT_KEY), nbt.getBoolean(DISC_LOOP_NBT_KEY)));
			}
		}
	}

	public static void stop(World world, ItemStack stack, BlockPos position, boolean cancel) {
		if (world == null || world.isClient) {
			return;
		}

		NbtCompound nbt = stack.getOrCreateNbt();

		for (PlayerEntity player : world.getPlayers()) {
			NETWORK_CHANNEL.serverHandle(player).send(new ClientEvent.StopSoundRecord(position, nbt.getString(DISC_URL_NBT_KEY), cancel));
		}
	}
}