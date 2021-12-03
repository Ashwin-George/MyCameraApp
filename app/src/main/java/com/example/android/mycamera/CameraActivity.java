package com.example.android.mycamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraProvider;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.LifecycleOwner;

import android.content.pm.PackageManager;
import android.location.GnssAntennaInfo;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity {
    FloatingActionButton mCaptureButton;
    PreviewView mPreviewView;
    ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    ImageCapture mImageCapture;
    private static final int REQUEST_CODE_PERMISSION = 101;
    private static final String[] requiredPermissions={"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.MANAGE_EXTERNAL_STORAGE"};
    private  static File outputDir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if(!allPermisionsGranted())
            ActivityCompat.requestPermissions(CameraActivity.this,requiredPermissions,REQUEST_CODE_PERMISSION);
        for(String permission:requiredPermissions){
            Log.v(CameraActivity.class.getSimpleName(),
                    permission+String.valueOf(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED));
        }


        outputDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM+"/CameraX");
        if(!outputDir.mkdirs()){
            outputDir.mkdirs();
            Toast.makeText(CameraActivity.this,"Output folder created",Toast.LENGTH_SHORT);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mCaptureButton=(FloatingActionButton) findViewById(R.id.capture);
        mPreviewView=(PreviewView)findViewById(R.id.preview);
        mCameraProviderFuture=ProcessCameraProvider.getInstance(this);

        mCameraProviderFuture.addListener(()->{
            try {
                ProcessCameraProvider cameraProvider=mCameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },getExecutor());

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(CameraActivity.class.getSimpleName(),"Icon clicked");
                capturePhoto();

            }
        });

        mCaptureButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CameraActivity.this,"Long text",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
//        Camera Selector Use Case
        CameraSelector cameraSelector=new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK  )
                .build();
//        Preview Use Case
        Preview preview=new Preview.Builder().build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
//        Image Use Case
        mImageCapture=new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(mPreviewView.getDisplay().getRotation())
                .build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this,cameraSelector,preview,mImageCapture);
    }
    public boolean allPermisionsGranted(){
        for(String permission: requiredPermissions){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }

        }
        return true;

    }

    public void capturePhoto(){
        File photoDir=new File("/storage/emulated/0/AndroidStorageDirectory/Photos/CameraX");
        if(!photoDir.exists())
            photoDir.mkdir();
        Date date=new Date();

        File filePath;
        String Timestamp=String.valueOf(date.getTime());

        String photoFilePath=
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                Environment.getExternalStorageDirectory()
//                        +"/"+"CameraX-"+
                outputDir.getAbsolutePath()+
                               "/" +Timestamp+".jpg";

        File photoFile=new File(photoFilePath);

//        File photoDir=new File(Environment.getExternalStorageDirectory()+"/Photos/CameraX");
//        if(!photoDir.exists())
//            photoDir.mkdir();
//        Date date=new Date();
//        String Timestamp=String.valueOf(date.getTime());
//        String photoFilePath=photoDir.getAbsolutePath()+"/"+"CameraX-"+Timestamp+".jpg";
//        File photoFile=new File(photoFilePath);


        mImageCapture.takePicture(new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivity.this,"Image saved",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivity.this,"Error saving Photo "+exception.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.e(CameraActivity.class.getSimpleName(),exception.getMessage());

                    }
//                    public void onCaptureSuccess
                });


    }
}