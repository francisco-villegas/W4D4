package com.example.francisco.w4d4week.view.main_activity;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.francisco.w4d4week.R;
import com.example.francisco.w4d4week.model.LocationAddress;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by FRANCISCO on 10/08/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private static final String TAG = "RandomListAdapter";
    ArrayList<LocationAddress> locationaddressList = new ArrayList<>();
    Context context;

    public ListAdapter(ArrayList<LocationAddress> locationaddressList) {
        this.locationaddressList = locationaddressList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.tvlatitude)
        TextView tvlatitude;

        @Nullable
        @BindView(R.id.tvlongitude)
        TextView tvlongitude;

        @Nullable
        @BindView(R.id.tvaddress)
        TextView tvaddress;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    private int lastPosition = -1;
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(position > lastPosition){
            //Animation animation = AnimationUtils
        }

        Log.d(TAG, "onBindViewHolder: ");
        final LocationAddress locationAddress = locationaddressList.get(position);
        holder.tvlatitude.setText(locationAddress.getLatitude());
        holder.tvlongitude.setText(locationAddress.getLongitude());
        holder.tvaddress.setText(locationAddress.getAddress());
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: "+locationaddressList.size());
        return locationaddressList.size();
    }
}
