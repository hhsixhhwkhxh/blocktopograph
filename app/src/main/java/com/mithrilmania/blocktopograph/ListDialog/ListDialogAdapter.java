package com.mithrilmania.blocktopograph.ListDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mithrilmania.blocktopograph.R;

import java.util.ArrayList;
import java.util.List;

public class ListDialogAdapter extends RecyclerView.Adapter<ListDialogAdapter.ViewHolder> {
    private List<String> items;
    private List<String> selectedItems = new ArrayList<>();
    private boolean isMultiSelectMode;
    private String selectedItem;
    private OnItemClickListener listener; // 新增监听器接口

    public ListDialogAdapter(List<String> items, boolean isMultiSelectMode,OnItemClickListener listener) {
        this.items = items;
        this.isMultiSelectMode = isMultiSelectMode;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemSelected(String selectedItem);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialog_template_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.textView.setText(item);
        holder.checkbox.setVisibility(isMultiSelectMode ? View.VISIBLE : View.GONE);
        /*holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                // 多选逻辑（保留原逻辑）
            } else {
                // 单选模式下直接触发回调并关闭弹窗
                selectedItem = item;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemSelected(selectedItem);
                }
            }
        });*/

        holder.checkbox.setChecked(selectedItems.contains(item));

        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                } else {
                    selectedItems.add(item);
                }
                notifyItemChanged(position);
            } else {
                selectedItem = item;
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemSelected(selectedItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView textView;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.textView);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}