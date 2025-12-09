package com.vinurl.api;

import java.util.UUID;

import com.vinurl.ModNetworking;
import com.vinurl.items.CustomMusicDiscItem;
import com.vinurl.net.ModClientEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VinURLSound {

	// Configuration

	private static final int JUKEBOX_RANGE = 16 * 16;

	// Playback

	public static void play(World world, ItemStack stack, BlockPos position, UUID ownerUuid) {
		if (world == null || world.isClient()) {
			return;
		}

		NbtCompound nbt = stack.getOrCreateNbt();

		for (PlayerEntity player : world.getPlayers()) {
			if (player.getPos().distanceTo(position.toCenterPos()) <= JUKEBOX_RANGE) {
				var showOverlay = ownerUuid != null && ownerUuid.equals(player.getUuid());
				var discUrl = nbt.getString(CustomMusicDiscItem.DISC_URL_NBT_KEY);
				var soundEvent = new ModClientEvents.PlaySoundRecord(position, discUrl, showOverlay);

				ModNetworking.NETWORK_CHANNEL.serverHandle(player).send(soundEvent);
			}
		}
	}

	public static void stop(World world, ItemStack stack, BlockPos position, boolean cancel) {
		if (world == null || world.isClient) {
			return;
		}

		NbtCompound nbt = stack.getOrCreateNbt();

		for (PlayerEntity player : world.getPlayers()) {
			ModNetworking.NETWORK_CHANNEL.serverHandle(player)
					.send(new ModClientEvents.StopSoundRecord(position, nbt.getString(CustomMusicDiscItem.DISC_URL_NBT_KEY), cancel));
		}
	}
}
