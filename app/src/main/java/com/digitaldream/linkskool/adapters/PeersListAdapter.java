package com.digitaldream.linkskool.adapters;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitaldream.linkskool.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PeersListAdapter extends RecyclerView.Adapter<PeersListAdapter.PeersViewHolder>{
    private Context context;
    private List<WifiP2pDevice> deviceList;
    private OnDeviceClickListener onDeviceClickListener;
    public PeersListAdapter(Context context, List<WifiP2pDevice> deviceList,OnDeviceClickListener onDeviceClickListener) {
        this.context = context;
        this.deviceList = deviceList;
        this.onDeviceClickListener = onDeviceClickListener;
    }

    @NonNull
    @Override
    public PeersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.peer_list_item,parent,false);
        return new PeersViewHolder(view,onDeviceClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PeersViewHolder holder, int position) {
        holder.deviceName.setText(deviceList.get(position).deviceName);

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class PeersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName;
        OnDeviceClickListener onDeviceClickListener;
        public PeersViewHolder(@NonNull View itemView,OnDeviceClickListener onDeviceClickListener) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            this.onDeviceClickListener = onDeviceClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDeviceClickListener.onDeviceClick(getAdapterPosition());
        }
    }

    public interface OnDeviceClickListener{
        void onDeviceClick(int position);
    }
}
