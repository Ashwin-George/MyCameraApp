package com.example.android.mycamera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity {
    ImageView mDisplayImage;
    static private final String EXTRA_URI="Extra Uri";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mDisplayImage=(ImageView) findViewById(R.id.display);
        Intent initialIntent=getIntent();
        String savedUri;
        if(initialIntent.hasExtra(EXTRA_URI)){
            savedUri=initialIntent.getStringExtra(EXTRA_URI);
            Uri imageUri;
            imageUri=Uri.parse(savedUri);
            Bitmap bitmap =
//                        MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                    BitmapFactory.decodeFile(savedUri);
            mDisplayImage.setImageBitmap(bitmap);

        }else{
            Log.e(GalleryActivity.class.getSimpleName(),"URI not found  in intent");
        }

    }
}