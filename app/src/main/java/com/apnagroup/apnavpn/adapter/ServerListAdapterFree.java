package com.apnagroup.apnavpn.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.apnagroup.apnavpn.activity.MainActivity;
import com.apnagroup.apnavpn.R;

import java.util.ArrayList;
import java.util.Locale;

import static com.apnagroup.apnavpn.activity.MainActivity.selectedCountry;

public class ServerListAdapterFree extends RecyclerView.Adapter<ServerListAdapterFree.mViewhoder> {

    ArrayList<Country> datalist;
    private Context context;
    RemainingTraffic remainingTrafficResponse;

    public ServerListAdapterFree(ArrayList<Country> datalist, Context ctx) {
        this.datalist = datalist;
        this.context = ctx;
    }

    @NonNull
    @Override
    public mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_list_free, parent, false);
        mViewhoder mvh = new mViewhoder(item);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewhoder holder, int position) {
        remainingTrafficResponse = new RemainingTraffic();
        Country data = datalist.get(position);
        Locale locale = new Locale("", data.getCountry());
        Log.e("strrrrrrrrrr",""+datalist);
        Log.e("strrrrrrrrrr sizeee",""+datalist.size());
        if (position == 0) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/flag_default", null, context.getPackageName()));
            holder.app_name.setText("Select Fastest Server");
            holder.limit.setVisibility(View.GONE);
        }if (position == 1) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ar", null, context.getPackageName()));
            holder.app_name.setText("Argentina");
            holder.limit.setVisibility(View.GONE);
        }if (position == 2) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ch", null, context.getPackageName()));
            holder.app_name.setText("China");
            holder.limit.setVisibility(View.GONE);
        }if (position == 3) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ch", null, context.getPackageName()));
            holder.app_name.setText("China");
            holder.limit.setVisibility(View.GONE);
        }if (position == 4) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/cz", null, context.getPackageName()));
            holder.app_name.setText("czech republic");
            holder.limit.setVisibility(View.GONE);
        }if (position == 5) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/fr", null, context.getPackageName()));
            holder.app_name.setText("France");
            holder.limit.setVisibility(View.GONE);
        }if (position == 6) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/hk", null, context.getPackageName()));
            holder.app_name.setText("Hong Kong");
            holder.limit.setVisibility(View.GONE);
        }if (position == 7) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/in", null, context.getPackageName()));
            holder.app_name.setText("India");
            holder.limit.setVisibility(View.GONE);
        }if (position == 8) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/jp", null, context.getPackageName()));
            holder.app_name.setText("Japan");
            holder.limit.setVisibility(View.GONE);
        }if (position == 9) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/nl", null, context.getPackageName()));
            holder.app_name.setText("Luxembourg");
            holder.limit.setVisibility(View.GONE);
        }if (position == 10) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/no", null, context.getPackageName()));
            holder.app_name.setText("Norway");
            holder.limit.setVisibility(View.GONE);
        }if (position == 11) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ru", null, context.getPackageName()));
            holder.app_name.setText("Russia");
            holder.limit.setVisibility(View.GONE);
        }if (position == 12) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/se", null, context.getPackageName()));
            holder.app_name.setText("Sweden");
            holder.limit.setVisibility(View.GONE);
        }
        holder.limit.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCountry = data.getCountry();
                Log.e("strrrrrrrrr",""+data);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("c", data.getCountry());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class mViewhoder extends RecyclerView.ViewHolder {
        TextView app_name;
        ImageView flag, limit;

        public mViewhoder(View itemView) {
            super(itemView);
            app_name = itemView.findViewById(R.id.region_title);
            limit = itemView.findViewById(R.id.region_limit);
            flag = itemView.findViewById(R.id.country_flag);
        }
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Country item);
    }

}
