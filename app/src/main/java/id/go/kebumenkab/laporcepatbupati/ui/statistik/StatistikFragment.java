package id.go.kebumenkab.laporcepatbupati.ui.statistik;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import id.go.kebumenkab.laporcepatbupati.R;
import id.go.kebumenkab.laporcepatbupati.handler.Server;
import id.go.kebumenkab.laporcepatbupati.ui.statistik.jsontable.model.Aduan;
import id.go.kebumenkab.laporcepatbupati.ui.statistik.jsontable.utils.SortUtil;

import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatistikFragment extends Fragment implements View.OnClickListener {

    View rootStatistik;
//    private Button buttonLoad;
    private TableLayout tableLayout;
    private ProgressDialog mProgressBar;
    private int PAGE_SIZE = 50;
    private LinearLayout buttonLayout;
    private Button[] buttons;
    private int no_of_pages;
    private ScrollView scrollView;
    private static TableRow tableRowHeader;
    private List<Aduan> aduans = new ArrayList<>();
    String status;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rootStatistik = inflater.inflate(R.layout.fragment_statistik, container, false);
//        buttonLoad = rootStatistik.findViewById(R.id.button_parse);
        tableLayout = rootStatistik.findViewById(R.id.table);
        tableLayout.setStretchAllColumns(true);
        mProgressBar = new ProgressDialog(getActivity());

        buttonLayout = rootStatistik.findViewById(R.id.btnLay);

        scrollView = rootStatistik.findViewById(R.id.scroll_view);
//        buttonLoad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getStatistik();
//            }
//        });
        getStatistik();
        return rootStatistik;
    }

    public void getStatistik(){
        mProgressBar.setCancelable(true);
        mProgressBar.setMessage("Menyiapkan Data...");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.show();
        AndroidNetworking.post(Server.URL+"bupati")
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("tag-response",response.toString());
                        if (response == null) {
                            Toast.makeText(getActivity(), "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }else {
                            try {
                                ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Jumlah Laporan (" + String.valueOf(response.length() + ")"));
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonPost = response.getJSONObject(i);
                                    String namaKategori = jsonPost.getString("kategori_nm");
                                    String namaSkpd = jsonPost.getString("akronim");
                                    String jumlAduanBelum = jsonPost.getString("jml_aduan_belum");
                                    String jumlAduanProses = jsonPost.getString("jml_aduan_proses");
                                    String jumlAduanSelesai = jsonPost.getString("jml_aduan_selesai");
                                    String persenAduanBelum = jsonPost.getString("persen_belum");
                                    String persenAduanProses = jsonPost.getString("persen_proses");
                                    String persenAduanSelesai = jsonPost.getString("persen_selesai");
                                    int total = Integer.parseInt(jumlAduanBelum) + Integer.parseInt(jumlAduanProses) + Integer.parseInt(jumlAduanSelesai);
                                    aduans.add(new Aduan(namaKategori, namaSkpd, jumlAduanBelum+" \n("+persenAduanBelum+"%)", jumlAduanProses+" \n("+persenAduanProses+"%)",jumlAduanSelesai+" \n("+persenAduanSelesai+"%)",persenAduanBelum,persenAduanProses,persenAduanSelesai,String.valueOf(total)));
                                }
                                createTableHeader();
                                createTable(aduans, 0);
                                paginate(buttonLayout, response.length(), PAGE_SIZE, aduans);
                                checkBtnBackGroud(0);
                                sortData(buttonLayout, response.length(), PAGE_SIZE, aduans);
                                mProgressBar.hide();
//                                buttonLoad.setVisibility(View.GONE);

                            } catch (JSONException e) {
                                mProgressBar.hide();
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.i("tag-error",anError.getResponse().toString());
                    }
                });
    }

    private void paginate(final LinearLayout buttonLayout, final int data_size, final int page_size, final List<Aduan> aduans) {
        no_of_pages = (data_size + page_size - 1) / page_size;
        buttons = new Button[no_of_pages];
        showPageNo(0, no_of_pages);

        for (int i = 0; i < no_of_pages; i++) {
            buttons[i] = new Button(getActivity());
            buttons[i].setBackgroundColor(R.style.Theme_LaporCepatBupati);
            buttons[i].setText(String.valueOf(i + 1));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonLayout.addView(buttons[i], lp);

            final int j = i;
            buttons[j].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    createTable(aduans, j);
                    checkBtnBackGroud(j);
                    showPageNo(j, no_of_pages);
                }
            });
        }
    }

    private void showPageNo(int j, int no_of_pages) {

        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Snackbar.make(buttonLayout, "Page " + (j + 1) + " of " + no_of_pages, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.purple_200)).show();
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                Snackbar.make(buttonLayout, "Page " + (j + 1) + " of " + no_of_pages, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.transparent)).show();
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                Snackbar.make(buttonLayout, "Page " + (j + 1) + " of " + no_of_pages, Snackbar.LENGTH_LONG).setBackgroundTint(getResources().getColor(R.color.transparent)).show();
                break;
        }

    }

    private void checkBtnBackGroud(int index) {
        for (int i = 0; i < no_of_pages; i++) {
            if (i == index) {
                buttons[index].setBackgroundResource(R.drawable.cell_shape_square_blue);
            } else {
                buttons[i].setBackground(null);
            }
        }
    }

    private void sortData(final LinearLayout buttonLayout, final int data_size, final int page_size, final List<Aduan> aduans) {
        final TextView tvKategori = (TextView) tableRowHeader.getChildAt(1);
        final TextView tvSkpd = (TextView) tableRowHeader.getChildAt(2);
        final TextView tvJmlBelum = (TextView) tableRowHeader.getChildAt(3);
        final TextView tvJmlProses = (TextView) tableRowHeader.getChildAt(4);
        final TextView tvJmlSelesai = (TextView) tableRowHeader.getChildAt(5);
//        final TextView tvPersenBlm = (TextView) tableRowHeader.getChildAt(6);
//        final TextView tvPersenProses = (TextView) tableRowHeader.getChildAt(7);
//        final TextView tvPersenSelesai = (TextView) tableRowHeader.getChildAt(8);
        final TextView tvTotal = (TextView) tableRowHeader.getChildAt(6);
        tvKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortUtil.sortByKategori(aduans);
                createTable(aduans, 0);
                checkBtnBackGroud(0);
            }
        });

        tvSkpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortUtil.sortBySkpd(aduans);
                createTable(aduans, 0);
                checkBtnBackGroud(0);
            }
        });

        tvJmlBelum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SortUtil.sortByJmlBelum(aduans);
