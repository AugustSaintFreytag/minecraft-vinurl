package com.vinurl.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.net.URI;
import static com.vinurl.util.Constants.*;


public class ServerEvent {
	public static final int MAX_URL_LENGTH = 400;

	public static void register() {
		NETWORK_CHANNEL.registerClientboundDeferred(ClientEvent.GUIRecord.class);
		NETWORK_CHANNEL.registerClientboundDeferred(ClientEvent.PlaySoundRecord.class);
		NETWORK_CHANNEL.registerClientboundDeferred(ClientEvent.StopSoundRecord.class);

		// Server event handler for setting the URL on the custom record
		NETWORK_CHANNEL.registerServerbound(SetURLRecord.class, (payload, context) -> {
			PlayerEntity player = context.player();
			Hand stackHand = null;
			ItemStack stack = ItemStack.EMPTY;
			for (Hand hand : Hand.values()) {
				ItemStack currentStack = player.getStackInHand(hand);
				if (currentStack.isOf(CUSTOM_RECORD) || currentStack.isOf(CUSTOM_RECORD_REWRITABLE)) {
					stackHand = hand;
					stack = currentStack;
					break;
				}
			}

			if (stackHand == null || stack.isEmpty()) {
				player.sendMessage(Text.literal("VinURL-Disc needed in hand!"), true);
				return;
			}

			boolean isRewritable = stack.isOf(CUSTOM_RECORD_REWRITABLE);
			NbtCompound currentData = stack.getOrCreateNbt();

			if (currentData.getBoolean(DISC_LOCKED_NBT_KEY)) {
				player.sendMessage(Text.translatable("text.vinurl.custom_record.locked.tooltip"), true);
				return;
			}

			String url;

			try {
				url = new URI(payload.url()).toURL().toString();

			} catch (Exception e) {
				player.sendMessage(Text.literal("Song URL is invalid!"), true);
				return;
			}

			if (url.length() > MAX_URL_LENGTH) {
				player.sendMessage(Text.literal("Song URL is too long!"), true);
				return;
			}

			ItemStack singleRecordStack = stack;
			ItemStack remainingStack = ItemStack.EMPTY;

			if (stack.getCount() > 1) {
				singleRecordStack = stack.split(1);
				remainingStack = stack;
				player.setStackInHand(stackHand, singleRecordStack);
			}

			currentData = singleRecordStack.getOrCreateNbt();
			currentData.putString(DISC_URL_NBT_KEY, url);
			currentData.putInt(DISC_DURATION_KEY, payload.duration());
			currentData.putBoolean(DISC_LOOP_NBT_KEY, payload.loop());
			currentData.putBoolean(DISC_LOCKED_NBT_KEY, !isRewritable || payload.lock());
			currentData.putBoolean(DISC_REWRITABLE_NBT_KEY, isRewritable);
			
			singleRecordStack.setNbt(currentData);
			player.playSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, SoundCategory.BLOCKS, 1.0f, 1.0f);

			if (!remainingStack.isEmpty()) {
				if (!player.getInventory().insertStack(remainingStack)) {
					player.dropItem(remainingStack, false);
				}
			}
		});
	}

	public record SetURLRecord(String url, int duration, boolean loop, boolean lock) {}
}
