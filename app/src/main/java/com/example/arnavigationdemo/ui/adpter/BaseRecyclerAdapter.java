package com.example.arnavigationdemo.ui.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2018/8/15.
 */

public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    protected String TAG = "";
    protected Context context;
    protected List<T> dataList;
    private OnItemClick<T> mOnItemClick;
    protected LayoutInflater inflater;

    protected BaseRecyclerAdapter(Context context) {
        TAG = getClass().getSimpleName();

        this.context = context;
        this.dataList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setDataList(List<T> dataList) {
        if (dataList == null) {
            this.dataList.clear();
        } else {
            this.dataList = dataList;
        }
    }

    public void addData(T data, int index) {
        if (dataList == null) {
            this.dataList = new ArrayList<>();
        }
        this.dataList.add(index, data);
    }

    public void clearData() {
        this.dataList.clear();
    }

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    protected void callOnItemClick(T data) {
        if (mOnItemClick != null)
            mOnItemClick.onClick(data);
    }

    public void addOnItemClick(OnItemClick<T> itemClick) {
        this.mOnItemClick = itemClick;
    }

    public interface OnItemClick<T> {
        void onClick(T data);
    }
}
