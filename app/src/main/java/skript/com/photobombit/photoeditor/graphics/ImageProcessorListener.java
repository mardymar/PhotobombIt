package skript.com.photobombit.photoeditor.graphics;

import android.graphics.Bitmap;

public interface ImageProcessorListener {
	void onProcessStart();
	void onProcessEnd(Bitmap result);
}
