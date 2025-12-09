package com.vinurl.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.vinurl.VinURLItems;
import com.vinurl.api.VinURLSound;
import com.vinurl.items.VinURLDisc;
import com.vinurl.mixinaccessor.JukeboxInteractionAccessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements JukeboxInteractionAccessor, SingleStackInventory {

	// Properties (Shadowed)

	@Shadow
	private long tickCount;

	@Shadow
	private long recordStartTick;

	// Properties (Unique)

	@Unique
	private UUID vinurl$lastInteractingPlayer;

	// Init

	public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// Interaction

	@Override
	public UUID vinurl$getLastInteractingPlayer() {
		return vinurl$lastInteractingPlayer;
	}

	@Override
	public void vinurl$setLastInteractingPlayer(UUID playerUuid) {
		this.vinurl$lastInteractingPlayer = playerUuid;
	}

	// Playback

	@Shadow
	private void stopPlaying() {
	}

	@Inject(at = @At("HEAD"), method = "removeStack")
	public void vinurl$stopPlaying(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
		if (isVinURLRecord()) {
			VinURLSound.stop(world, getStack(), getPos(), false);
			vinurl$setLastInteractingPlayer(null);
		}
	}

	@Inject(at = @At("TAIL"), method = "setStack")
	public void vinurl$startPlaying(int slot, ItemStack stack, CallbackInfo ci) {
		if (isVinURLRecord()) {
			VinURLSound.play(world, getStack(), getPos(), vinurl$getLastInteractingPlayer());
		}
	}

	@Inject(at = @At("HEAD"), method = "dropRecord")
	public void vinurl$cancelDownload(CallbackInfo ci) {
		if (isVinURLRecord()) {
			VinURLSound.stop(world, getStack(), getPos(), true);
			vinurl$setLastInteractingPlayer(null);
		}
	}

	// Ticking

	@Inject(at = @At("HEAD"), method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
	private void vinurl$tick(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (isVinURLRecord()) {
			NbtCompound nbt = getStack().getOrCreateNbt();
			if (tickCount > recordStartTick + nbt.getInt(VinURLDisc.DISC_DURATION_KEY) * 20L) {
				stopPlaying();
				VinURLSound.stop(world, getStack(), pos, false);
				vinurl$setLastInteractingPlayer(null);
			}
		}
	}

	// Types

	private boolean isVinURLRecord() {
		return getStack().isOf(VinURLItems.CUSTOM_RECORD) || getStack().isOf(VinURLItems.CUSTOM_RECORD_REWRITABLE);
	}
}
