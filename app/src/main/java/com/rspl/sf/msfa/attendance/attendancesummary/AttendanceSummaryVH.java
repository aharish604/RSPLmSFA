package com.rspl.sf.msfa.attendance.attendancesummary;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rspl.sf.msfa.R;


/**
 * Created by e10769 on 20-12-2017.
 */

 public class AttendanceSummaryVH extends RecyclerView.ViewHolder {
    public TextView tvTimeDiff, tvSPName, tvRSCode, tvStartTime, tvEndTime;

    public AttendanceSummaryVH(View viewItem) {
        super(viewItem);
        tvTimeDiff = (TextView) viewItem.findViewById(R.id.tvTimeDiff);
        tvSPName = (TextView) viewItem.findViewById(R.id.tvSPName);
        tvRSCode = (TextView) viewItem.findViewById(R.id.tvRSCode);
        tvStartTime = (TextView) viewItem.findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) viewItem.findViewById(R.id.tvEndTime);

    }
}
