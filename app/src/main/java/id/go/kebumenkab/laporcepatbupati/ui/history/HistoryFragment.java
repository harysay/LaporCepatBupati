package id.go.kebumenkab.laporcepatbupati.ui.history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.go.kebumenkab.laporcepatbupati.MainActivity;
import id.go.kebumenkab.laporcepatbupati.R;
import id.go.kebumenkab.laporcepatbupati.handler.Server;
import id.go.kebumenkab.laporcepatbupati.ui.history.placeholder.DataAdapter;
import id.go.kebumenkab.laporcepatbupati.ui.history.placeholder.RecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 */
public class HistoryFragment extends Fragment {
//    List<DataAdapter> ListOfdataAdapter;
//    ProgressBar progressBarPegawaiGrid;
    View v;
    ProgressBar progressBarAduanSaya;
    TextView tungguLoadAduan;
    List<DataAdapter> ListOfdataAdapter;
    private String TAG ;
    //    ConfigApp alamatIP = new ConfigApp();
//    String IP_add = alamatIP.getIP();
    private static String HTTP_JSON_URL = Server.URL + "aduan?email=";

    String aduanStatus = "aduan_st";
    String tgl_aduan = "aduan_mdd";
    String isiAduan = "aduan_isi";
    String aduanId = "aduan_id";

    JsonArrayRequest RequestOfJSonArray ;
    JsonObjectRequest jsonObjReq;

    RequestQueue requestQueue ;
    RecyclerView recyclerView;
    TextView teksStatus;
    int RecyclerViewItemPosition ;

    RecyclerView.LayoutManager layoutManagerOfrecyclerView;

    RecyclerView.Adapter recyclerViewadapter;

//    ArrayList<String> ImageTitleNameArrayListForClick;
//    ArrayList<String> IdPNSArrayListForClick;
//    ArrayList<String> NipBaruListForClick;
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v =inflater.inflate(R.layout.fragment_history, container, false);
        //session = new SessionManager(getActivity().getApplication());
//        getActivity().setTitle("Aduan Saya");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Jumlah Aduan Anda (0)");
//        ImageTitleNameArrayListForClick = new ArrayList<>();
//        IdPNSArrayListForClick = new ArrayList<>();
//        NipBaruListForClick = new ArrayList<>();

        ListOfdataAdapter = new ArrayList<>();
        progressBarAduanSaya = (ProgressBar) v.findViewById(R.id.progressBarAduanSaya);
        tungguLoadAduan = (TextView) v.findViewById(R.id.tungguLoadAduan);
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerviewAduanSaya);

        recyclerView.setHasFixedSize(true);

        layoutManagerOfrecyclerView = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManagerOfrecyclerView);

        JSON_HTTP_CALL();

        // Implementing Click Listener on RecyclerView.
//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//
//            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
//
//                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {
//
//                    return true;
//                }
//
//            });
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
//
//                view = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
//
//                if(view != null && gestureDetector.onTouchEvent(motionEvent)) {
//
//                    //Getting RecyclerView Clicked Item value.
//                    RecyclerViewItemPosition = Recyclerview.getChildAdapterPosition(view);
//
//                    // Showing RecyclerView Clicked Item value using Toast.
//                    //Toast.makeText(getActivity(), ImageTitleNameArrayListForClick.get(RecyclerViewItemPosition), Toast.LENGTH_LONG).show();
//
//                    Intent in = new Intent(getActivity(), VerifikasiDetailActivity.class);
//                    in.putExtra("id_pns", IdPNSArrayListForClick.get(RecyclerViewItemPosition));
//                    startActivity(in);
//                }
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
        return v;
    }

    public void JSON_HTTP_CALL(){
        SharedPreferences mSettings = getActivity().getSharedPreferences("logSession", Context.MODE_PRIVATE);
        String sessionEmail = mSettings.getString("email",null);
        recyclerView.setVisibility(View.GONE);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,HTTP_JSON_URL+sessionEmail, null, //"kabumian@gmail.com"
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        JSONArray jsonAr ;
                        try {
                            if(response.getString("status").equals("success")) {
                                jsonAr = response.getJSONArray("data");
                                ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Jumlah Aduan Anda (" + String.valueOf(jsonAr.length() + ")"));
                                ParseJSonResponse(jsonAr);
                            }else{
                                progressBarAduanSaya.setVisibility(View.GONE);
                                tungguLoadAduan.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Anda tidak memiliki aduan!", Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.e("ERROR", "Error occurred ", error);
            }
        });

        requestQueue = Volley.newRequestQueue(getActivity());

        requestQueue.add(jsonObjReq);
    }

    public void ParseJSonResponse(JSONArray array){

        for(int i = 0; i<array.length(); i++) {

            DataAdapter GetDataAdapter2 = new DataAdapter();

            JSONObject json = null;
            try {
                String statusGet="Status tidak terdefinisi";
                json = array.getJSONObject(i);
                if(json.getString(aduanStatus).equals("0")){
                    statusGet = "Belum";
                }else if(json.getString(aduanStatus).equals("1")){
                    statusGet = "Proses";
                }else if(json.getString(aduanStatus).equals("2")){
                    statusGet = "Selesai";
                }

                GetDataAdapter2.setAduanStatus(statusGet);
                GetDataAdapter2.setTglAduan(json.getString(tgl_aduan));
                GetDataAdapter2.setIsiAduan(json.getString(isiAduan));
                GetDataAdapter2.setAduanId(json.getString(aduanId));

            } catch (JSONException e) {

                e.printStackTrace();
            }
            ListOfdataAdapter.add(GetDataAdapter2);
        }

        recyclerViewadapter = new RecyclerViewAdapter(ListOfdataAdapter, getActivity());
        progressBarAduanSaya.setVisibility(View.GONE);
        tungguLoadAduan.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(recyclerViewadapter);
    }


}