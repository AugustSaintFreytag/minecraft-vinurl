package com.vinurl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.vinurl.ModItems;
import com.vinurl.mixinaccessor.JukeboxInteractionAccessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(JukeboxBlock.class)
public class JukeboxBlockMixin {

	@Inject(method = "onUse", at = @At("HEAD"))
	private void vinurl$onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
			CallbackInfoReturnable<ActionResult> callbackInfo) {
		if (world.isClient()) {
			return;
		}

		var stack = player.getStackInHand(hand);

		if (!stack.isOf(ModItems.CUSTOM_RECORD) && !stack.isOf(ModItems.CUSTOM_RECORD_REWRITABLE)) {
			return;
		}

		var blockEntity = world.getBlockEntity(pos);

		if (blockEntity instanceof JukeboxInteractionAccessor ownerAccessor) {
			ownerAccessor.vinurl$setLastInteractingPlayer(player.getUuid());
		}
	}
}
