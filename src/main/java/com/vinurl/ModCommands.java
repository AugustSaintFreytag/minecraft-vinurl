package com.vinurl;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.io.FileUtils;

import com.mojang.brigadier.context.CommandContext;
import com.vinurl.client.SoundDownloadManager;
import com.vinurl.exe.Executable;

import io.wispforest.owo.config.ui.ConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModCommands {

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher,
				registryAccess) -> dispatcher.register(ClientCommandManager.literal(Mod.MOD_ID)
						.then(ClientCommandManager.literal("delete").executes(ModCommands::deleteAudioFiles))
						.then(ClientCommandManager.literal("update").executes(ModCommands::updateExecutables))
						.then(ClientCommandManager.literal("config").executes(ModCommands::openConfig))));
	}

	private static int deleteAudioFiles(CommandContext<FabricClientCommandSource> context) {
		try {
			FileUtils.deleteDirectory(SoundDownloadManager.AUDIO_DIRECTORY.toFile());
			context.getSource().sendFeedback(Text.literal("Deleted all audio files"));
			return 1;
		} catch (IOException e) {
			context.getSource().sendFeedback(Text.literal("Deleted only non active audio files"));
			return 0;
		}
	}

	private static int updateExecutables(CommandContext<FabricClientCommandSource> context) {
		context.getSource().sendFeedback(Text.literal("Checking for updates..."));
		CompletableFuture.runAsync(() -> {
			boolean anyUpdate = false;
			for (Executable executable : Executable.values()) {
				String current = executable.currentVersion();
				if (executable.checkForUpdates()) {
					String latest = executable.currentVersion();
					context.getSource().sendFeedback(Text.literal(String.format("%s: %s -> %s", executable, current, latest)));
					anyUpdate = true;
				}
			}
			if (!anyUpdate) {
				context.getSource().sendFeedback(Text.literal("Everything is up to date!"));
			}
		});
		return 1;
	}

	private static int openConfig(CommandContext<FabricClientCommandSource> context) {
		var client = MinecraftClient.getInstance();
		client.send(() -> client.setScreen(Objects.requireNonNull(ConfigScreen.getProvider(Mod.MOD_ID)).apply(null)));

		return 0;
	}
}
