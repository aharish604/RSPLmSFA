package com.rspl.sf.msfa.visit;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.store.OfflineManager;


/**
 * Created by e10526 on 12-19-2016.
 */
public class MerchandisingDetailsActivity extends AppCompatActivity {
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrRemarks = "",
            mStrEtag = "", mStrMerchReviewGUID = "", mStrMerchReviewTypeDesc = "", mStrImagePath = "",
            mStrImgData = "";
    TextView retId, retName, tv_remrks, tv_mer_type;
    ImageView imageViewFront;
    byte[] imageByteArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
//        ActionBarView.initActionBarView(this, true,getString(R.string.title_merchindising_list));
        setContentView(R.layout.activity_merchandising_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrMerchReviewGUID = bundleExtras.getString(Constants.MerchReviewGUID);
            mStrMerchReviewTypeDesc = bundleExtras.getString(Constants.MerchReviewTypeDesc);
            mStrRemarks = bundleExtras.getString(Constants.Remarks);
            mStrEtag = bundleExtras.getString(Constants.Etag);
            mStrImgData = bundleExtras.getString(Constants.Image);

            imageByteArray = bundleExtras.getByteArray(Constants.ImageByteArray);
            mStrImagePath = bundleExtras.getString(Constants.ImagePath);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_merchindising_list), 0);

        initUI();
        setValuesToUI();
        setImage();
    }


    /*InitializesUI*/
    void initUI() {
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
        imageViewFront = (ImageView) findViewById(R.id.iv_image_front);
        tv_remrks = (TextView) findViewById(R.id.tv_remrks);
        tv_mer_type = (TextView) findViewById(R.id.tv_mer_type);
    }

    private void setValuesToUI() {
        retId.setText(mStrBundleRetUID);
        retName.setText(mStrBundleRetName);
        tv_remrks.setText(mStrRemarks);
        tv_mer_type.setText(mStrMerchReviewTypeDesc);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    //TODO set image to image view based on etag
    private void setImage() {
        if (mStrEtag.equalsIgnoreCase("")) {
            setImageToImageView();
        } else {
            getImageDetails();
            setImageToImageView();
        }
    }

    private void setImageToImageView() {


        try {

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            imageViewFront.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        imageViewFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Dialog dialog = new Dialog(MerchandisingDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.img_expand);
                // set the custom dialog components - text, image and
                // button
                ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);

                image.setImageBitmap(BitmapFactory.decodeByteArray(imageByteArray, 0,
                        imageByteArray.length));
                dialog.show();

            }
        });
//        }
    }


    // TODO get device merchandising image from offline store
    private void getImageDetails() {
        try {
            imageByteArray = OfflineManager.getImageList(mStrImagePath);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }

}
