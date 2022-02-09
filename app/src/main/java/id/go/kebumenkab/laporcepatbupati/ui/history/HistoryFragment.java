package id.go.kebumenkab.laporcepatbupati.ui.history;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import id.go.kebumenkab.laporcepatbupati.R;
import id.go.kebumenkab.laporcepatbupati.handler.Server;

public class HistoryFragment extends Fragment {

View rootHistory;
int versionCode;
String versionName;
TextView versionTV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootHistory = inflater.inflate(R.layout.fragment_history, container, false);
        versionTV = rootHistory.findViewById(R.id.text_notifications);
//                    textView.setText(Server.version);
        getVersion();
        return rootHistory;
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