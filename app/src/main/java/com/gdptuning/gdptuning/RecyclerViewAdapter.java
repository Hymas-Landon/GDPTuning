package com.gdptuning.gdptuning;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Code> mCode;

    public RecyclerViewAdapter(List<Code> codes) {
        mCode = codes;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.code_recycler_blueprint, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int position) {
        Code code = mCode.get(position);


        TextView mCode = viewHolder.code;
        mCode.setText(code.getCode());
    }

    @Override
    public int getItemCount() {
        return mCode.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView code;


        public ViewHolder(final View itemView) {
            super(itemView);
            code = (TextView) itemView.findViewById(R.id.code);
        }

    }
}