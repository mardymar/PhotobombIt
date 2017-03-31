package skript.com.photobombit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import skript.com.photobombit.permissions.PermissionUtil;
import skript.com.photobombit.photoeditor.graphics.ImageProcessor;
import skript.com.photobombit.photoeditor.utils.BitmapScalingUtil;

public class Home extends AppCompatActivity {

    public static final int CAMERA_REQUEST = 10;
    public static final int GALLERY_REQUEST = 20;
    public static final int CAMERA_PERMISSION = 30;
    AppCompatActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        mActivity = this;
    }

    public void cameraClicked(View view) {


        PermissionUtil.checkPermission(mActivity, Manifest.permission.CAMERA,
                new PermissionUtil.PermissionAskListener() {
                    @Override
                    public void onPermissionAsk() {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION
                        );
                    }
                    @Override
                    public void onPermissionPreviouslyDenied() {
                        //show a dialog explaining permission and then request permission
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION
                        );


                    }
                    @Override
                    public void onPermissionDisabled() {

                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION
                        );
                    }
                    @Override
                    public void onPermissionGranted() {

                        startCamera();

                    }
                });
    }

    public void startCamera(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAMERA_REQUEST);

    }

    public void galleryClicked(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        intent.setDataAndType(data, "image/*");

        startActivityForResult(intent, GALLERY_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startCamera();

                } else {

                    Toast.makeText(Home.this, "Camera permission denied",
                            Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK && (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST)) {
            Intent intent = new Intent(this, Editor.class);
            openBitmap(data.getData());

            intent.putExtra("flag", "From_Activity_Home");
            startActivity(intent);
        }
    }

    private void openBitmap(Uri imageUri) {
        Bitmap b;

        try {
            if (imageUri != null) {
                b = BitmapScalingUtil.bitmapFromUri(this, imageUri);
                ImageProcessor.getInstance().setBitmap(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
