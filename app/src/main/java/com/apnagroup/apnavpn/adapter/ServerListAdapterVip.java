package com.apnagroup.apnavpn.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.apnagroup.apnavpn.activity.MainActivity;
import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.activity.SubsActivity;
import com.apnagroup.apnavpn.utils.AppData;

import java.util.ArrayList;
import java.util.Locale;

public class ServerListAdapterVip extends RecyclerView.Adapter<ServerListAdapterVip.mViewhoder> implements AppData {

    ArrayList<Country> datalist;
    private Context context;
    RemainingTraffic remainingTrafficResponse;
    Boolean isVip;

    public ServerListAdapterVip(ArrayList<Country> datalist, Boolean isVip, Context ctx) {
        this.datalist = datalist;
        this.isVip = isVip;
        this.context = ctx;
    }

    @NonNull
    @Override
    public mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.region_list_item, parent, false);
        mViewhoder mvh = new mViewhoder(item);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewhoder holder, int position) {
        remainingTrafficResponse = new RemainingTraffic();
        Country data = datalist.get(position);
        Locale locale = new Locale("", data.getCountry());
        if (position == 0) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/flag_default", null, context.getPackageName()));
            holder.app_name.setText("Select Fastest Server");
            holder.limit.setVisibility(View.GONE);
        }if (position == 1) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/au", null, context.getPackageName()));
            holder.app_name.setText("Australia");
            holder.limit.setVisibility(View.GONE);
        }if (position == 2) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ca", null, context.getPackageName()));
            holder.app_name.setText("Canada");
            holder.limit.setVisibility(View.GONE);
        }if (position == 3) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/dk", null, context.getPackageName()));
            holder.app_name.setText("Denmark");
            holder.limit.setVisibility(View.GONE);
        }if (position == 4) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/es", null, context.getPackageName()));
            holder.app_name.setText("Spain");
            holder.limit.setVisibility(View.GONE);
        }if (position == 5) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/gb", null, context.getPackageName()));
            holder.app_name.setText("United Kingdom");
            holder.limit.setVisibility(View.GONE);
        }if (position == 6) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ie", null, context.getPackageName()));
            holder.app_name.setText("Italy");
            holder.limit.setVisibility(View.GONE);
        }if (position == 7) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/mx", null, context.getPackageName()));
            holder.app_name.setText("Mexico");
            holder.limit.setVisibility(View.GONE);
        }if (position == 8) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/ro", null, context.getPackageName()));
            holder.app_name.setText("Romania");
            holder.limit.setVisibility(View.GONE);
        }if (position == 9) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/tr", null, context.getPackageName()));
            holder.app_name.setText("Turkey");
            holder.limit.setVisibility(View.GONE);
        }if (position == 10) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/us", null, context.getPackageName()));
            holder.app_name.setText("USA");
            holder.limit.setVisibility(View.GONE);
        }if (position == 11) {
            holder.flag.setImageResource(context.getResources().getIdentifier("drawable/uk", null, context.getPackageName()));
            holder.app_name.setText("UK");
            holder.limit.setVisibility(View.GONE);
        }

        holder.limit.setVisibility(View.VISIBLE);

        if (isVip) {
            holder.limit.setImageResource(R.drawable.ic_signal_full);

        }else {
            holder.limit.setImageResource(R.drawable.ic_lock);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVip){
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("c", data.getCountry());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }else {
                    Intent intent2 = new Intent(context, SubsActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent2);
                }
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
