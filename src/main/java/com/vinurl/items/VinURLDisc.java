package com.vinurl.items;

import static com.vinurl.util.Constants.DISC_DURATION_KEY;
import static com.vinurl.util.Constants.DISC_LOCKED_NBT_KEY;
import static com.vinurl.util.Constants.DISC_LOOP_NBT_KEY;
import static com.vinurl.util.Constants.DISC_URL_NBT_KEY;
import static com.vinurl.util.Constants.NETWORK_CHANNEL;
import static com.vinurl.util.Constants.SONG;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.vinurl.net.ClientEvent;

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

public class VinURLDisc extends MusicDiscItem {
	private final boolean rewritable;

	public VinURLDisc(boolean isRewritable) {
		super(15, SONG, new FabricItemSettings().maxCount(1).rarity(isRewritable ? Rarity.UNCOMMON : Rarity.RARE), 3600);
		this.rewritable = isRewritable;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);

		if (!world.isClient) {
			NbtCompound nbt = stack.getOrCreateNbt();
			if (!nbt.getBoolean(DISC_LOCKED_NBT_KEY)) {
				NETWORK_CHANNEL.serverHandle(player).send(new ClientEvent.GUIRecord(nbt.getString(DISC_URL_NBT_KEY),
						nbt.getInt(DISC_DURATION_KEY), nbt.getBoolean(DISC_LOOP_NBT_KEY), rewritable));
			} else {
				player.sendMessage(Text.translatable("text.vinurl.custom_record.locked.tooltip"), true);
			}
		}

		return TypedActionResult.success(stack);
	}

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
}
