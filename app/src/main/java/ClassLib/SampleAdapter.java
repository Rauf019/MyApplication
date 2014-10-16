package ClassLib;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.util.DynamicHeightTextView;
import com.example.my_computer.myapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SampleAdapter extends ArrayAdapter<Contact> {


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
        mBackgroundColors.add(R.color.blue);
        mBackgroundColors.add(R.color.grey);
        mBackgroundColors.add(R.color.red_light);
        mBackgroundColors.add(R.color.blue_light);
        mBackgroundColors.add(R.color.green);


    }

    public void updateReceiptsList() {

        contact1.clear();
        contact1.addAll(dataBaseHelper.getAllContacts_true());
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        try {

            ViewHolder vh;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.list_item_sample, parent, false);
                vh = new ViewHolder();
                vh.txtLineOne = (DynamicHeightTextView) convertView.findViewById(R.id.txt_line1);
                vh.contact_img = (ImageView) convertView.findViewById(R.id.contact_image);
                vh.Del_btn = (ImageView) convertView.findViewById(R.id.del);
                vh.call_icon = (ImageView) convertView.findViewById(R.id.call_icon);
                vh.msg_icon = (ImageView) convertView.findViewById(R.id.msg_icon);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            int backgroundIndex = position >= mBackgroundColors.size() ?
                    position % mBackgroundColors.size() : position;

            convertView.setBackgroundResource(mBackgroundColors.get(backgroundIndex));

            vh.txtLineOne.setHeightRatio(1.52);

            final Contact contact = contact1.get(position);

            if (contact.get_is_Msg_block() == false) {

                vh.msg_icon.setVisibility(ImageView.GONE);

            }

            if (contact.get_is_Call_block() == false) {

                vh.call_icon.setVisibility(ImageView.GONE);

            }

            if (contact.getPhoto() != null) {


                Picasso.with(getContext())
                        .load(Uri.parse(contact.getPhoto()))

                        .into(vh.contact_img);
            } else {

                Picasso.with(getContext())
                        .load(R.drawable.contact)

                        .into(vh.contact_img);

            }
            if (contact.get_Name() == null) {

                vh.txtLineOne.setText(contact.get_phoneNumber());

            } else {

                vh.txtLineOne.setText(contact.get_Name());
            }


            vh.Del_btn.setOnClickListener(new View.OnClickListener() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }


    static class ViewHolder {
        DynamicHeightTextView txtLineOne;
        TextView textView;
        ImageView Del_btn;
        ImageView call_icon;
        ImageView msg_icon;
        ImageView contact_img;
    }

}






