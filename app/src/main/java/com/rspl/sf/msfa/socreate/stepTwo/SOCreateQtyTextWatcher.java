package com.rspl.sf.msfa.socreate.stepTwo;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import com.rspl.sf.msfa.socreate.SOItemBean;


/**
 * Created by e10769 on 20-12-2017.
 */

public class SOCreateQtyTextWatcher implements TextWatcher {
    private SOItemBean soItemBean = null;
    private RecyclerView.ViewHolder holder = null;
    private SOCreateQtyTextWatcherInterface createQtyTextWatcherInterface = null;

    public SOCreateQtyTextWatcher() {

    }

    public void updateTextWatcher(SOItemBean soItemBean, RecyclerView.ViewHolder holder, SOCreateQtyTextWatcherInterface createQtyTextWatcherInterface) {
        this.soItemBean = soItemBean;
        this.holder = holder;
        this.createQtyTextWatcherInterface = createQtyTextWatcherInterface;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (soItemBean != null) {
            soItemBean.setSoQty(s.toString());
        }
        if (createQtyTextWatcherInterface != null) {
            createQtyTextWatcherInterface.onTextChange(s.toString(), soItemBean, holder);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
       /* if (!TextUtils.isEmpty(s.toString())) {
            String text = Double.toString(Math.abs(Double.parseDouble(s.toString())));
            int integerPlaces = text.indexOf('.');
            int decimalPlaces = text.length() - integerPlaces - 1;
            int charLength = integerPlaces + 1 + decimalPlaces;
            if (charLength<=13 && decimalPlaces >=3) {
                if (holder instanceof SOMultiMaterialVH) {
                    ((SOMultiMaterialVH) holder).etQty.setFilters(new InputFilter[]{new InputFilter.LengthFilter(charLength)});
                }
            }
        }*/
    }

}
