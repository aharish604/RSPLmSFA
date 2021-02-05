package com.rspl.sf.msfa.collectionPlan.collectionCreate;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rspl.sf.msfa.R;

/**
 * Created by e10526 on 26-02-2018.
 */

public class RTGSCreateVH extends RecyclerView.ViewHolder {
    public TextView tvPlanNameDesc,tvActualNameDesc;
    public TextInputLayout tiCollectionRemarks1,tiCollectionAmount1,tiCollectionRemarks;
    public EditText edtCollectionAmount,edtCollectionRemarks,edtCollectionAmount1,edtCollectionRemarks1;
    public RTGSTextWatcher rtgsTextWatcher=null;
    public RTGSRemarksWatcher rtgsRemarksWatcher=null;
    public TextInputLayout tiCollectionAmount;
    public ConstraintLayout cl_achivedValue;
    public LinearLayout ll_achivedline;
    public ConstraintLayout ccCollectionRemarks,ccCollectionRemarks1,ccCollectionAmount,ccCollectionAmount1;


    public RTGSCreateVH(View view,RTGSTextWatcher rtgsTextWatcher,RTGSRemarksWatcher rtgsRemarksWatcher) {
        super(view);
        tvPlanNameDesc= (TextView) view.findViewById(R.id.tvPlanNameDesc);
        tvActualNameDesc= (TextView) view.findViewById(R.id.tvActualNameDesc);
        edtCollectionAmount = (EditText) view.findViewById(R.id.edtCollectionAmount);
        edtCollectionAmount1 = (EditText) view.findViewById(R.id.edtCollectionAmount1);
        edtCollectionRemarks = (EditText) view.findViewById(R.id.edtCollectionRemarks);
        edtCollectionRemarks1 = (EditText) view.findViewById(R.id.edtCollectionRemarks1);
        tiCollectionAmount = (TextInputLayout) view.findViewById(R.id.tiCollectionAmount);
        ccCollectionRemarks = (ConstraintLayout) view.findViewById(R.id.clCollectionRemarks);
        ccCollectionRemarks1 = (ConstraintLayout) view.findViewById(R.id.clCollectionRemarks1);
        ccCollectionAmount = (ConstraintLayout) view.findViewById(R.id.clCollectionAmount);
        ccCollectionAmount1 = (ConstraintLayout) view.findViewById(R.id.clCollectionAmount1);
        tiCollectionRemarks1 = (TextInputLayout) view.findViewById(R.id.tiCollectionRemarks1);
        tiCollectionRemarks = (TextInputLayout) view.findViewById(R.id.tiCollectionRemarks);
        tiCollectionAmount1 = (TextInputLayout) view.findViewById(R.id.tiCollectionAmount1);
        tiCollectionAmount = (TextInputLayout) view.findViewById(R.id.tiCollectionAmount);
        cl_achivedValue = (ConstraintLayout) view.findViewById(R.id.cl_achivedValue);
        ll_achivedline = (LinearLayout) view.findViewById(R.id.ll_achivedline);

        this.rtgsTextWatcher = rtgsTextWatcher;
        this.rtgsRemarksWatcher = rtgsRemarksWatcher;
        edtCollectionAmount.addTextChangedListener(rtgsTextWatcher);
//        edtCollectionRemarks.addTextChangedListener(rtgsRemarksWatcher);
        edtCollectionRemarks.addTextChangedListener(rtgsTextWatcher);
        edtCollectionAmount1.addTextChangedListener(rtgsTextWatcher);
        edtCollectionRemarks1.addTextChangedListener(rtgsTextWatcher);
    }
}
