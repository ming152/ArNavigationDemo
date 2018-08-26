package com.example.arnavigationdemo.ui.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.example.arnavigationdemo.R;

/**
 * Created by ming on 2018/8/15.
 */

public class PoiSearchAdapter extends BaseRecyclerAdapter<PoiInfo, PoiSearchAdapter.ViewHolder> {
    private OnPoiClickListener listener;

    public PoiSearchAdapter(Context context) {
        super(context);
    }

    @Override
    public PoiSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_rv_baidu_poi, parent, false));
    }

    @Override
    public void onBindViewHolder(final PoiSearchAdapter.ViewHolder holder, int position) {
        final PoiInfo poiInfo = dataList.get(position);
        holder.poiName.setText(poiInfo.name);
        holder.poiAddress.setText(poiInfo.address);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(holder.getAdapterPosition(), poiInfo.location);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView poiName, poiAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            poiName = itemView.findViewById(R.id.tv_poi_name);
            poiAddress = itemView.findViewById(R.id.tv_poi_address);
        }
    }

    public interface OnPoiClickListener{
        void onClick(int position, LatLng location);
    }

    public void setOnItemClickListener(OnPoiClickListener listener) {
        this.listener = listener;
    }
}
