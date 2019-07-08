package com.example.imagechooser;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
ImageView imageView;
Button button;
ImageView rotateLeft,rotateRight;
    Matrix matrix=new Matrix();
    Bitmap bitmap;
    Bitmap img;
    int angle=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.image_view);
        button=findViewById(R.id.button);
        rotateLeft=findViewById(R.id.rotate_left);
        rotateRight=findViewById(R.id.rotate_right);

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle=angle+90;
                imageView.setRotation(angle);
//                matrix.setRotate(angle);
//                bitmap=Bitmap.createBitmap(img,0,0,img.getWidth(),img.getHeight(),matrix,true);
//                imageView.setImageBitmap(bitmap);
                if (angle==360){
                    angle=0;
                }
            }
        });

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angle=angle-90;
                imageView.setRotation(angle);
//                matrix.setRotate(angle);
//                bitmap=Bitmap.createBitmap(img,0,0,img.getWidth(),img.getHeight(),matrix,true);
//                imageView.setImageBitmap(bitmap);
                if (angle==-360){
                    angle=0;
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode==RESULT_OK&&data!=null){
            Uri pickedImage=data.getData();

            InputStream inputStream;

            try {
                inputStream=getContentResolver().openInputStream(pickedImage);
                img=BitmapFactory.decodeStream(inputStream);
                ExifInterface exifInterface;
                exifInterface=new ExifInterface(inputStream);
               // int opration=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);

//                switch (opration){
//                    case ExifInterface.ORIENTATION_ROTATE_90:
//                        matrix.setRotate(90);
//                        break;
//                    case ExifInterface.ORIENTATION_ROTATE_180:
//                        matrix.setRotate(90);
//                        break;
//                }
                bitmap=Bitmap.createBitmap(img,0,0,img.getWidth(),img.getHeight(),matrix,true);
                imageView.setImageBitmap(bitmap);
                rotateLeft.setVisibility(View.VISIBLE);
                rotateRight.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
