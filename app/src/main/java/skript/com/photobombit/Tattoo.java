package skript.com.photobombit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import skript.com.photobombit.multitouch.MultiTouchListener;
import skript.com.photobombit.photoeditor.graphics.ImageProcessor;


public class Tattoo extends Activity implements View.OnClickListener {
    static ImageView imageView, tattoo;
    ImageProcessor imageProcessor;
    SeekBar seekBar;
    ImageButton trans, save, move, allMove;
    RelativeLayout parent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tattoo);

        imageProcessor = ImageProcessor.getInstance();
        parent = (RelativeLayout) findViewById(R.id.RL);

        initComponents();
        initImageView();
        initTattoo();

    }

    private void initComponents() {


        seekBar = (SeekBar) findViewById(R.id.seekBar);

        seekBar.setProgress(255);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {

                // TODO Auto-generated method stub

                int alpha = seekBar.getProgress();
                tattoo.setImageAlpha(alpha);
            }
        });

        trans = (ImageButton) findViewById(R.id.trans_button);
        trans.setOnClickListener(this);

        move = (ImageButton) findViewById(R.id.multiTouch_button);
        move.setOnClickListener(this);

        save = (ImageButton) findViewById(R.id.tattoo_save_button);
        save.setOnClickListener(this);

        allMove = (ImageButton) findViewById(R.id.all_move);
        allMove.setOnClickListener(this);
    }

    private void initImageView() {
        imageView = (ImageView) findViewById(R.id.picToEdit);

        Bitmap bmp = imageProcessor.getBitmap();
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();


        if( (float) bmp.getWidth() / (float) bmp.getHeight() < (float) metrics.widthPixels / (float) metrics.heightPixels) {

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                   ViewGroup.LayoutParams.WRAP_CONTENT,
                   ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

            imageView.setLayoutParams(layoutParams);

        }

        imageView.setImageBitmap(bmp);


        MultiTouchListener mtl = new MultiTouchListener();
        mtl.isRotateEnabled = false;
        imageView.setOnTouchListener(mtl);
    }

    private void initTattoo() {
        tattoo = (ImageView) findViewById(R.id.tattoo);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Uri uri = (Uri) extras.get("ID");

        Glide.with(this).load(uri)
                .crossFade()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(tattoo);

        MultiTouchListener mtl = new MultiTouchListener();
        tattoo.setOnTouchListener(mtl);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trans_button:

                disableTouch();
                seekBar.setVisibility(View.VISIBLE);

                break;

            case R.id.multiTouch_button:

                seekBar.setVisibility(View.GONE);

                MultiTouchListener mtl = new MultiTouchListener();
                mtl.isRotateEnabled = false;
                imageView.setOnTouchListener(mtl);

                mtl = new MultiTouchListener();
                tattoo.setOnTouchListener(mtl);

                break;

            case R.id.tattoo_save_button:

                seekBar.setVisibility(View.GONE);
                disableTouch();

                Bitmap bit1 = ImageProcessor.getInstance().getBitmap();

                tattoo.buildDrawingCache();
                Bitmap bit2 = tattoo.getDrawingCache();

                Bitmap bitmap = overlay(bit1, bit2);

                bit1.recycle();
                bit2.recycle();

                ImageProcessor.getInstance().setBitmap(bitmap);
                Intent intent = new Intent(this, Editor.class);

                intent.putExtra("flag","From_Activity_Tattoo");

                Toast.makeText(Tattoo.this, getString(R.string.tattoo_saved_toast),
                        Toast.LENGTH_LONG).show();

                startActivity(intent);

                break;

            case R.id.all_move:

                disableTouch();
                seekBar.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                parent.setLayoutParams(params);

                MultiTouchListener mtlAll = new MultiTouchListener();
                mtlAll.isRotateEnabled = false;

                parent.setOnTouchListener(mtlAll);

                break;
        }
    }

    private void disableTouch() {

        imageView.setOnTouchListener(null);
        tattoo.setOnTouchListener(null);
        parent.setOnTouchListener(null);

    }


    public static Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {

        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());


        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        final float baseScale = f[Matrix.MSCALE_X] * imageView.getScaleX();


        float finalScale = tattoo.getScaleX() * (float) tattoo.getWidth() / (float) bmp2.getWidth() / baseScale;


        float oiRotation=tattoo.getRotation();

        int[] frontCoords = getRelativeCoords(tattoo);
        int[] baseCoords = getRelativeCoords(imageView);


        float transX = (frontCoords[0] - baseCoords[0]) / baseScale;
        float transY = (frontCoords[1] - baseCoords[1]) / baseScale;


        Matrix matrix = new Matrix();
        matrix.postScale(finalScale,finalScale);
        matrix.postRotate(oiRotation);
        matrix.postTranslate(transX, transY);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0,0 , paint);
        canvas.drawBitmap(bmp2, matrix, paint);


        bmp1.recycle();
        bmp2.recycle();

        return bmOverlay;
    }

    private static int[] getRelativeCoords(View v) {

        int[] viewLocation = new int[2];
        v.getLocationOnScreen(viewLocation);


        return new int[]{viewLocation[0], viewLocation[1]};
    }


}
