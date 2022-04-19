package id.go.kebumenkab.laporcepatbupati.ui.tentang;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import id.go.kebumenkab.laporcepatbupati.R;

public class TentangFragment extends Fragment {

View rootTentang;
int versionCode;
String versionName;
TextView versionTV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootTentang = inflater.inflate(R.layout.fragment_tentang, container, false);
        versionTV = rootTentang.findViewById(R.id.text_notifications);
//                    textView.setText(Server.version);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Versi aplikasi yang Anda pakai");
        getVersion();
        TextView t2 = (TextView) rootTentang.findViewById(R.id.linkprivacypolicy);
        t2.setMovementMethod(LinkMovementMethod.getInstance());
        return rootTentang;
    }

    private void getVersion(){
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            versionName = pInfo.versionName;//Version Name
            versionCode = pInfo.versionCode;//Version Code
//            versionTV         = (TextView)findViewById(R.id.version);
            versionTV.setText(versionName );

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}