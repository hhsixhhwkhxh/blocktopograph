package com.mithrilmania.blocktopograph.ListDialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mithrilmania.blocktopograph.R;

import java.util.Collections;
import java.util.List;

public class ListDialog extends BottomSheetDialog {
    private List<String> items;
    private boolean isMultiSelectMode;
    private OnConfirmListener listener;
    private String title;

    public ListDialog(@NonNull Context context, List<String> items, boolean isMultiSelectMode, String title) {
        super(context);
        this.items = items;
        this.isMultiSelectMode = isMultiSelectMode;
        this.title=title;
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_template_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        EditText searchBox = findViewById(R.id.searchBox);
        Button confirmButton = findViewById(R.id.confirmButton);
        TextView titleView = findViewById(R.id.titleView);
        titleView.setText(title);

        ListDialogAdapter adapter = new ListDialogAdapter(items, isMultiSelectMode,selectedItem -> {
            if (listener != null) {
                listener.onConfirm(Collections.singletonList(selectedItem));
            }
            dismiss(); // 直接关闭弹窗
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirm(adapter.getSelectedItems());
            }
            dismiss();
        });

        if(!isMultiSelectMode){
            confirmButton.setVisibility(View.GONE);
        }
    }

    public interface OnConfirmListener {
        void onConfirm(List<String> selectedItems);
    }
}