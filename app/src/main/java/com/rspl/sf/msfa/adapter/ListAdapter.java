package com.rspl.sf.msfa.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.mbo.SchemeBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by e10854 on 22-09-2017.
 */
public class ListAdapter extends BaseAdapter   implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<SchemeBean> data=new ArrayList();
    private static LayoutInflater inflater=null;
  //  public Resources res;
   // ListModel tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public ListAdapter(Activity a, ArrayList d) {

        /********** Take passed values **********/
        activity = a;
        data=d;
      //  res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView text;
        public TextView text1;
        public TextView text2;
        public ImageView image;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.schemes, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.tv_scheme_name);
            holder.text1=(TextView)vi.findViewById(R.id.tv_scheme_validfrom);
            holder.text2=(TextView)vi.findViewById(R.id.tv_scheme_validto);
            holder.image=(ImageView)vi.findViewById(R.id.image_arrow);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();
/*
        if(data.size()<=0)
        {
            holder.text.setText("No Data");

        }
        else
        {*/
            /***** Get each Model object from Arraylist ********/
    /*        tempValues=null;
            tempValues = ( ListModel ) data.get( position );*/

            /************  Set Model values in Holder elements ***********/

            holder.text.setText(data.get(position).getSchemeName());
            holder.text1.setText(data.get(position).getValidFrom());
            holder.text2.setText(data.get(position).getValidTo());

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
       // }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
        try {
            openPDF();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            try {
                openPDF();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
/*
            CustomListViewAndroidExample sct = (CustomListViewAndroidExample)activity;

            *//****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****//*

            sct.onItemClick(mPosition);*/
        }
    }



    public void readPdf(){




        File dir = Environment.getExternalStorageDirectory();
        File file = new File("android.resource://com.arteriatech.sf/raw/"+"monsoon_dhamaka_q2_fy18_od.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {

            // Toast.makeText(RequestPage.this, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
        }





    }

    public void openPDF() throws FileNotFoundException {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        try {
            copyFile(activity.getResources().openRawResource(R.raw.monsoon_dhamaka_q2_fy18_od)
                    , new FileOutputStream(new File(baseDir, "/monsoon_dhamaka_q2_fy18_od.pdf")));
        } catch (IOException e) {
            e.printStackTrace();
        }
//file:///storage/emulated/0/custom.pdf
        File pdfFile = new File(baseDir, "/monsoon_dhamaka_q2_fy18_od.pdf");
//        Uri path = Uri.fromFile(pdfFile);
       /* Uri path = Uri.parse("content://"+pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setDataAndType(path, "application/pdf");
        try {
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Intent intent = Intent.createChooser(target, "Open File");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {

            // Toast.makeText(RequestPage.this, "You may not have a proper app for viewing this content ", Toast.LENGTH_LONG).show();
        }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


}