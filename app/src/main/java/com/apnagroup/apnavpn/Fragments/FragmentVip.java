package com.apnagroup.apnavpn.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.apnagroup.apnavpn.adapter.ServerListAdapterVip;
import com.apnagroup.apnavpn.R;
import com.apnagroup.apnavpn.utils.AppData;

import java.util.ArrayList;
import java.util.List;

public class FragmentVip extends Fragment implements ServerListAdapterVip.RegionListAdapterInterface, AppData {
    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private ArrayList<Country> countryArrayList;
    private RegionChooserInterface regionChooserInterface;

    SharedPreferences prefs;
    Boolean isVip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countryArrayList = new ArrayList<>();

        prefs = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        isVip = prefs.getBoolean(IS_VIP, false);

        adapter = new ServerListAdapterVip(countryArrayList, isVip, getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        isVip = prefs.getBoolean(IS_VIP, false);

        loadServers();
    }

    private void loadServers() {
        HydraSdk.countries(new Callback<List<Country>>() {
            @Override
            public void success(List<Country> countries) {
                if(countries.size()==1){
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));
                    countryArrayList.add(countries.get(0));

                }
                for (int i = 0; i < countries.size(); i++) {
                    if (countries.get(i).getServers() > 0) {
                        countryArrayList.add(countries.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(HydraException e) {

            }
        });
    }

    @Override
    public void onCountrySelected(Country item) {
        regionChooserInterface.onRegionSelected(item);
        // dismiss();
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }

}