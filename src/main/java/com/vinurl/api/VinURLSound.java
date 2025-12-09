package com.vinurl.api;

import java.util.UUID;

import com.vinurl.VinURLNetwork;
import com.vinurl.items.VinURLDisc;
import com.vinurl.net.ClientEvent;

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
				VinURLNetwork.NETWORK_CHANNEL.serverHandle(player).send(new ClientEvent.PlaySoundRecord(position,
						nbt.getString(VinURLDisc.DISC_URL_NBT_KEY), nbt.getBoolean(VinURLDisc.DISC_LOOP_NBT_KEY), showOverlay));
			}
		}
	}

	public static void stop(World world, ItemStack stack, BlockPos position, boolean cancel) {
		if (world == null || world.isClient) {
			return;
		}

		NbtCompound nbt = stack.getOrCreateNbt();

		for (PlayerEntity player : world.getPlayers()) {
			VinURLNetwork.NETWORK_CHANNEL.serverHandle(player)
					.send(new ClientEvent.StopSoundRecord(position, nbt.getString(VinURLDisc.DISC_URL_NBT_KEY), cancel));
		}
	}
}
