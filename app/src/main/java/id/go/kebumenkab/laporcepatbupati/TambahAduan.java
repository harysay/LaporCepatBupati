package id.go.kebumenkab.laporcepatbupati;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.go.kebumenkab.laporcepatbupati.handler.Server;
import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.GONE;
public class TambahAduan extends AppCompatActivity implements View.OnClickListener {

    private EditText deskripsi;
    private String teksJudul, teksDeskripsi, teksKategori,teksLokasi;
    private Button sendBtn,photoButton;
    private ImageView imageView;
    private TextView tvPhoto;
    private String status ="";
    private ProgressBar progressBar;
    //    private String wargaEmail,wargaDeviceId;
    private boolean imageAvail = false;
    private int bitmap_size = 100;
    private String latitude, longitude;
    //    private LinearLayout lokasi;
    private Uri filePath;
    private ByteArrayOutputStream byteArrayOutputStreamObject ;
    private File imageFile;
    String deviceId,emailId,usrRealSession;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_EMAIL = 1;
    int ukuranFile;

    // IMAGE HANDLING METHODS ------------------------------------------------------------------------
    int CAMERA      = 0;
    int GALLERY     = 1;

//    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_aduan);
        initViews();
    }
    private void initViews(){
        progressBar     = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);
        deskripsi       = (EditText)findViewById(R.id.description);
        tvPhoto         = (TextView)findViewById(R.id.tv_photo);
        photoButton     = (Button)findViewById(R.id.btn_takpic);
        imageView       =  (ImageView)findViewById(R.id.image_view);
        sendBtn         = (Button)findViewById(R.id.send);
        sendBtn.setOnClickListener(this);
        SharedPreferences mSettings = TambahAduan.this.getSharedPreferences("logSession", Context.MODE_PRIVATE);
        usrRealSession = mSettings.getString("email","Pengguna");
        deviceId = mSettings.getString("deviceid","anonim");
        latitude = "-";
        longitude = "-";
//        findViewById(R.id.btn_takpic).setOnClickListener(view -> showBottomSheetDialog());
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkAndRequestPermissions(TambahAduan.this)){
//                    chooseImage(TambahAduan.this);
                    showBottomSheetDialog();
                }
            }
        });

    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){

        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit" }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals("Take Photo")){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
                else if(optionsMenu[i].equals("Choose from Gallery")){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                }
                else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        bottomSheetDialog.findViewById(R.id.takePhoto).setOnClickListener(v -> {
//            openCamera();
            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, 0);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.chooseFromGallery).setOnClickListener(v -> {
            Intent chosePicture = new Intent(MediaStore.ACTION_PICK_IMAGES);
            startActivityForResult(chosePicture, 1);
//            openGallery();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.exit).setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }


    // function to check permission
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
        }else {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES);
        }
//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(context, listPermissionsNeeded
//                            .toArray(new String[listPermissionsNeeded.size()]),
//                    REQUEST_ID_MULTIPLE_PERMISSIONS);
//            return false;
//        }
        return true;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA);
        }
    }

    String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private Account getAccount(AccountManager accountManager) {
//        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }


    @Override
    public void onClick(View view) {
        if(view == sendBtn){
            if (validate()) {
                kirimAduanByFile();
            }
        }
    }

    // OPEN GALLERY
    public void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, GALLERY);
    }

    //kompres image
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
        String teksDeskripsi = deskripsi.getText().toString();
        if (teksDeskripsi.isEmpty()) {
            Toast.makeText(TambahAduan.this, "Harap isi deskripsi aduan", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            deskripsi.setError(null);
        }

        if (!imageAvail) {
            Toast.makeText(TambahAduan.this, "Harap pilih gambar", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    //kirim
    private void kirimAduanByFile(){
        final ProgressDialog progressDialog = new ProgressDialog(TambahAduan.this);
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
                        .addMultipartParameter("aduan_isi", deskripsi.getText().toString())
                        .addMultipartParameter("ordinat_lat", latitude)
                        .addMultipartParameter("ordinat_lng", longitude)
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
                                        Toast.makeText(TambahAduan.this, message, Toast.LENGTH_LONG).show();
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
            AndroidNetworking.upload(Server.TAMBAH_ADUAN)
                    // .addMultipartParameter("return_id", "true")
                    .addMultipartParameter("email", usrRealSession)
                    .addMultipartParameter("device_id", deviceId)
                    .addMultipartParameter("aduan_isi", deskripsi.getText().toString())
                    .addMultipartParameter("ordinat_lat", latitude)
                    .addMultipartParameter("ordinat_lng", longitude)
//                    .addMultipartParameter("ordinat_address","-")

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
                                String message1 = response.getString("message");
                                Log.e("Status", "Response: " + status);
                                // Check for error node in json
                                if (status.equalsIgnoreCase("success")) {
                                    Toast.makeText(TambahAduan.this, message1, Toast.LENGTH_LONG).show();
                                    Log.e("Response!", response.toString());
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
                                Log.d("elseUploadLaporan", "onError errorCode : " + error.getErrorCode());
                                Log.d("elseUploadLaporan", "onError errorBody : " + error.getErrorBody());
                                Log.d("elseUploadLaporan", "onError errorDetail : " + error.getErrorDetail());
                                // get parsed error object (If ApiError is your class)
                                // ApiError apiError = error.getErrorAsObject(ApiError.class);

                            } else {
                                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                                Log.d("elseElseUploadLaporan", "onError errorDetail : " + error.getErrorDetail());
                            }
                        }
                    });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int dataSize=0;
        File fil = null;

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
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
                case 1:
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
    }

    //    dipanggil dari onActivityResult
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
    public String getRealPathFromURICamera(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}