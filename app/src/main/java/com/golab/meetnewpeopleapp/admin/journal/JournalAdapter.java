package com.golab.meetnewpeopleapp.admin.journal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.golab.meetnewpeopleapp.R;
import java.util.List;

public class JournalAdapter extends  RecyclerView.Adapter<JournalViewHolder>{
    private List<JournalObject>  reportsList;
    private Context context;

    public JournalAdapter(List<JournalObject> reportsList, Context context) {
        this.reportsList = reportsList;
        this.context = context;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal, null, false);
        RecyclerView.LayoutParams lp= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        JournalViewHolder jvh = new JournalViewHolder(layoutView);
        return jvh;
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        holder.reportedBy.setText(reportsList.get(position).getReportedBy());
        holder.reason.setText(reportsList.get(position).getReportReason());
        holder.date.setText(reportsList.get(position).getReportTime());
    }

    @Override
    public int getItemCount() {
                return reportsList.size();
    }
}


