package com.gdptuning.gdptuning;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DiagnosticsAdapter extends RecyclerView.Adapter<DiagnosticsAdapter.DiagnosticsViewHolder> {

    ArrayList<Code> codeList;

    public DiagnosticsAdapter(Context context, ArrayList<Code> codeList) {
        this.codeList = codeList;
    }

    @Override
    public DiagnosticsAdapter.DiagnosticsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.code_recycler_blueprint, parent, false);
        DiagnosticsViewHolder viewHolder = new DiagnosticsViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiagnosticsAdapter.DiagnosticsViewHolder holder, int position) {
        Code mCode = codeList.get(position);

        String code = mCode.getCode();
        holder.text.setText(code);
    }

    @Override
    public int getItemCount() {
        return codeList.size();
    }

    public static class DiagnosticsViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public DiagnosticsViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.code);
        }
    }
}