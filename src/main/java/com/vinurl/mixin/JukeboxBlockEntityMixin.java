package com.vinurl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.vinurl.VinURLItems;
import com.vinurl.api.VinURLSound;
import com.vinurl.items.VinURLDisc;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements SingleStackInventory, Clearable {

	@Shadow
	private long tickCount;

	@Shadow
	private long recordStartTick;

	@Shadow
	private void stopPlaying() {
	}

	public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Inject(at = @At("HEAD"), method = "removeStack")
	public void stopPlaying(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
		if (isVinURLRecord()) {
			VinURLSound.stop(world, getStack(), getPos(), false);
		}
	}

	@Inject(at = @At("TAIL"), method = "setStack")
	public void startPlaying(int slot, ItemStack stack, CallbackInfo ci) {
		if (isVinURLRecord()) {
			VinURLSound.play(world, getStack(), getPos());
		}
	}

	@Inject(at = @At("HEAD"), method = "dropRecord")
	public void cancelDownload(CallbackInfo ci) {
		if (isVinURLRecord()) {
			VinURLSound.stop(world, getStack(), getPos(), true);
		}
	}

	@Inject(at = @At("HEAD"), method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V")
	private void tick(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (isVinURLRecord()) {
			NbtCompound nbt = getStack().getOrCreateNbt();
			if (tickCount > recordStartTick + nbt.getInt(VinURLDisc.DISC_DURATION_KEY) * 20L) {
				stopPlaying();
				VinURLSound.stop(world, getStack(), pos, false);
			}
		}
	}

	private boolean isVinURLRecord() {
		return getStack().isOf(VinURLItems.CUSTOM_RECORD) || getStack().isOf(VinURLItems.CUSTOM_RECORD_REWRITABLE);
	}
}
