package com.mapsoft.aftersale.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.mapsoft.aftersale.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Even_Kwok on 2017/1/2.
 */

public class KeywordAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int resource;
    private List<String> allNames = new ArrayList<>();
    private List<String> names = new ArrayList<>();


    public KeywordAdapter(Context context, int resource, List<String> list) {
        this.context = context;
        this.resource = resource;
        this.allNames = list;
    }

    @Override
    public int getCount() {
        return names == null ? 0 : names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_name.setText(names.get(position));

        return convertView;
    }


    static class ViewHolder {
        private TextView tv_name;
    }


    @Override
    public Filter getFilter() {
        return allNames == null || allNames.size() <= 0 ? null : new MyFilter();
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && !"".equals(constraint)) {
                constraint=constraint.toString().toUpperCase();
                List<String> list = new ArrayList<>();
                for (int i = 0; i < allNames.size(); i++) {
                    String s = allNames.get(i);
                    if (s.contains(constraint)) {
                        list.add(s);
                    }
                }
                names.clear();
                names.addAll(list);
                // 然后将这个新的集合数据赋给FilterResults对象
                results.values = list.toArray();
                results.count = list.size();
            } else {
                results.values = allNames;
                results.count = allNames.size();
            }


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            }
        }
    }

}
