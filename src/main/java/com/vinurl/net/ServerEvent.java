package com.vinurl.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.net.URI;
import java.util.stream.Stream;

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
			ItemStack stack = Stream.of(Hand.values())
				.map(player::getStackInHand)
				.filter(currentStack -> currentStack.getItem() == CUSTOM_RECORD)
				.findFirst()
				.orElse(null);

			if (stack == null) {
				player.sendMessage(Text.literal("VinURL-Disc needed in hand!"), true);
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

			player.playSound(SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, SoundCategory.BLOCKS, 1.0f, 1.0f);

			stack.setNbt(new NbtCompound() {{
				putString(DISC_URL_NBT_KEY, url);
				putInt(DISC_DURATION_KEY, payload.duration());
				putBoolean(DISC_LOOP_NBT_KEY, payload.loop());
				putBoolean(DISC_LOCKED_NBT_KEY, payload.lock());
			}});
		});
	}

	public record SetURLRecord(String url, int duration, boolean loop, boolean lock) {}
}
