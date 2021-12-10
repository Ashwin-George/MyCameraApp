package com.example.android.mycamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class GalleryActivity extends AppCompatActivity {
    ImageView mDisplayImage;
    TextView mDisplayText;
    static private final String EXTRA_URI="Extra Uri";
    static private final String EXTRA_ROTATION="Extra Rotation";
    private static final int SHOULD_PROCESS_IMAGE = 0;
    private static final int SHOULD_NOT_PROCESS_IMAGE = 1;
    private int rotationDegree;
    private FloatingActionButton mTextRecognizerButton;
    private Intent initialIntent;
    private Bitmap imageBitmap;

    TextRecognizer textRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mTextRecognizerButton=(FloatingActionButton) findViewById(R.id.text_recognizer);
        mDisplayImage=(ImageView) findViewById(R.id.display);
        mDisplayText=(TextView)findViewById(R.id.recognizedText);
        mDisplayText.setMovementMethod(new ScrollingMovementMethod());
        initialIntent=getIntent();

        String savedUri;
        if(initialIntent.hasExtra(EXTRA_URI)){
            savedUri=initialIntent.getStringExtra(EXTRA_URI);

            Bitmap bitmap =
                    BitmapFactory.decodeFile(savedUri);
            imageBitmap=bitmap;
            mDisplayImage.setImageBitmap(bitmap);

        }else{
            Log.e(GalleryActivity.class.getSimpleName(),"URI not found  in intent");
        }
        if(initialIntent.hasExtra(EXTRA_ROTATION)){
            rotationDegree=initialIntent.getIntExtra(EXTRA_ROTATION,-1);
            Log.v(GalleryActivity.class.getSimpleName(),"Rotation = "+rotationDegree);

        }else{
            Log.e(GalleryActivity.class.getSimpleName(),"Rotation not found  in intent");
        }


//        RecognizedTextFragment textFragment=new RecognizedTextFragment();
//        FragmentManager fragmentManager=getSupportFragmentManager();
        mTextRecognizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proccessForTextRecognition(imageBitmap,rotationDegree);
            }
        });


    }

    private void processImage(){




    }
    private void proccessForTextRecognition(Bitmap bitmap,int rotationDegree){
        textRecognizer =TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image=InputImage.fromBitmap(bitmap,rotationDegree);
        int[] processImage = new int[1];

        Task<Text> result= textRecognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(@NonNull Text finalresult) {

                        Toast.makeText(GalleryActivity.this,"Image processed",Toast.LENGTH_SHORT).show();
                        processImage[0] =SHOULD_PROCESS_IMAGE;

//                        Processing Image
                        StringBuilder builder=new StringBuilder();
                        String resultText = finalresult.getText();
                        for (Text.TextBlock block : finalresult.getTextBlocks()) {
                            String blockText = block.getText();
                            Point[] blockCornerPoints = block.getCornerPoints();
                            Rect blockFrame = block.getBoundingBox();
                            for (Text.Line line : block.getLines()) {
                                String lineText = line.getText();
                                Point[] lineCornerPoints = line.getCornerPoints();
                                Rect lineFrame = line.getBoundingBox();
                                for (Text.Element element : line.getElements()) {
                                    String elementText = element.getText();
                                    Point[] elementCornerPoints = element.getCornerPoints();
                                    Rect elementFrame = element.getBoundingBox();
                                    builder.append(elementText+" ");
                                }
                                builder.append("\n");
                            }
                            builder.append("\n\n");
                        }
                        mDisplayText.setText(builder.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GalleryActivity.this,"Image process failed "+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });



    }
}