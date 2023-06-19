package com.sma.proiect.reader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sma.proiect.ItemForMenu;
import com.sma.proiect.R;
import java.util.List;


public class GridAdapterChooseBookReaderActivity extends ArrayAdapter<ItemForMenu> {
    private Context context;
    private int layoutResID;
    private List<ItemForMenu> itemsForBookReaderMenu;

    GridAdapterChooseBookReaderActivity(Context context, int layoutResourceID, List<ItemForMenu> itemsForBookReaderMenu) {
        super(context, layoutResourceID, itemsForBookReaderMenu);
        this.context = context;
        this.layoutResID = layoutResourceID;
        this.itemsForBookReaderMenu = itemsForBookReaderMenu;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridAdapterChooseBookReaderActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new GridAdapterChooseBookReaderActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tText = view.findViewById(R.id.grid_text);
            itemHolder.iIcon = view.findViewById(R.id.grid_icon);

            view.setTag(itemHolder);

        } else {
            itemHolder = (GridAdapterChooseBookReaderActivity.ItemHolder) view.getTag();
        }

        // current values of menuItem
        final ItemForMenu menuItem = itemsForBookReaderMenu.get(position);
        String sText = menuItem.getText();
        int iIcon = menuItem.getImageID();

        itemHolder.tText.setText(sText);
        itemHolder.iIcon.setImageResource(iIcon);

        return view;
    }

    public static class ItemHolder {
        TextView tText;
        ImageView iIcon;
    }
}
