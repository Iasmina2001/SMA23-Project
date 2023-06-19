package com.sma.proiect.librarian;

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


public class GridAdapterChooseBookLibrarianActivity extends ArrayAdapter<ItemForMenu> {
    private Context context;
    private int layoutResID;
    private List<ItemForMenu> itemsForLibrarianMenu;

    GridAdapterChooseBookLibrarianActivity(Context context, int layoutResourceID, List<ItemForMenu> itemsForLibrarianMenu) {
        super(context, layoutResourceID, itemsForLibrarianMenu);
        this.context = context;
        this.layoutResID = layoutResourceID;
        this.itemsForLibrarianMenu = itemsForLibrarianMenu;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridAdapterChooseBookLibrarianActivity.ItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new GridAdapterChooseBookLibrarianActivity.ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.tText = view.findViewById(R.id.grid_text);
            itemHolder.iIcon = view.findViewById(R.id.grid_icon);

            view.setTag(itemHolder);

        } else {
            itemHolder = (GridAdapterChooseBookLibrarianActivity.ItemHolder) view.getTag();
        }

        // current values of menuItem
        final ItemForMenu menuItem = itemsForLibrarianMenu.get(position);
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