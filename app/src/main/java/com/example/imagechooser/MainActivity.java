package com.example.imagechooser;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsSharedAccessSignature;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
ImageView imageView;
Button button;
TextView imgSizeText;
LinearLayout saveBtn,cancelBtn;
    Bitmap bitmap;
    Uri pickedImage,uriInputImg;
    URI i;
    InputStream inputStreamImg;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.image_view);
        button=findViewById(R.id.button);
        imgSizeText =findViewById(R.id.img_size);
        saveBtn=findViewById(R.id.save_btn);
        cancelBtn=findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                imgSizeText.setText("");
                bitmap=null;
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveImage(bitmap);
                imageView.setImageDrawable(null);
                saveBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.GONE);
                imgSizeText.setText("");
//                progressDialog=new ProgressDialog(getApplicationContext());
//                progressDialog.setMessage("Please Wait...");
//                progressDialog.show();
                UploadImage();
//               task.execute();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkPermission();
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE&&resultCode== Activity.RESULT_OK){
            pickedImage=CropImage.getPickImageResultUri(this,data);
            cropReq(pickedImage);

        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                try {
                    bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),result.getUri());
                    imageView.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);

                    uriInputImg=Uri.parse(path);
                    inputStreamImg = getContentResolver().openInputStream(uriInputImg);

                    byte[] imageInByte = stream.toByteArray();
                    long lengthbmp = imageInByte.length/1000;
//                    bitmap.getWidth();
//                    bitmap.getHeight();
                    imgSizeText.setText((lengthbmp)+" KB");
                    imgSizeText.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    cancelBtn.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void checkPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},555);
            }catch (Exception e){

            }
        }
        else {
            pickImg();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode==555&&grantResults[0]== PackageManager.PERMISSION_GRANTED){

            pickImg();
        }else {
            checkPermission();
        }
    }
    public void pickImg(){
        CropImage.startPickImageActivity(this);
    }
    public void cropReq(Uri uri){
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(this);
    }
    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/MyFileFolder");
        myDir.mkdirs();

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();

        String fname = "Image-"+ ts +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            i=file.toURI();
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(MainActivity.this,"File Saved successfully",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void UploadImage()
    {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.uriInputImg);
            final int imageLength = imageStream.available();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        final String imageName = ImageManager.UploadImage(imageStream, imageLength);

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Image Uploaded Successfully. Name = " + imageName, Toast.LENGTH_SHORT).show();
                             //   progressDialog.dismiss();
                            }
                        });
                    }
                    catch(Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }});
            th.start();
        }
        catch(Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
