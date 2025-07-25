package com.mithrilmania.blocktopograph;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取SharedPreferences实例
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        // 创建设置项列表（实际应用中可从资源文件加载）
        List<SettingItem> items = new ArrayList<>();
        items.add(new SettingItem(
                "扩大侧边栏的滑动响应范围",
                "基于反射 缓解与全面屏手势的冲突",
                sharedPreferences.getBoolean("expand_drawer_touch_range", false),
                "expand_drawer_touch_range"
        ));
        items.add(new SettingItem(
                "自动打开侧边栏",
                "打开世界时自动带出侧边栏",
                sharedPreferences.getBoolean("auto_open_drawer", false),
                "auto_open_drawer"
        ));


        // 设置适配器
        SettingsAdapter adapter = new SettingsAdapter(items, sharedPreferences);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

class SettingItem {
    private String title;
    private String description;
    private boolean isEnabled;
    private String prefKey; // SharedPreferences存储键名

    public SettingItem(String title, String description, boolean isEnabled, String prefKey) {
        this.title = title;
        this.description = description;
        this.isEnabled = isEnabled;
        this.prefKey = prefKey;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
    public String getPrefKey() { return prefKey; }
}

class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingItem> items;
    private SharedPreferences sharedPreferences;

    public SettingsAdapter(List<SettingItem> items, SharedPreferences sharedPreferences) {
        this.items = items;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.toggle.setChecked(item.isEnabled());

        // 开关状态变化监听
        holder.toggle.setOnCheckedChangeListener(null); // 先清除旧监听器
        holder.toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 更新数据项状态
            item.setEnabled(isChecked);
            // 保存到SharedPreferences
            sharedPreferences.edit().putBoolean(item.getPrefKey(), isChecked).apply();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        SwitchCompat toggle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_description);
            toggle = itemView.findViewById(R.id.switch_toggle);
        }
    }
}