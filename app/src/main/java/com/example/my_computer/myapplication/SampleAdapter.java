package com.example.my_computer.myapplication;


import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.util.DynamicHeightTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ClassLib.Contact;
import ClassLib.DataBaseHelper;


public class SampleAdapter extends ArrayAdapter<Contact> {

    private static final String TAG = "SampleAdapter";
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private final ArrayList<Integer> mBackgroundColors;
    DataBaseHelper dataBaseHelper;
    private List<Contact> contact1;

    public SampleAdapter(final Context context, List<Contact> contact1) {
        super(context, R.layout.list_item_sample, contact1);
        dataBaseHelper = new DataBaseHelper(getContext());


        this.contact1 = contact1;
        mLayoutInflater = LayoutInflater.from(context);
        dataBaseHelper = new DataBaseHelper(getContext());
        mRandom = new Random();
        mBackgroundColors = new ArrayList<Integer>();
        mBackgroundColors.add(R.color.orange);
        mBackgroundColors.add(R.color.green);
        mBackgroundColors.add(R.color.blue);
        mBackgroundColors.add(R.color.yellow);
        mBackgroundColors.add(R.color.grey);
        mBackgroundColors.add(R.color.red_light);
        mBackgroundColors.add(R.color.blue_light);
        mBackgroundColors.add(R.color.green);
        mBackgroundColors.add(R.color.green_light);

    }

    public void updateReceiptsList() {

        contact1.clear();
        contact1.addAll(dataBaseHelper.getAllContacts());
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {


        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_sample, parent, false);
            vh = new ViewHolder();
            vh.txtLineOne = (DynamicHeightTextView) convertView.findViewById(R.id.txt_line1);

            //   vh.textView = (TextView) convertView.findViewById(R.id.textView);

            vh.btnGo = (ImageView) convertView.findViewById(R.id.call_icon);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        int backgroundIndex = position >= mBackgroundColors.size() ?
                position % mBackgroundColors.size() : position;

        convertView.setBackgroundResource(mBackgroundColors.get(backgroundIndex));
        vh.txtLineOne.setHeightRatio(positionHeight);
        final Contact contact = contact1.get(position);


        if (contact.get_Name().isEmpty()) {

            vh.txtLineOne.setText(contact.get_phoneNumber());

        } else {
            //   .replaceAll("[^a-zA-Z0-9]", "")
            vh.txtLineOne.setText(contact.get_Name());
        }


        vh.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                int deleteContact = dataBaseHelper.deleteContact(contact.get_phoneNumber());

                if (deleteContact != 0) {

                    Toast.makeText(getContext(), " Delete "
                            , Toast.LENGTH_SHORT).show();
                    updateReceiptsList();

                } else {
                    Toast.makeText(getContext(), " Unable to Delete "
                            , Toast.LENGTH_SHORT).show();
                }


            }
        });


        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);

        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }

    static class ViewHolder {
        DynamicHeightTextView txtLineOne;
        TextView textView;
        ImageView btnGo;
    }
}