//                createTable(aduans, 0);
//                checkBtnBackGroud(0);
                Toast.makeText(getActivity(), "Silahkan Klik Total untuk mengurutkan!", Toast.LENGTH_SHORT).show();
            }
        });

        tvJmlProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SortUtil.sortByJmlProses(aduans);
//                createTable(aduans, 0);
//                checkBtnBackGroud(0);
                Toast.makeText(getActivity(), "Silahkan Klik Total untuk mengurutkan!", Toast.LENGTH_SHORT).show();
            }
        });

        tvJmlSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SortUtil.sortByJmlSelesai(aduans);
//                createTable(aduans, 0);
//                checkBtnBackGroud(0);
                Toast.makeText(getActivity(), "Silahkan Klik Total untuk mengurutkan!", Toast.LENGTH_SHORT).show();
            }
        });

//        tvPersenBlm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SortUtil.sortByPersenBlm(aduans);
//                createTable(aduans, 0);
//                checkBtnBackGroud(0);
//            }
//        });

//        tvPersenProses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SortUtil.sortByPersenProses(aduans);
//                createTable(aduans, 0);
//                checkBtnBackGroud(0);
//            }
//        });
//
//        tvPersenSelesai.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SortUtil.sortByPersenSelesai(aduans);
//                createTable(aduans, 0);
//                checkBtnBackGroud(0);
//            }
//        });

        tvTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SortUtil.sortByTotal(aduans);
                createTable(aduans, 0);
                checkBtnBackGroud(0);
            }
        });
    }


    private void createTableRow(String nomor, String kategori, String skpd, String jmlbelum, String jmlproses, String jmlselesai, String totalAdu, int index) {
        TableRow tableRow = new TableRow(getActivity());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);
