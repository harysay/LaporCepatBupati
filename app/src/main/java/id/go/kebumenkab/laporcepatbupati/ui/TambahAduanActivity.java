package id.go.kebumenkab.laporcepatbupati.ui;

import static android.view.View.GONE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import id.go.kebumenkab.laporcepatbupati.R;
//import id.go.kebumenkab.laporcepatbupati.TambahAduan;
import id.go.kebumenkab.laporcepatbupati.handler.Server;

public class TambahAduanActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_PERMISSION = 100;
    private ImageView imageView;
    private TextView deskripsiTeks;
    private Button btnTakePic, btnSend;
    private boolean imageAvail = false;
    private Uri filePath;
    private File imageFile;
    int ukuranFile;
    private String status ="";
    String deviceId="";
    String usrRealSession ="";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_aduan2);

        progressBar     = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);
        imageView = findViewById(R.id.image_view);
        btnTakePic = findViewById(R.id.btn_takpic);
        deskripsiTeks = findViewById(R.id.description);
        btnSend = findViewById(R.id.send);

        btnTakePic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                            ContextCompat.checkSelfPermission(this, MediaStore.ACTION_PICK_IMAGES) != PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, MediaStore.ACTION_PICK_IMAGES}, REQUEST_PERMISSION);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
                }
            } else {
                showImageSourceDialog();
            }
        });

        btnSend.setOnClickListener(v -> {
            if (validate()) {
                kirimAduanByFile();
            }
        });

        SharedPreferences mSettings = this.getSharedPreferences("logSession", Context.MODE_PRIVATE);
        usrRealSession = mSettings.getString("email","");
        deviceId = mSettings.getString("deviceid","");
    }

    private void showImageSourceDialog() {
        String[] options = {"Ambil Gambar", "Pilih dari Galeri"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Pilih Sumber Gambar")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            dispatchTakePictureIntent();
                            break;
                        case 1:
                            dispatchPickPictureIntent();
                            break;
                    }
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchPickPictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            Intent chosePicture = new Intent(MediaStore.ACTION_PICK_IMAGES);
            startActivityForResult(chosePicture, REQUEST_IMAGE_PICK);
        }else{
            Intent pickPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            int dataSize=0;
            File fil = null;
            if (resultCode != RESULT_CANCELED) {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE:
                        imageAvail=true;
                        if (resultCode == RESULT_OK && data != null) {
                            filePath = data.getData();
                            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(selectedImage);
//                        String imagepath = getRealPathFromURI(filePath,this);
//                        imageFile = new File(imagepath);
                            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                            Uri tempUri = getImageUri(getApplicationContext(), selectedImage);

                            // CALL THIS METHOD TO GET THE ACTUAL PATH
                            imageFile = new File(getRealPathFromURI(tempUri,this));

                        }
                        break;
                    case REQUEST_IMAGE_PICK:
                        if (resultCode == RESULT_OK && data != null) {
                            filePath = data.getData();
                            String[] filePathColumn = {MediaStore.Images.Media.DATA};

                            imageAvail = true;
                            String scheme = filePath.getScheme();
                            //                        System.out.println("Scheme type " + scheme);
                            Log.e("Scheme type", "Scheme type: " + scheme);
                            if(scheme.equals(ContentResolver.SCHEME_CONTENT))
                            {
                                try {
                                    InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(filePath);
                                    dataSize = fileInputStream.available();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //                            System.out.println("File size in bytes"+dataSize);
                                Log.e("FileSizeContent", "File size in bytes"+dataSize);
                                ukuranFile = dataSize;

                            }
                            else if(scheme.equals(ContentResolver.SCHEME_FILE))
                            {
                                String path = filePath.getPath();
                                try {
                                    fil = new File(path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //                            System.out.println("File size in bytes"+fil.length());
                                Log.e("FileSizeFile", "File size in bytes"+fil.length());
                            }


//                            String[] filePathColumn = { MediaStore.Images.Media.DATA };
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(filePath, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            //Get the column index of MediaStore.Images.Media.DATA
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            //Gets the String value in the column
                            String imgDecodableString = cursor.getString(columnIndex);

                            cursor.close();
                            // Set the Image in ImageView after decoding the String
                            imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

                            String imagepath = getRealPathFromURI(filePath,this);
                            imageFile = new File(imagepath);
//                        }

                        }
                        break;
                }
            }
        } else {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                    imageAvail = true;
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                    // Save image to a file for uploading
                    imageFile = new File(getCacheDir(), "temp_image.jpg");
                    try {
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                        imageAvail = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Bundle extras = data.getExtras();
//                    if (extras != null) {
//                        Bitmap imageBitmap = (Bitmap) extras.get("data");
//                        if (imageBitmap != null) {
//                            imageView.setImageBitmap(imageBitmap);
//                            Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
//                            imageFile = new File(getRealPathFromURI(tempUri,this));
//                        } else {
//                            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                    imageAvail = true;
                    Uri selectedImage = data.getData();
                    imageView.setImageURI(selectedImage);
                    // Save image to a file for uploading
                    try {
                        imageFile = new File(getCacheDir(), "temp_image.jpg");
                        InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        fileOutputStream.close();
                        inputStream.close();
                        imageAvail = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSourceDialog();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            cursor.close();
            return s;
        }
        cursor.close();
        return null;
    }

    private void kirimAduanByFile(){
        final ProgressDialog progressDialog = new ProgressDialog(TambahAduanActivity.this);
        File prosesGambar;
        if(ukuranFile>4096000){//jika file lebih dari 4Mb
            prosesGambar = saveBitmapToFile(imageFile);
        }else{
            prosesGambar = imageFile;
        }
        progressDialog.setMessage("Sedang mengirim aduan ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        if(imageAvail==true){
            try {
                AndroidNetworking.upload(Server.TAMBAH_ADUAN)
                        .addMultipartFile("aduan_file", prosesGambar)
                        //.addMultipartFile("aduan_file", imageFileSource)
                        //.addMultipartParameter("return_id", "true")
                        .addMultipartParameter("email", usrRealSession)
                        .addMultipartParameter("device_id", deviceId)
                        .addMultipartParameter("aduan_isi", deskripsiTeks.getText().toString())
                        .addMultipartParameter("ordinat_lat", "-")
                        .addMultipartParameter("ordinat_lng", "-")
//                        .addMultipartParameter("ordinat_address","-")

                        .setTag("uploadTest")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .setUploadProgressListener(new UploadProgressListener() {
                            @Override
                            public void onProgress(long bytesUploaded, long totalBytes) {

                                // progress.setText((bytesUploaded / totalBytes)*100 + " %");
                            }
                        })
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.e("Response", "Response: " + response.toString());

                                try {
                                    status = response.getString("status");
                                    String message = response.getString("message");
                                    Log.e("Status", "Response: " + status);
                                    // Check for error node in json
                                    if (status.equalsIgnoreCase("success")) {

                                        Log.e("Response!", response.toString());
                                        Toast.makeText(TambahAduanActivity.this, message, Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        finish();

                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    // JSON error
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                progressDialog.dismiss();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), error.getErrorDetail(), Toast.LENGTH_SHORT).show();

                                // handle error
                                if (error.getErrorCode() != 0) {
                                    // received error from server
                                    // error.getErrorCode() - the error jmlbelum from server
                                    // error.getErrorBody() - the error body from server
                                    // error.getErrorDetail() - just an error detail
                                    Log.d("UploadLaporan", "onError errorCode : " + error.getErrorCode());
                                    Log.d("UploadLaporan", "onError errorBody : " + error.getErrorBody());
                                    Log.d("UploadLaporan", "onError errorDetail : " + error.getErrorDetail());
                                    // get parsed error object (If ApiError is your class)
                                    // ApiError apiError = error.getErrorAsObject(ApiError.class);

                                } else {
                                    // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                    Log.d("UploadLaporan", "onError errorDetail : " + error.getErrorDetail());
                                }
                            }
                        });

            }catch (Exception e){
                progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Log.d("Error upload", "Laporbup file path error : "+ e.getLocalizedMessage()+"/"+e.getMessage());
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        }else{
            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Gambar tidak boleh kosong!", Toast.LENGTH_SHORT).show();
        }

    }

    public File saveBitmapToFile(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    //validasi inputan
    public boolean validate() {
        boolean valid = true;
        String teksDeskripsi = deskripsiTeks.getText().toString();
        if (teksDeskripsi.isEmpty()) {
            Toast.makeText(TambahAduanActivity.this, "Harap isi deskripsi aduan", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            deskripsiTeks.setError(null);
        }

        if (!imageAvail) {
            Toast.makeText(TambahAduanActivity.this, "Harap pilih gambar", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }
}
