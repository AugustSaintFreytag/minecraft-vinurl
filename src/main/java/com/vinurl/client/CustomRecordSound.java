package com.vinurl.client;

import static com.vinurl.client.SoundDownloadManager.getAudioFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.vinurl.VinURLSounds;

import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundLoader;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public class CustomRecordSound extends AbstractSoundInstance {
	public final String fileName;

	public CustomRecordSound(String fileName, Vec3d position) {
		super(VinURLSounds.CUSTOM_MUSIC, SoundCategory.RECORDS, SoundInstance.createRandom());
		this.fileName = fileName;
		this.repeat = false;
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
	}

	@Override
	public CompletableFuture<AudioStream> getAudioStream(SoundLoader loader, Identifier id, boolean repeatInstantly) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				InputStream inputStream = new FileInputStream(getAudioFile(fileName));
				return new OggAudioStream(inputStream);
			} catch (IOException e) {
				throw new CompletionException(e);
			}
		}, Util.getMainWorkerExecutor());
	}
}
