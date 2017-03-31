package skript.com.photobombit;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import skript.com.photobombit.multitouch.MultiTouchListener;
import skript.com.photobombit.permissions.PermissionUtil;
import skript.com.photobombit.photoeditor.graphics.ImageProcessor;


public class Editor extends AppCompatActivity implements View.OnClickListener {
    //private static final int EDITOR_FUNCTION = 1;

    private ImageView imageView;
    ImageButton tattoos, rotate, save;
    ProgressDialog mProgressDiag;

    public static final int GALLERY_REQUEST = 20;
    public static final int LOAD_EXTERNAL_STORAGE_PERMISSION = 40;

    private final String DIR_SAVE="Photobomb It";
    public static final int EXTERNAL_STORAGE_PERMISSION = 30;

    private AppCompatActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mActivity = this;

        setContentView(R.layout.activity_editor);

        mProgressDiag = new ProgressDialog(this);
        mProgressDiag.setCancelable(false);

        initComponents();
        initImageView();

        imageView.post(new Runnable() {
            @Override
            public void run() {
                fitImageView();
            }
        });

    }


    private void initComponents() {
        tattoos = (ImageButton) findViewById(R.id.tattoo_button);
        tattoos.setOnClickListener(this);

        rotate = (ImageButton) findViewById(R.id.rotate_button);
        rotate.setOnClickListener(this);

        save = (ImageButton) findViewById(R.id.save_button);
        save.setOnClickListener(this);
    }

    private void initImageView() {
        imageView = (ImageView) findViewById(R.id.picture);

        Bitmap b = ImageProcessor.getInstance().getBitmap();
        if (b != null) {
            imageView.setImageBitmap(b);
        }

        MultiTouchListener mtl = new MultiTouchListener();
        mtl.isRotateEnabled = false;

        imageView.setOnTouchListener(mtl);
    }

    private void fitImageView() {
        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        RectF drawableRect = new RectF(0, 0, imageWidth, imageHeight);
        RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());

        Matrix matrix = imageView.getImageMatrix();
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

        imageView.setImageMatrix(matrix);

        imageView.invalidate();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tattoo_button:
                startTattooActivity();
                break;

            case R.id.rotate_button:
                rotate();
                break;

            case R.id.save_button:
                if (getIntent().getStringExtra("flag").equals("From_Activity_Home")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(R.string.editor_save_without_change_alert);

                    alert.setPositiveButton(R.string.save_anyway, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            initSave();
                        }
                    });

                    alert.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dlg = alert.show();
                    WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dlg.getWindow().setAttributes(lp);
                    dlg.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


                } else if (!isExternalStorageWritable()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Oops. Your external storage is unavailable.");

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dlg = alert.show();
                    WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dlg.getWindow().setAttributes(lp);
                    dlg.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                }

                else {
                    initSave();
                }

                break;
        }
    }

    private void startTattooActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        intent.setDataAndType(data, "image/*");

        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && (requestCode == GALLERY_REQUEST )) {
            Intent intent = new Intent(this, Tattoo.class);
            intent.putExtra("ID", data.getData());
            startActivity(intent);
        }
    }

    private void rotate() {
        ImageProcessor ip = ImageProcessor.getInstance();

        Matrix matrix = new Matrix();

        matrix.setScale(1f, 1f);
        matrix.postRotate(90);

        ip.setBitmap(Bitmap.createBitmap(ip.getBitmap(),
                0, 0, ip.getBitmap().getWidth(), ip.getBitmap().getHeight(), matrix, true));

        imageView.setImageBitmap(ip.getBitmap());

        fitImageView();
    }

    private void initTattoo(){

        PermissionUtil.checkPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE,
                new PermissionUtil.PermissionAskListener() {
                    @Override
                    public void onPermissionAsk() {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_PERMISSION
                        );
                    }
                    @Override
                    public void onPermissionPreviouslyDenied() {
                        //show a dialog explaining permission and then request permission
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                LOAD_EXTERNAL_STORAGE_PERMISSION
                        );


                    }
                    @Override
                    public void onPermissionDisabled() {

                        Toast.makeText(mActivity, "Permission Disabled.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionGranted() {

                        startTattooActivity();

                    }
                });

    }

    private void initSave(){

        PermissionUtil.checkPermission(mActivity, Manifest.permission.CAMERA,
                new PermissionUtil.PermissionAskListener() {
                    @Override
                    public void onPermissionAsk() {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_PERMISSION
                        );
                    }
                    @Override
                    public void onPermissionPreviouslyDenied() {
                        //show a dialog explaining permission and then request permission
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_PERMISSION
                        );


                    }
                    @Override
                    public void onPermissionDisabled() {

                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                EXTERNAL_STORAGE_PERMISSION
                        );
                    }
                    @Override
                    public void onPermissionGranted() {

                        save();

                    }
                });

    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    save();

                } else {

                    Toast.makeText(Editor.this, "Storage permission denied",
                            Toast.LENGTH_SHORT).show();

                }
                return;
            }

            case LOAD_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startTattooActivity();

                } else {

                    Toast.makeText(Editor.this, "Storage permission denied",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private void save(){

        new AsyncTask<Bitmap,String,Object>(){

            public void onPreExecute(){
                mProgressDiag.setTitle("Please Wait");
                mProgressDiag.setMessage("Processing Image....");
            }

            public void onProgressUpdate(String... values){
                mProgressDiag.setMessage(values[0]);
            }

            @Override
            protected Object doInBackground(Bitmap[] objects) {


                publishProgress("saving image to " + DIR_SAVE + " .....");

                File extDir= Environment.getExternalStorageDirectory();
                File appDir=new File(extDir,DIR_SAVE);

                if(!appDir.exists())appDir.mkdirs();

                String fileName = "Photobomb" + System.currentTimeMillis() + ".jpg";

                try {
                    FileOutputStream ostream=new FileOutputStream(appDir.getAbsolutePath()+ File.separator+fileName);
                    ImageProcessor.getInstance().getBitmap().compress(Bitmap.CompressFormat.JPEG,100,ostream);

                    try {
                        ostream.close();
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(appDir.getAbsolutePath() + File.separator + fileName))));


                    } catch (IOException e) {

                    }

                } catch (FileNotFoundException e) {

                }


                return null;
            }

            public void onPostExecute(Object result){
                mProgressDiag.dismiss();

                Toast.makeText(Editor.this, getString(R.string.image_saved_toast),
                        Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Editor.this, Home.class);
                startActivity(intent);
            }

        }.execute();

    }


    public void onDestroy() {
        super.onDestroy();

        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap.recycle();
        }

    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case EDITOR_FUNCTION:
                if (resultCode == RESULT_OK) {
                    imageView.setImageBitmap(ImageProcessor.getInstance().getBitmap());
                }
                break;
            default:
                break;
        }
    }*/

    public void onResume() {
        super.onResume();
        Bitmap savedImagePath = ImageProcessor.getInstance().getBitmap();
        imageView.setImageBitmap(savedImagePath);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("flag").equals("From_Activity_Tattoo")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.tattoo_back_alert_title);

            alert.setPositiveButton(R.string.go_home, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    launchHomeActivity();
                }
            });

            alert.setNegativeButton(R.string.stay_here,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            AlertDialog dlg = alert.show();
            WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dlg.getWindow().setAttributes(lp);
            dlg.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


        } else {

            launchHomeActivity();

        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

}
