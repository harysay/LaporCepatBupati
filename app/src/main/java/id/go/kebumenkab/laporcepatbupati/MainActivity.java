package id.go.kebumenkab.laporcepatbupati;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import id.go.kebumenkab.laporcepatbupati.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    String deviceId;
    private static final int REQUEST_CODE_EMAIL = 1;
    public boolean isFirstStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Intro App Initialize SharedPreferences
                SharedPreferences getSharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getSharedPreferences.getBoolean("firstStart", true);

                //  Check either activity or app is open very first time or not and do action
                if (isFirstStart) {

                    //  Launch application introduction screen
//                    Intent i = new Intent(MainActivity.this, MyIntro.class);
//                    startActivity(i);
                    SharedPreferences.Editor e = getSharedPreferences.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();

//        SharedPreferences mSettings = MainActivity.this.getSharedPreferences("logSession", Context.MODE_PRIVATE);
//        String usrEmailSession = mSettings.getString("email",null);
        getViewByUser();
//        if(usrEmailSession==null){
//            try {
//                Intent intent = AccountPicker.newChooseAccountIntent(null, null,
//                        new String[] { GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE }, false, null, null, null, null);
//                startActivityForResult(intent, REQUEST_CODE_EMAIL);
//            } catch (ActivityNotFoundException e) {
//
//            }
//        }else {
//            getViewByUser();
//        }
    }

    private void getViewByUser(){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_statistik, R.id.navigation_history, R.id.navigation_about).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
//        if(accountName.equals("harypoenya91@gmail.com")){
//            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.navigation_home, R.id.navigation_statistik, R.id.navigation_history).build();
//            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//            NavigationUI.setupWithNavController(navView, navController);
//        }else {
//            navView.getMenu().removeItem(R.id.navigation_statistik);
//            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.navigation_home, R.id.navigation_history).build();
//            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//            NavigationUI.setupWithNavController(navView, navController);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EMAIL && resultCode == RESULT_OK) {
//            String emailName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            deviceId = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            getViewByUser();
            SharedPreferences sp = getSharedPreferences("logSession", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
//            editor.putString("email", emailName);
            editor.putString("deviceid",deviceId);
            editor.commit();
            //Toast.makeText(MainActivity.this, emailName, Toast.LENGTH_SHORT).show();
        }else {
//            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//            alertDialog.setTitle("Peringatan");
//            alertDialog.setMessage("Aplikasi tidak bisa digunakan karena Anda belum memilih email, silahkan restart aplikasi kemudian pilih email yang dipakai!");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
            finish();
        }
    }


}