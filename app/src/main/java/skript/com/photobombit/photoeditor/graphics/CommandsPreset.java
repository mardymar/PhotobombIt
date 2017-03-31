package skript.com.photobombit.photoeditor.graphics;

import java.util.ArrayList;

import skript.com.photobombit.photoeditor.graphics.commands.BlackFrameCommand;
import skript.com.photobombit.photoeditor.graphics.commands.ColorFilterCommand;
import skript.com.photobombit.photoeditor.graphics.commands.EmbossCommand;
import skript.com.photobombit.photoeditor.graphics.commands.EmptyCommand;
import skript.com.photobombit.photoeditor.graphics.commands.GammaCorrectionCommand;
import skript.com.photobombit.photoeditor.graphics.commands.GaussianBlurCommand;
import skript.com.photobombit.photoeditor.graphics.commands.GrayscaleCommand;
import skript.com.photobombit.photoeditor.graphics.commands.ImageProcessingCommand;
import skript.com.photobombit.photoeditor.graphics.commands.InvertColorCommand;
import skript.com.photobombit.photoeditor.graphics.commands.MirrorCommand;
import skript.com.photobombit.photoeditor.graphics.commands.SepiaCommand;
import skript.com.photobombit.photoeditor.graphics.commands.SharpenCommand;

public class CommandsPreset {

	public static final ArrayList<ImageProcessingCommand> Preset = new ArrayList<ImageProcessingCommand>();
	public static final ArrayList<String> Names = new ArrayList<String>();

	static {
		Preset.add(new EmptyCommand());
		Names.add("No Filter");
		Preset.add(new GaussianBlurCommand());
		Names.add("Gaussian Blur");
		Preset.add(new GrayscaleCommand());
		Names.add("Grayscale");
//		Preset.add(new TintCommand(30));
//		Names.add("Tint 1");
//		Preset.add(new TintCommand(70));
//		Names.add("Tint 2");
		Preset.add(new BlackFrameCommand());
		Names.add("Black Frame");
//		Preset.add(new ColorBoostCommand(Color.RED, 20));
//		Names.add("Red Boost");
//		Preset.add(new ColorBoostCommand(Color.GREEN, 50));
//		Names.add("Green Boost");
//		Preset.add(new ColorBoostCommand(Color.BLUE, 20));
//		Names.add("Blue Boost");
		Preset.add(new ColorFilterCommand(1.1, 0.7, 0.7));
		Names.add("Color Filter 1");
		Preset.add(new ColorFilterCommand(0.7, 1.1, 0.7));
		Names.add("Color Filter 2");
		Preset.add(new ColorFilterCommand(0.7, 0.7, 1.1));
		Names.add("Color Filter 3");
		Preset.add(new ColorFilterCommand(1.3, 1.1, 0.8));
		Names.add("Color Filter 4");
//		Preset.add(new DecreaseColorDepthCommand(128));
//		Names.add("Decrease Color Depth");
		Preset.add(new GammaCorrectionCommand(0.6, 0.5, 0.7));
		Names.add("Gamma Correction");
		Preset.add(new InvertColorCommand());
		Names.add("Invert Color");
		Preset.add(new MirrorCommand());
		Names.add("Mirror");
		Preset.add(new SepiaCommand(2, 1, 0, 20));
		Names.add("Sepia");
		Preset.add(new SepiaCommand(2, 2, 0, 20));
		Names.add("Sepia 2");
		Preset.add(new SepiaCommand(1.62, 0.78, 1.21, 20));
		Names.add("Sepia 3");
		Preset.add(new SepiaCommand(1.62, 1.28, 1.01, 45));
		Names.add("Sepia 4");
		Preset.add(new SharpenCommand(13));
		Names.add("Sharpen");
		Preset.add(new EmbossCommand());
		Names.add("Emboss");
	}

	public static final Integer[] ImageIds = new Integer[] {
			 };
}