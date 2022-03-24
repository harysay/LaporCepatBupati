package id.go.kebumenkab.laporcepatbupati.ui.history.placeholder;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import id.go.kebumenkab.laporcepatbupati.R;
import id.go.kebumenkab.laporcepatbupati.ui.history.placeholder.DataAdapter;

import java.util.List;

/**
 * Created by harysay on 2/8/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<DataAdapter> dataAdapters;
    public RecyclerViewAdapter(List<DataAdapter> getDataAdapter, Context context){
        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_history_cardview, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {
        DataAdapter dataAdapterOBJ =  dataAdapters.get(position);

        Viewholder.idAduan.setText(dataAdapterOBJ.getAduanId());
        Viewholder.statAduan.setText(dataAdapterOBJ.getAduanStatus());
        if(dataAdapterOBJ.getAduanStatus().equals("Selesai")){
            Viewholder.statAduan.setTextColor(Color.parseColor("#4ad481"));
        }else if(dataAdapterOBJ.getAduanStatus().equals("Selesai")){
            Viewholder.statAduan.setTextColor(Color.parseColor("#FFECB3"));
        }
        Viewholder.tglAduan.setText(dataAdapterOBJ.getTglAduan());
        Viewholder.isiAduan.setText(dataAdapterOBJ.getIsiAduan());

    }

    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView idAduan;
        public TextView statAduan;
        public TextView tglAduan;
        public TextView isiAduan ;

        public ViewHolder(View itemView) {

            super(itemView);

            idAduan = (TextView) itemView.findViewById(R.id.idAduan) ;
            statAduan = (TextView) itemView.findViewById(R.id.status_aduan);
            tglAduan = (TextView) itemView.findViewById(R.id.tgl_aduan);
            isiAduan = (TextView) itemView.findViewById(R.id.isi_aduan) ;

        }
    }
}