//        tableRow.setClickable(true);  //allows you to select a specific row
//
//        tableRow.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                v.setBackgroundColor(Color.GRAY);
//                System.out.println("Row clicked: " + v.getId());
//
////                //get the data you need
////                TableRow tablerow = (TableRow)v.getParent();
////                TextView sample = (TextView) tablerow.getChildAt(2);
////                String result=sample.getText().toString();
//            }
//        });

        TextView textViewNo = new TextView(getActivity());
        TextView textViewKategori = new TextView(getActivity());
        TextView textViewSkpd = new TextView(getActivity());
        TextView textViewJmlBlm = new TextView(getActivity());
        TextView textViewJmlProses = new TextView(getActivity());
        TextView textViewJmlSelesai = new TextView(getActivity());
//        TextView textViewPersenBlm = new TextView(getActivity());
//        TextView textViewPersenProses = new TextView(getActivity());
//        TextView textViewPersenSelesai = new TextView(getActivity());
        TextView textViewTotal = new TextView(getActivity());

        textViewNo.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0));
        textViewKategori.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f));
        textViewSkpd.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.5f));
        textViewJmlBlm.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0));
        textViewJmlProses.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f));
        textViewJmlSelesai.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0));
//        textViewPersenBlm.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f));
//        textViewPersenProses.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0));
//        textViewPersenSelesai.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f));
        textViewTotal.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f));

        textViewNo.setGravity(Gravity.CENTER);
        textViewKategori.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textViewSkpd.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        textViewJmlBlm.setGravity(Gravity.CENTER);
        textViewJmlProses.setGravity(Gravity.CENTER);
        textViewJmlSelesai.setGravity(Gravity.CENTER);
//        textViewPersenBlm.setGravity(Gravity.CENTER);
//        textViewPersenProses.setGravity(Gravity.CENTER);
//        textViewPersenSelesai.setGravity(Gravity.CENTER);
        textViewTotal.setGravity(Gravity.CENTER);

        textViewSkpd.setMaxLines(3);
        textViewKategori.setMaxLines(2);
        textViewJmlProses.setMaxLines(2);
        textViewJmlBlm.setMaxLines(2);
        textViewJmlSelesai.setMaxLines(2);
//        textViewPersenBlm.setMaxLines(2);
//        textViewPersenProses.setMaxLines(2);
//        textViewPersenSelesai.setMaxLines(2);
        textViewTotal.setMaxLines(2);

        textViewNo.setPadding(5, 15, 5, 15);
        textViewKategori.setPadding(5, 15, 5, 15);
        textViewSkpd.setPadding(5, 15, 5, 15);
        textViewJmlBlm.setPadding(5, 15, 5, 15);
        textViewJmlProses.setPadding(5, 15, 5, 15);
        textViewJmlSelesai.setPadding(5, 15, 5, 15);
//        textViewPersenBlm.setPadding(5, 15, 5, 15);
//        textViewPersenProses.setPadding(5, 15, 5, 15);
//        textViewPersenSelesai.setPadding(5, 15, 5, 15);
        textViewTotal.setPadding(5, 15, 5, 15);

        textViewNo.setText(nomor);
        textViewKategori.setText(kategori);
        textViewSkpd.setText(skpd);
        textViewJmlBlm.setText(jmlbelum);
        textViewJmlProses.setText(jmlproses);
        textViewJmlSelesai.setText(jmlselesai);
