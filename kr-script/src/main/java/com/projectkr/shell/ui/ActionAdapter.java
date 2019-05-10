package com.projectkr.shell.ui;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.projectkr.shell.ScriptEnvironmen;
import com.projectkr.shell.action.ActionInfo;
import com.projectkr.shell.R;
import java.util.ArrayList;

public class ActionAdapter extends BaseAdapter {
    private ArrayList<ActionInfo> actionInfos;

    public ActionAdapter(ArrayList<ActionInfo> actionInfos) {
        this.actionInfos = actionInfos;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return actionInfos != null ? actionInfos.size() : 0;
    }

    public void update(int index, ListView listview) {
        int visiblePosition = listview.getFirstVisiblePosition();
        View view = listview.getChildAt(index - visiblePosition);
        ViewHolder holder = (ViewHolder) view.getTag();
        ActionInfo actionInfo = ((ActionInfo) getItem(index));
        if (actionInfo.descPollingShell != null && !actionInfo.descPollingShell.isEmpty()) {
            actionInfo.desc = ScriptEnvironmen.executeResultRoot(listview.getContext(), actionInfo.descPollingShell);
        }
        holder.itemText.setText(actionInfo.desc);
    }

    @Override
    public Object getItem(int position) {
        return actionInfos != null ? actionInfos.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View convertView = view;
        final ViewHolder viewHolder;
        final ActionInfo item = (ActionInfo) getItem(position);
        try {
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.action_row_item, null);
                viewHolder.itemTitle = convertView.findViewById(R.id.Title);
                viewHolder.itemText = convertView.findViewById(R.id.Desc);
                viewHolder.itemSeparator = convertView.findViewById(R.id.Separator);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (isNullOrEmpty(item.desc)) {
                viewHolder.itemText.setVisibility(View.GONE);
            } else {
                viewHolder.itemText.setText(item.desc);
                viewHolder.itemText.setVisibility(View.VISIBLE);
            }

            if (isNullOrEmpty(item.title)) {
                viewHolder.itemTitle.setVisibility(View.GONE);
            } else {
                viewHolder.itemTitle.setText(item.title);
                viewHolder.itemTitle.setVisibility(View.VISIBLE);
            }

            if (isNullOrEmpty(item.separator)) {
                viewHolder.itemSeparator.setVisibility(View.GONE);
            } else {
                viewHolder.itemSeparator.setText(item.separator);
                viewHolder.itemSeparator.setVisibility(View.VISIBLE);
            }
            convertView.setTag(viewHolder);
            viewHolder.itemTitle.setTag(item);
        } catch (Exception ignored) {
        }

        return convertView;
    }

    private boolean isNullOrEmpty(String text) {
        return text == null || text.trim().equals("");
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return actionInfos == null;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    protected class ViewHolder {
        TextView itemSeparator = null;
        TextView itemTitle = null;
        TextView itemText = null;
    }
}
