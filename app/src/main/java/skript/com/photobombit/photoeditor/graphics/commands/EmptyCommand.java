package skript.com.photobombit.photoeditor.graphics.commands;

import android.graphics.Bitmap;

public class EmptyCommand implements ImageProcessingCommand {
	
	public static final String ID = "EmptyCommand";
	
	public Bitmap process(Bitmap bitmap) {
		return bitmap;
	}

	public String getId() {
		return ID;
	}

}