//        textViewPersenBlm.setText(persenblm);
//        textViewPersenProses.setText(persenproses);
//        textViewPersenSelesai.setText(persenselesai);
        textViewTotal.setText(totalAdu);
        

        textViewNo.setBackgroundResource(R.drawable.cell_shape_white);
        textViewKategori.setBackgroundResource(R.drawable.cell_shape_grey);
        textViewSkpd.setBackgroundResource(R.drawable.cell_shape_white);
        textViewJmlBlm.setBackgroundResource(R.drawable.cell_shape_grey);
        textViewJmlProses.setBackgroundResource(R.drawable.cell_shape_white);
        textViewJmlSelesai.setBackgroundResource(R.drawable.cell_shape_grey);
//        textViewPersenBlm.setBackgroundResource(R.drawable.cell_shape_white);
//        textViewPersenProses.setBackgroundResource(R.drawable.cell_shape_grey);
//        textViewPersenSelesai.setBackgroundResource(R.drawable.cell_shape_white);
        textViewTotal.setBackgroundResource(R.drawable.cell_shape_white);

        tableRow.addView(textViewNo);
        tableRow.addView(textViewKategori);
        tableRow.addView(textViewSkpd);
        tableRow.addView(textViewJmlBlm);
        tableRow.addView(textViewJmlProses);
        tableRow.addView(textViewJmlSelesai);
//        tableRow.addView(textViewPersenBlm);
//        tableRow.addView(textViewPersenProses);
//        tableRow.addView(textViewPersenSelesai);
        tableRow.addView(textViewTotal);

        if (index == -1) {
            tableRowHeader = tableRow;
            textViewNo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
            textViewKategori.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
            textViewSkpd.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
            textViewJmlBlm.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
            textViewJmlProses.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
            textViewJmlSelesai.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
//            textViewPersenBlm.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
//            textViewPersenProses.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
//            textViewPersenSelesai.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));
            textViewTotal.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.font_size_small));

            textViewKategori.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
            textViewSkpd.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
//            textViewJmlBlm.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
//            textViewJmlProses.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
//            textViewJmlSelesai.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
//            textViewPersenBlm.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
//            textViewPersenProses.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
//            textViewPersenSelesai.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);
            textViewTotal.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_sort_by_alpha_black, 0);

            textViewNo.setBackgroundResource(R.drawable.cell_shape_blue);
            textViewKategori.setBackgroundResource(R.drawable.cell_shape_blue);
            textViewSkpd.setBackgroundResource(R.drawable.cell_shape_blue);
            textViewJmlBlm.setBackgroundResource(R.drawable.cell_shape_blue);
            textViewJmlProses.setBackgroundResource(R.drawable.cell_shape_blue);
            textViewJmlSelesai.setBackgroundResource(R.drawable.cell_shape_blue);
//            textViewPersenBlm.setBackgroundResource(R.drawable.cell_shape_blue);
//            textViewPersenProses.setBackgroundResource(R.drawable.cell_shape_blue);
//            textViewPersenSelesai.setBackgroundResource(R.drawable.cell_shape_blue);
            textViewTotal.setBackgroundResource(R.drawable.cell_shape_blue);
        }
        tableLayout.addView(tableRow, index + 1);
    }

    private void createTableHeader() {
        tableLayout.removeAllViews();
        createTableRow("No", "Kategori", "SKPD", "Belum", "Proses","Selesai","Total", -1);
    }

    private void createTable(List<Aduan> aduans, int page) {
        tableLayout.removeAllViews();
        tableLayout.addView(tableRowHeader);
        // data rows
        for (int i = 0, j = page * 50; j < aduans.size() && i < 50; i++, j++) {
            createTableRow(
                    String.valueOf(j + 1),
                    aduans.get(j).getnamaKategori(),
                    aduans.get(j).getnamaSkpd(),
                    aduans.get(j).getjumlAduanBelum(),
                    aduans.get(j).getjumlAduanProses(),
                    aduans.get(j).getJumlAduanSelesai(),
                    aduans.get(j).getTotal(),
                    i
            );
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        TextView tv = v.findViewById(id);
        if (null != tv) {
            Log.i("onClick", "Clicked on row :: " + id);
            Toast.makeText(getActivity(), "Clicked on row :: " + id + ", Text :: " + tv.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}