package com.vinurl.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.vinurl.ModNetworking;
import com.vinurl.ModSounds;
import com.vinurl.net.ModClientEvents;
import com.vinurl.items.DiscDecoration;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CustomMusicDiscItem extends MusicDiscItem {

	// NBT

	public static final String DISC_URL_NBT_KEY = "MusicUrl";
	public static final String DISC_REWRITABLE_NBT_KEY = "Rewritable";
	public static final String DISC_LOCKED_NBT_KEY = "Locked";
	public static final String DISC_DURATION_KEY = "Duration";
	public static final String DISC_DECORATION_KEY = "Decoration";

	// State

	private final boolean rewritable;

	// Init

	public CustomMusicDiscItem(boolean isRewritable) {
		super(15, ModSounds.CUSTOM_MUSIC, new FabricItemSettings().maxCount(1).rarity(isRewritable ? Rarity.UNCOMMON : Rarity.RARE), 3600);
		this.rewritable = isRewritable;
	}

	// Interaction

	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (!world.isClient) {
			NbtCompound nbt = stack.getOrCreateNbt();
			if (!nbt.getBoolean(DISC_LOCKED_NBT_KEY)) {
				var discUrl = nbt.getString(DISC_URL_NBT_KEY);
				var discDuration = nbt.getInt(DISC_DURATION_KEY);
				var event = new ModClientEvents.GUIRecord(discUrl, discDuration, rewritable);

				ModNetworking.NETWORK_CHANNEL.serverHandle(player).send(event);
			} else {
				player.sendMessage(Text.translatable("text.vinurl.custom_record.locked.tooltip"), true);
			}
		}

		return TypedActionResult.success(stack);
	}

	// Tooltip

	@Override
	public void appendTooltip(ItemStack stack, @Nullable
	World world, List<Text> tooltip, TooltipContext context) {
		NbtCompound nbt = stack.getOrCreateNbt();
		if (nbt.equals(new NbtCompound())) {
			return;
		}

		tooltip.add(Text.translatable("itemGroup.tools").formatted(Formatting.BLUE));

		if (nbt.getBoolean(DISC_LOCKED_NBT_KEY)) {
			tooltip.add(Text.translatable("text.vinurl.custom_record.locked.tooltip").formatted(Formatting.GRAY));
		}
	}

	public static DiscDecoration getDecoration(ItemStack stack) {
		return DiscDecoration.from(stack);
	}

	public static void setDecoration(ItemStack stack, DiscDecoration decoration) {
		decoration.writeTo(stack);
	}

	public boolean isRewritable() {
		return rewritable;
	}
}
