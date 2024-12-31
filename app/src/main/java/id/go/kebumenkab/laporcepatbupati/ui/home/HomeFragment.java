package id.go.kebumenkab.laporcepatbupati.ui.home;

import static android.content.Context.ACCOUNT_SERVICE;
//import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import id.go.kebumenkab.laporcepatbupati.R;
//import id.go.kebumenkab.laporcepatbupati.TambahAduan;
import id.go.kebumenkab.laporcepatbupati.handler.CheckNetwork;
import id.go.kebumenkab.laporcepatbupati.ui.TambahAduanActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.widget.Toast;
import android.provider.Settings;

public class HomeFragment extends Fragment {

    View rootHome;
    ImageView btnKeTambahAduan;
    TextView tekskanal;
    ImageView iv_wa,iv_inst,iv_twit,iv_fb,iv_header;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    saveUserData();
                } else {
                    // Jika izin ditolak, tampilkan pesan ke pengguna
                    Toast.makeText(requireContext(),"Jika ingin membuat laporan, izinkan aplikasi untuk hak akses yang diminta", Toast.LENGTH_LONG).show();
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootHome = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences mSettings = getActivity().getSharedPreferences("logSession", Context.MODE_PRIVATE);
        String usrRealSession = mSettings.getString("email","");
//        String strEmail = getArguments().getString("email");
//        String strDeviceId = getArguments().getString("device_id");
        iv_header = (ImageView)rootHome.findViewById(R.id.imageLapoCepat);
//        iv_header.setImageResource(R.drawable.headerlaporcepat);
//        teksHome = (TextView) rootHome.findViewById(R.id.text_home);
//        teksHome.setText(usrRealSession);
        String text = "atau melalui kanal di bawah ini:";
        tekskanal = (TextView) rootHome.findViewById(R.id.text_kanal);
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StrokeSpan(Color.BLACK, Color.RED, 1), 1, text.length(), Spanned.SPAN_COMPOSING);
        tekskanal.setText(spannableString);

        btnKeTambahAduan = (ImageView) rootHome.findViewById(R.id.btnLaporCepat);
        iv_wa = (ImageView)rootHome.findViewById(R.id.wa);
        iv_inst = (ImageView)rootHome.findViewById(R.id.instagram);
        iv_twit = (ImageView)rootHome.findViewById(R.id.twitter);
        iv_fb = (ImageView)rootHome.findViewById(R.id.facebook);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
        btnKeTambahAduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mengecek apakah pengguna sudah memberikan izin
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

                    // Mengecek apakah koneksi internet tersedia
                    if (CheckNetwork.isInternetAvailable(getActivity())) {
                        // Jika izin dan koneksi internet tersedia, buka TambahAduanActivity
                        Intent i = new Intent(getActivity(), TambahAduanActivity.class);
                        startActivity(i);
                    } else {
                        try {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Tidak ada koneksi internet!");
                            alertDialog.setMessage("Cek koneksi internet Anda dan ulangi lagi");
                            alertDialog.setIcon(android.R.drawable.stat_sys_warning);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int n) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        } catch (Exception e) {
                            // Log.d(Constants.TAG, "Show Dialog: "+e.getMessage());
                        }
                    }
                } else {
                    // Jika izin belum diberikan, tampilkan pesan
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Akses Ditolak");
                    alertDialog.setMessage("Jika ingin membuat laporan, izinkan aplikasi untuk hak akses yang diminta.");
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        iv_wa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=628112634556";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        iv_inst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://instagram.com/_u/pemkabkebumen");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/pemkabkebumen")));
                }
            }
        });
        iv_twit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                try {
                    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=pemkab_kebumen"));
//                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=pemkab_kebumen"));
                } catch (Exception e) {
//                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/pemkab_kebumen"));
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/pemkab_kebumen"));
                }

                getActivity().startActivity(intent);
            }
        });
        iv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String facebookURL = "https://www.facebook.com/pemkab.kebumen";
                String facebookURL = "https://m.facebook.com/profile.php?id=100063707950029";

                //format the facebook URL to URI if app is installed
                try {
                    ApplicationInfo applicationInfo
                            = getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                    if (applicationInfo.enabled) {
                        facebookURL = "fb://facewebmodal/f?href=" + facebookURL;
                    }
                } catch (PackageManager.NameNotFoundException ignored) {}

                //open facebook account
                Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(facebookURL));
                startActivity(intent);

            }
        });
        return rootHome;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Mengecek izin saat view sudah dibuat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            } else {
                saveUserData();
            }
        } else {
            saveUserData();
        }
    }

    private void saveUserData() {
        String email = getEmailAddress();
        String deviceId = getDeviceId();

        if (email != null && deviceId != null) {
            saveToSharedPreferences(email, deviceId);
            //Toast.makeText(requireContext(), "Data saved to SharedPreferences", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Gagal memperoleh email dan ID laporan!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getEmailAddress() {
        AccountManager accountManager = (AccountManager) requireContext().getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length > 0) {
            return accounts[0].name; // Mengambil email pertama yang terdaftar
        }
        return null;
    }

    private String getDeviceId() {
        return Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void saveToSharedPreferences(String email, String deviceId) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("deviceid", deviceId);
        editor.apply();
    }

    // Inner class for custom StrokeSpan
    public static class StrokeSpan extends CharacterStyle implements UpdateAppearance {
        private final int strokeColor;
        private final int textColor;
        private final float strokeWidth;

        public StrokeSpan(int textColor, int strokeColor, float strokeWidth) {
            this.textColor = textColor;
            this.strokeColor = strokeColor;
            this.strokeWidth = strokeWidth;
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setStrokeWidth(strokeWidth);
            textPaint.setColor(strokeColor);
            textPaint.setAntiAlias(true);
            textPaint.setMaskFilter(new BlurMaskFilter(9.5f, BlurMaskFilter.Blur.OUTER));

            textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint.setColor(textColor);
        }
    }
}