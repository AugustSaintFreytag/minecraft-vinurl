package com.vinurl.gui;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.vinurl.Mod;
import com.vinurl.ModNetworking;
import com.vinurl.client.SoundDescriptionManager;
import com.vinurl.exe.Executable;
import com.vinurl.net.ServerEvents;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.SlimSliderComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.TextureComponent;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class URLScreen extends BaseUIModelScreen<StackLayout> {
	// Configuration

	private static final int EXTRA_MUSIC_DURATION_SECONDS = 3;

	// State

	private String url;
	private boolean sliderDragged;
	private boolean simulate;
	private int duration;
	private boolean rewritable;

	// Components

	private final ButtonComponent.Renderer SIMULATE_BUTTON_TEXTURE = (matrices, button, delta) -> {
		RenderSystem.enableDepthTest();
		Identifier texture = !simulate ? (button.active && button.isHovered() ? Identifier.of(Mod.MOD_ID, "simulate_button_hovered")
				: Identifier.of(Mod.MOD_ID, "simulate_button")) : Identifier.of(Mod.MOD_ID, "simulate_button_disabled");
		NinePatchTexture.draw(texture, matrices, button.getX(), button.getY(), button.getWidth(), button.getHeight());
	};

	// Init

	public URLScreen(String defaultURL, int defaultDuration, boolean rewritable) {
		super(StackLayout.class, DataSource.asset(Identifier.of(Mod.MOD_ID, "disc_url_screen")));

		this.url = defaultURL;
		this.duration = defaultDuration;
		this.rewritable = rewritable;
	}

	// Build

	@Override
	protected void build(StackLayout stackLayout) {
		LabelComponent placeholderLabel = stackLayout.childById(LabelComponent.class, "placeholder_label");
		TextBoxComponent urlTextbox = stackLayout.childById(TextBoxComponent.class, "url_textbox");
		SlimSliderComponent durationSlider = stackLayout.childById(SlimSliderComponent.class, "duration_slider");
		ButtonComponent simulateButton = stackLayout.childById(ButtonComponent.class, "simulate_button");
		TextureComponent textFieldTexture = stackLayout.childById(TextureComponent.class, "text_field_disabled");

		durationSlider.value(duration);
		durationSlider.tooltipSupplier(slider -> Text.literal(String.format("%02d:%02d", duration / 60, duration % 60)));
		durationSlider.mouseDrag().subscribe((mouseX, mouseY, deltaX, deltaY, button) -> {
			sliderDragged = true;
			return true;
		});
		durationSlider.mouseUp().subscribe((mouseX, mouseY, button) -> {
			sliderDragged = false;
			return true;
		});
		durationSlider.onChanged().subscribe(newValue -> {
			duration = (int) newValue;
		});
		durationSlider.mouseScroll().subscribe((mouseX, mouseY, amount) -> {
			durationSlider.value(Math.max(durationSlider.min(), Math.min(durationSlider.max(), durationSlider.value() + amount)));
			return true;
		});

		simulateButton.renderer(SIMULATE_BUTTON_TEXTURE);
		simulateButton.onPress(button -> {
			if (simulate) {
				return;
			}

			simulate = true;
			button.tooltip(Text.literal("Calculating..."));

			Executable.YT_DLP.executeCommand(SoundDescriptionManager.hashURL(url) + "/duration", url, "--print", "DURATION: %(duration)d",
					"--no-playlist").subscribe("duration").onOutput(line -> {
						String type = line.substring(0, line.indexOf(':') + 1);
						String message = line.substring(type.length()).trim();

						if (type.equals("DURATION:")) {
							int duration = Integer.parseInt(message) + EXTRA_MUSIC_DURATION_SECONDS;
							durationSlider.value(duration);

							return;
						}

						if (type.equals("WARNING:")) {
							Mod.LOGGER.warn(message);
							return;
						}

						Mod.LOGGER.error(message);
						return;
					}).onError(error -> {
						button.tooltip(Text.literal("Automatic Duration"));
						simulate = false;
					}).onComplete(() -> {
						button.tooltip(Text.literal("Automatic Duration"));
						simulate = false;
					}).start();
		});

		urlTextbox.onChanged().subscribe(text -> placeholderLabel.text(Text.literal((url = text).isEmpty() ? "URL" : "")));
		urlTextbox.text(url);
		urlTextbox.focusLost().subscribe(() -> textFieldTexture.visibleArea(PositionedRectangle.of(0, 0, 151, 16)));
		urlTextbox.focusGained().subscribe((source) -> textFieldTexture.visibleArea(PositionedRectangle.of(0, 0, 0, 0)));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		var client = MinecraftClient.getInstance();

		if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
			ModNetworking.NETWORK_CHANNEL.clientHandle().send(new ServerEvents.SetURLRecord(url, duration, !rewritable));
			client.setScreen(null);
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		var client = MinecraftClient.getInstance();

		if (sliderDragged) {
			context.drawTooltip(client.textRenderer, Text.literal(String.format("%02d:%02d", duration / 60, duration % 60)), mouseX,
					mouseY);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
