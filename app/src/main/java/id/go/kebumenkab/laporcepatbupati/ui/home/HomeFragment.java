package id.go.kebumenkab.laporcepatbupati.ui.home;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import id.go.kebumenkab.laporcepatbupati.R;
import id.go.kebumenkab.laporcepatbupati.TambahAduan;
import id.go.kebumenkab.laporcepatbupati.handler.CheckNetwork;

public class HomeFragment extends Fragment {

    View rootHome;
    ImageView btnKeTambahAduan;
//    TextView teksHome;
    ImageView iv_wa,iv_inst,iv_twit,iv_fb,iv_header;

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
        btnKeTambahAduan = (ImageView) rootHome.findViewById(R.id.btnLaporCepat);
        iv_wa = (ImageView)rootHome.findViewById(R.id.wa);
        iv_inst = (ImageView)rootHome.findViewById(R.id.instagram);
        iv_twit = (ImageView)rootHome.findViewById(R.id.twitter);
        iv_fb = (ImageView)rootHome.findViewById(R.id.facebook);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
        btnKeTambahAduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.isInternetAvailable(getActivity()))
                {
                    Intent i = new Intent(getActivity(), TambahAduan.class);
                    startActivity(i);
                }else {
                    try {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                        alertDialog.setTitle("Tidak ada koneksi internet!");
                        alertDialog.setMessage("Cek koneksi internet Anda dan ulangi lagi");
                        alertDialog.setIcon(android.R.drawable.stat_sys_warning);
                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int n) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    } catch (Exception e) {
                        //Log.d(Constants.TAG, "Show Dialog: "+e.getMessage());
                    }
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
}