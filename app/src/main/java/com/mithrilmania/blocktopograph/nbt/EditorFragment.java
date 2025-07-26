package com.mithrilmania.blocktopograph.nbt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mithrilmania.blocktopograph.ListDialog.ListDialog;
import com.mithrilmania.blocktopograph.Log;
import com.mithrilmania.blocktopograph.R;
import com.mithrilmania.blocktopograph.WorldActivity;
import com.mithrilmania.blocktopograph.WorldActivityInterface;
import com.mithrilmania.blocktopograph.nbt.convert.NBTConstants;
import com.mithrilmania.blocktopograph.nbt.tags.ByteArrayTag;
import com.mithrilmania.blocktopograph.nbt.tags.ByteTag;
import com.mithrilmania.blocktopograph.nbt.tags.CompoundTag;
import com.mithrilmania.blocktopograph.nbt.tags.DoubleTag;
import com.mithrilmania.blocktopograph.nbt.tags.FloatTag;
import com.mithrilmania.blocktopograph.nbt.tags.IntArrayTag;
import com.mithrilmania.blocktopograph.nbt.tags.IntTag;
import com.mithrilmania.blocktopograph.nbt.tags.ListTag;
import com.mithrilmania.blocktopograph.nbt.tags.LongTag;
import com.mithrilmania.blocktopograph.nbt.tags.ShortArrayTag;
import com.mithrilmania.blocktopograph.nbt.tags.ShortTag;
import com.mithrilmania.blocktopograph.nbt.tags.StringTag;
import com.mithrilmania.blocktopograph.nbt.tags.Tag;
import com.mithrilmania.blocktopograph.util.JsonToNBTConverter;
import com.mithrilmania.blocktopograph.util.NBTToJsonConverter;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EditorFragment extends Fragment {

    /**
     * TODO:
     * <p>
     * - The onSomethingChanged listeners should start Asynchronous tasks
     * when directly modifying NBT.
     * <p>
     * - This editor should be refactored into parts, it grew too large.
     * <p>
     * - The functions lack documentation. Add it. Ask @mithrilmania for now...
     */

    private EditableNBT nbt;

    private EditorFragmentListener editorFragmentListener;
    public interface EditorFragmentListener{
        public void setDrawerEnabled(boolean flag);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        editorFragmentListener = (WorldActivity) context;
    }

    public void setNbt(@NonNull EditableNBT nbt) {
        this.nbt = nbt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (nbt == null) {
            Log.e(this, "No NBT data provided");
            if (getActivity() == null) return null;
            //What are you doing!
            TextView textView = new TextView(getActivity());
            textView.setText("Cannot load data. Close me please.");
            return textView;
        }

        final View rootView = inflater.inflate(R.layout.nbt_editor, container, false);


        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("NBT Editor");
        toolbar.setTitleTextColor(Color.BLACK);

        // edit functionality
        // ================================

        TreeNode superRoot = TreeNode.root();
        TreeNode root = new TreeNode(nbt);
        superRoot.addChild(root);
        root.setExpanded(true);
        root.setSelectable(false);


        final Activity activity = getActivity();

        root.setViewHolder(new RootNodeHolder(activity));

        for (Tag tag : nbt.getTags()) {
            if (tag != null)
                root.addChild(new TreeNode(new ChainTag(null, tag)).setViewHolder(new NBTNodeHolder(nbt, activity)));
        }

        FrameLayout frame = rootView.findViewById(R.id.nbt_editor_frame);

        //final AndroidTreeView tree = new AndroidTreeView(getActivity(), superRoot);
        //tree.setUse2dScroll(true);

        //final View treeView = tree.getView();

        //treeView.setScrollContainer(true);

        /*tree.setDefaultNodeLongClickListener(new TreeNode.TreeNodeLongClickListener() {
            @Override
            public boolean onLongClick(final TreeNode node, final Object value) {

                Log.d(this, "NBT editor: Long click!");


                //root tag has nbt as value
                if (value instanceof EditableNBT) {

                    if (!nbt.enableRootModifications) {
                        Toast.makeText(activity, R.string.cannot_edit_root_NBT_tag, Toast.LENGTH_LONG).show();
                        return true;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle(R.string.root_NBT_options)
                            .setItems(getRootNBTEditOptions(), new DialogInterface.OnClickListener() {

                                private void showMsg(int msg) {
                                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                                }

                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        RootNBTEditOption option = RootNBTEditOption.values()[which];

                                        switch (option) {
                                            case ADD_NBT_TAG: {
                                                final EditText nameText = new EditText(activity);
                                                nameText.setHint(R.string.hint_tag_name_here);

                                                //NBT tag type spinner
                                                final Spinner spinner = new Spinner(activity);
                                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                                                        activity, android.R.layout.simple_spinner_item, NBTConstants.NBTType.editorOptions_asString);

                                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                spinner.setAdapter(spinnerArrayAdapter);

                                                //wrap editText and spinner in linear layout
                                                LinearLayout linearLayout = new LinearLayout(activity);
                                                linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                linearLayout.setLayoutParams(
                                                        new LinearLayout.LayoutParams(
                                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                Gravity.BOTTOM));
                                                linearLayout.addView(nameText);
                                                linearLayout.addView(spinner);

                                                //wrap layout in alert
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                alert.setTitle(R.string.create_nbt_tag);
                                                alert.setView(linearLayout);

                                                //alert can create a new tag
                                                alert.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {

                                                        //new tag name
                                                        Editable newNameEditable = nameText.getText();
                                                        String newName = (newNameEditable == null || newNameEditable.toString().equals("")) ? null : newNameEditable.toString();

                                                        //new tag type
                                                        int spinnerIndex = spinner.getSelectedItemPosition();
                                                        NBTConstants.NBTType nbtType = NBTConstants.NBTType.editorOptions_asType[spinnerIndex];

                                                        //create new tag
                                                        Tag newTag = NBTConstants.NBTType.newInstance(newName, nbtType);

                                                        //add tag to nbt
                                                        nbt.addRootTag(newTag);
                                                        tree.addNode(node, new TreeNode(new ChainTag(null, newTag)).setViewHolder(new NBTNodeHolder(nbt, activity)));

                                                        nbt.setModified();

                                                    }
                                                });

                                                //or alert is cancelled
                                                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        Log.d(this, "NBT tag creation cancelled");
                                                    }
                                                });

                                                alert.show();

                                                return;
                                            }
                                            case PASTE_SUB_TAG: {
                                                if (clipboard == null) {
                                                    showMsg(R.string.clipboard_is_empty);
                                                    return;
                                                }

                                                Tag copy = clipboard.getDeepCopy();
                                                nbt.addRootTag(copy);
                                                tree.addNode(node, new TreeNode(new ChainTag(null, copy)).setViewHolder(new NBTNodeHolder(nbt, activity)));
                                                nbt.setModified();

                                                return;
                                            }
                                            case REMOVE_ALL_TAGS: {

                                                //wrap layout in alert
                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                alert.setTitle(R.string.confirm_delete_all_nbt_tags);

                                                //alert can create a new tag
                                                alert.setPositiveButton(R.string.delete_loud, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {

                                                        List<TreeNode> children = new ArrayList<>(node.getChildren());
                                                        //new tag name
                                                        for (TreeNode child : children) {
                                                            tree.removeNode(child);
                                                            Object childValue = child.getValue();
                                                            if (childValue instanceof ChainTag)
                                                                nbt.removeRootTag(((ChainTag) childValue).self);
                                                        }
                                                        nbt.setModified();
                                                    }

                                                });

                                                //or alert is cancelled
                                                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        Log.d(this, "NBT tag creation cancelled");
                                                    }
                                                });

                                                alert.show();

                                                break;
                                            }
                                            default: {
                                                Log.d(this, "User clicked unknown NBTEditOption! " + option.name());
                                            }
                                        }
                                    } catch (Exception e) {
                                        showMsg(R.string.failed_to_do_NBT_change);
                                    }
                                }

                            });
                    builder.show();
                    return true;


                } else if (value instanceof ChainTag) {
                    //other tags have a chain-tag as value


                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                    builder.setTitle(R.string.nbt_tag_options)
                            .setItems(getNBTEditOptions(), new DialogInterface.OnClickListener() {

                                private void showMsg(int msg) {
                                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                                }

                                @SuppressWarnings("unchecked")
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        NBTEditOption editOption = NBTEditOption.values()[which];

                                        final Tag parent = ((ChainTag) value).parent;
                                        final Tag self = ((ChainTag) value).self;

                                        if (self == null) return;//WTF?

                                        if (editOption == null) return;//WTF?

                                        switch (editOption) {
                                            case CANCEL: {
                                                return;
                                            }
                                            case COPY: {
                                                clipboard = self.getDeepCopy();
                                                return;
                                            }
                                            case PASTE_OVERWRITE: {
                                                if (clipboard == null) {
                                                    showMsg(R.string.clipboard_is_empty);
                                                    return;
                                                }

                                                if (parent == null) {
                                                    //it is one of the children of the root node
                                                    nbt.removeRootTag(self);
                                                    Tag copy = clipboard.getDeepCopy();
                                                    nbt.addRootTag(copy);
                                                    tree.addNode(node.getParent(), new TreeNode(new ChainTag(null, copy)).setViewHolder(new NBTNodeHolder(nbt, activity)));
                                                    tree.removeNode(node);
                                                    nbt.setModified();
                                                    return;
                                                } else {

                                                    ArrayList<Tag> content;
                                                    switch (parent.getType()) {
                                                        case LIST: {
                                                            content = ((ListTag) parent).getValue();
                                                            break;
                                                        }
                                                        case COMPOUND: {
                                                            content = ((CompoundTag) parent).getValue();
                                                            if (checkKeyCollision(clipboard.getName(), content)) {
                                                                showMsg(R.string.clipboard_key_exists_in_compound);
                                                                return;
                                                            }
                                                            break;
                                                        }
                                                        default: {
                                                            showMsg(R.string.error_cannot_overwrite_tag_unknow_parent_type);
                                                            return;
                                                        }
                                                    }
                                                    if (content != null) {
                                                        content.remove(self);
                                                        Tag copy = clipboard.getDeepCopy();
                                                        content.add(copy);
                                                        tree.addNode(node.getParent(), new TreeNode(new ChainTag(parent, copy)).setViewHolder(new NBTNodeHolder(nbt, activity)));
                                                        tree.removeNode(node);
                                                        nbt.setModified();
                                                        return;
                                                    } else
                                                        showMsg(R.string.error_cannot_overwrite_in_empty_parent);
                                                    return;
                                                }
                                            }
                                            case PASTE_SUBTAG: {
                                                if (clipboard == null) {
                                                    showMsg(R.string.clipboard_is_empty);
                                                    return;
                                                }

                                                ArrayList<Tag> content;
                                                switch (self.getType()) {
                                                    case LIST: {
                                                        content = ((ListTag) self).getValue();
                                                        break;
                                                    }
                                                    case COMPOUND: {
                                                        content = ((CompoundTag) self).getValue();
                                                        if (checkKeyCollision(clipboard.getName(), content)) {
                                                            showMsg(R.string.clipboard_key_exists_in_compound);
                                                            return;
                                                        }
                                                        break;
                                                    }
                                                    default: {
                                                        showMsg(R.string.error_cannot_paste_as_sub_unknown_parent_type);
                                                        return;
                                                    }
                                                }
                                                if (content == null) {
                                                    content = new ArrayList<>();
                                                    self.setValue(content);
                                                }

                                                Tag copy = clipboard.getDeepCopy();
                                                content.add(copy);
                                                tree.addNode(node, new TreeNode(new ChainTag(self, copy)).setViewHolder(new NBTNodeHolder(nbt, activity)));
                                                nbt.setModified();

                                                return;
                                            }
                                            case DELETE: {
                                                if (parent == null) {
                                                    //it is one of the children of the root node
                                                    tree.removeNode(node);
                                                    nbt.removeRootTag(self);
                                                    nbt.setModified();
                                                    return;
                                                }

                                                ArrayList<Tag> content;
                                                switch (parent.getType()) {
                                                    case LIST: {
                                                        content = ((ListTag) parent).getValue();
                                                        break;
                                                    }
                                                    case COMPOUND: {
                                                        content = ((CompoundTag) parent).getValue();
                                                        break;
                                                    }
                                                    default: {
                                                        showMsg(R.string.error_cannot_overwrite_tag_unknow_parent_type);
                                                        return;
                                                    }
                                                }
                                                if (content != null) {
                                                    content.remove(self);
                                                    tree.removeNode(node);
                                                    nbt.setModified();
                                                } else
                                                    showMsg(R.string.error_cannot_remove_from_empty_list);
                                                return;
                                            }
                                            case RENAME: {
                                                final EditText edittext = new EditText(activity);
                                                edittext.setHint(R.string.hint_tag_name_here);

                                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                alert.setTitle(R.string.rename_nbt_tag);

                                                alert.setView(edittext);

                                                alert.setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        Editable newNameEditable = edittext.getText();
                                                        String newName = (newNameEditable == null || newNameEditable.toString().equals("")) ? null : newNameEditable.toString();

                                                        if (parent instanceof CompoundTag
                                                                && checkKeyCollision(newName, ((CompoundTag) parent).getValue())) {
                                                            showMsg(R.string.error_parent_already_contains_child_with_same_key);
                                                            return;
                                                        }

                                                        self.setName(newName);

                                                        //refresh view with new TreeNode
                                                        tree.addNode(node.getParent(), new TreeNode(new ChainTag(parent, self)).setViewHolder(new NBTNodeHolder(nbt, activity)));
                                                        tree.removeNode(node);

                                                        nbt.setModified();
                                                    }
                                                });

                                                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        Log.d(this, "Cancelled rename NBT tag");
                                                    }
                                                });

                                                alert.show();

                                                return;
                                            }
                                            case ADD_SUBTAG: {
                                                switch (self.getType()) {
                                                    case LIST:
                                                    case COMPOUND: {
                                                        final EditText nameText = new EditText(activity);
                                                        nameText.setHint(R.string.hint_tag_name_here);

                                                        //NBT tag type spinner
                                                        final Spinner spinner = new Spinner(activity);
                                                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                                                                activity, android.R.layout.simple_spinner_item, NBTConstants.NBTType.editorOptions_asString);

                                                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                        spinner.setAdapter(spinnerArrayAdapter);

                                                        //wrap editText and spinner in linear layout
                                                        LinearLayout linearLayout = new LinearLayout(activity);
                                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                                        linearLayout.setLayoutParams(
                                                                new LinearLayout.LayoutParams(
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                        Gravity.BOTTOM));
                                                        linearLayout.addView(nameText);
                                                        linearLayout.addView(spinner);

                                                        //wrap layout in alert
                                                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                                                        alert.setTitle(R.string.create_nbt_tag);
                                                        alert.setView(linearLayout);

                                                        //alert can create a new tag
                                                        alert.setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {

                                                                //new tag name
                                                                Editable newNameEditable = nameText.getText();
                                                                String newName = (newNameEditable == null || newNameEditable.toString().equals("")) ? null : newNameEditable.toString();

                                                                //new tag type
                                                                int spinnerIndex = spinner.getSelectedItemPosition();
                                                                NBTConstants.NBTType nbtType = NBTConstants.NBTType.editorOptions_asType[spinnerIndex];


                                                                ArrayList<Tag> content;
                                                                if (self instanceof CompoundTag) {

                                                                    content = ((CompoundTag) self).getValue();

                                                                    if (checkKeyCollision(newName, content)) {
                                                                        showMsg(R.string.error_key_already_exists_in_compound);
                                                                        return;
                                                                    }
                                                                } else if (self instanceof ListTag) {
                                                                    content = ((ListTag) self).getValue();
                                                                } else return;//WTF?

                                                                if (content == null) {
                                                                    content = new ArrayList<>();
                                                                    self.setValue(content);
                                                                }

                                                                //create new tag
                                                                Tag newTag = NBTConstants.NBTType.newInstance(newName, nbtType);

                                                                //add tag to nbt
                                                                if (newTag != null) {
                                                                    content.add(newTag);
                                                                    tree.addNode(node, new TreeNode(new ChainTag(self, newTag)).setViewHolder(new NBTNodeHolder(nbt, activity)));

                                                                    nbt.setModified();
                                                                }

                                                            }
                                                        });

                                                        //or alert is cancelled
                                                        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                                Log.d(this, "NBT tag creation cancelled");
                                                            }
                                                        });

                                                        alert.show();

                                                        return;

                                                    }
                                                    default: {
                                                        showMsg(R.string.sub_tags_only_add_compound_list);
                                                        return;
                                                    }
                                                }
                                            }
                                            default: {
                                                Log.d(this, "User clicked unknown NBTEditOption! " + editOption.name());
                                            }

                                        }
                                    } catch (Exception e) {
                                        showMsg(R.string.failed_to_do_NBT_change);
                                    }

                                }
                            });

                    builder.show();
                    return true;
                }
                return false;
            }
        });*/
        //frame.addView(treeView, 0);
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //recyclerView.setAdapter(new NBTAdapter());
        frame.addView(recyclerView, 0);
        //后面要改
        List<Tag> rootTagList = new ArrayList<>();
        for (Tag tag : nbt.getTags()) {
            if (tag == null){continue;}
                rootTagList.add(tag);

        }
        try {
            NBTAdapter nbtAdapter = new NBTAdapter(getContext(),rootTagList,nbt);
            recyclerView.setAdapter(nbtAdapter);
        } catch (Exception e) {
            //Log.e(TAG,"错误",e);
            //printStackTrace(e);
        }



        // save functionality
        // ================================

        FloatingActionButton fabSaveNBT = rootView.findViewById(R.id.fab_save_nbt);
        assert fabSaveNBT != null;
        fabSaveNBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!nbt.isModified()) {
                    Snackbar.make(view, R.string.no_data_changed_nothing_to_save, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.nbt_editor)
                            .setMessage(R.string.confirm_nbt_editor_changes)
                            .setIcon(R.drawable.ic_action_save_b)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Snackbar.make(view, "Saving NBT data...", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            if (nbt.save()) {
                                                //nbt is not "modified" anymore, in respect to the new saved data
                                                nbt.modified = false;

                                                Snackbar.make(view, "Saved NBT data!", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                                Log.logFirebaseEvent(activity, Log.CustomFirebaseEvent.NBT_EDITOR_SAVE);
                                            } else {
                                                Snackbar.make(view, "Error: failed to save the NBT data.", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                        }
                                    })
                            .setNegativeButton(android.R.string.no, null).show();
                }

            }
        });

        return rootView;
    }

    public static class RootNodeHolder extends TreeNode.BaseNodeViewHolder<EditableNBT> {


        public RootNodeHolder(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode node, EditableNBT value) {

            final LayoutInflater inflater = LayoutInflater.from(context);

            final View tagView = inflater.inflate(R.layout.tag_root_layout, null, false);
            TextView tagName = tagView.findViewById(R.id.tag_name);
            tagName.setText(value.getRootTitle());

            return tagView;
        }

        @Override
        public void toggle(boolean active) {
        }

        @Override
        public int getContainerStyle() {
            return R.style.TreeNodeStyleCustomRoot;
        }
    }


    public static class NBTNodeHolder extends TreeNode.BaseNodeViewHolder<ChainTag> {

        private final EditableNBT nbt;

        public NBTNodeHolder(EditableNBT nbt, Context context) {
            super(context);
            this.nbt = nbt;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View createNodeView(TreeNode node, final ChainTag chain) {

            if (chain == null) return null;
            Tag tag = chain.self;
            if (tag == null) return null;

            final LayoutInflater inflater = LayoutInflater.from(context);

            int layoutID;

            switch (tag.getType()) {
                case COMPOUND: {
                    List<Tag> value = ((CompoundTag) tag).getValue();
                    if (value != null) {
                        for (Tag child : value) {
                            node.addChild(new TreeNode(new ChainTag(tag, child)).setViewHolder(new NBTNodeHolder(nbt, context)));
                        }
                    }

                    layoutID = R.layout.tag_compound_layout;
                    break;
                }
                case LIST: {
                    List<Tag> value = ((ListTag) tag).getValue();

                    if (value != null) {
                        for (Tag child : value) {
                            node.addChild(new TreeNode(new ChainTag(tag, child)).setViewHolder(new NBTNodeHolder(nbt, context)));
                        }
                    }

                    layoutID = R.layout.tag_list_layout;
                    break;
                }
                case BYTE_ARRAY: {
                    layoutID = R.layout.tag_default_layout;
                    break;
                }
                case BYTE: {
                    String name = tag.getName();
                    if (name == null) name = "";
                    else name = name.toLowerCase();

                    //TODO differentiate boolean tags from byte tags better
                    if (name.startsWith("has") || name.startsWith("is")) {
                        layoutID = R.layout.tag_boolean_layout;
                    } else {
                        layoutID = R.layout.tag_byte_layout;
                    }
                    break;
                }
                case SHORT:
                    layoutID = R.layout.tag_short_layout;
                    break;
                case INT:
                    layoutID = R.layout.tag_int_layout;
                    break;
                case LONG:
                    layoutID = R.layout.tag_long_layout;
                    break;
                case FLOAT:
                    layoutID = R.layout.tag_float_layout;
                    break;
                case DOUBLE:
                    layoutID = R.layout.tag_double_layout;
                    break;
                case STRING:
                    layoutID = R.layout.tag_string_layout;
                    break;
                default:
                    layoutID = R.layout.tag_default_layout;
                    break;
            }

            final View tagView = inflater.inflate(layoutID, null, false);
            TextView tagName = tagView.findViewById(R.id.tag_name);
            tagName.setText(tag.getName());

            switch (layoutID) {
                case R.layout.tag_boolean_layout: {
                    final CheckBox checkBox = tagView.findViewById(R.id.checkBox);
                    final ByteTag byteTag = (ByteTag) tag;
                    checkBox.setChecked(byteTag.getValue() == (byte) 1);
                    checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                        /**
                         * Called when the checked state of a compound button has changed.
                         *
                         * @param buttonView The compound button view whose state has changed.
                         * @param isChecked  The new checked state of buttonView.
                         */
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            byteTag.setValue(isChecked ? (byte) 1 : (byte) 0);
                            nbt.setModified();
                        }
                    });
                    break;
                }
                case R.layout.tag_byte_layout: {
                    final EditText editText = tagView.findViewById(R.id.byteField);
                    final ByteTag byteTag = (ByteTag) tag;
                    //parse the byte as an unsigned byte
                    editText.setText("" + (((int) byteTag.getValue()) & 0xFF));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                int value = Integer.parseInt(sValue);
                                if (value < 0 || value > 0xff)
                                    throw new NumberFormatException("No unsigned byte.");
                                byteTag.setValue((byte) value);
                                nbt.setModified();
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                    break;
                }
                case R.layout.tag_short_layout: {
                    final EditText editText = tagView.findViewById(R.id.shortField);
                    final ShortTag shortTag = (ShortTag) tag;
                    editText.setText(shortTag.getValue().toString());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                shortTag.setValue(Short.valueOf(sValue));
                                nbt.setModified();
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                    break;
                }
                case R.layout.tag_int_layout: {
                    final EditText editText = tagView.findViewById(R.id.intField);
                    final IntTag intTag = (IntTag) tag;
                    editText.setText(intTag.getValue().toString());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                intTag.setValue(Integer.valueOf(sValue));
                                nbt.setModified();
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                    break;
                }
                case R.layout.tag_long_layout: {
                    final EditText editText = tagView.findViewById(R.id.longField);
                    final LongTag longTag = (LongTag) tag;
                    editText.setText(longTag.getValue().toString());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                longTag.setValue(Long.valueOf(sValue));
                                nbt.setModified();
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                    break;
                }
                case R.layout.tag_float_layout: {
                    final EditText editText = tagView.findViewById(R.id.floatField);
                    final FloatTag floatTag = (FloatTag) tag;
                    editText.setText(floatTag.getValue().toString());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                floatTag.setValue(Float.valueOf(sValue));
                                nbt.setModified();
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                    break;
                }
                case R.layout.tag_double_layout: {
                    final EditText editText = tagView.findViewById(R.id.doubleField);
                    final DoubleTag doubleTag = (DoubleTag) tag;
                    editText.setText(doubleTag.getValue().toString());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String sValue = s.toString();
                            try {
                                doubleTag.setValue(Double.valueOf(sValue));
                                nbt.setModified();
                            } catch (NumberFormatException e) {
                                editText.setError(String.format(context.getString(R.string.x_is_invalid), sValue));
                            }
                        }
                    });
                    break;
                }
                case R.layout.tag_string_layout: {
                    final EditText editText = tagView.findViewById(R.id.stringField);
                    final StringTag stringTag = (StringTag) tag;
                    editText.setText(stringTag.getValue());
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            nbt.setModified();
                            stringTag.setValue(s.toString());
                        }
                    });
                    break;
                }
                default:
                    break;

            }

            return tagView;
        }

        @Override
        public void toggle(boolean active) {
        }

        @Override
        public int getContainerStyle() {
            return R.style.TreeNodeStyleCustom;
        }

    }

    public enum NBTEditOption {

        CANCEL(R.string.edit_cancel),
        COPY(R.string.edit_copy),
        PASTE_OVERWRITE(R.string.edit_paste_overwrite),
        PASTE_SUBTAG(R.string.edit_paste_sub_tag),
        DELETE(R.string.edit_delete),
        RENAME(R.string.edit_rename),
        ADD_SUBTAG(R.string.edit_add_sub_tag);

        public final int stringId;

        NBTEditOption(int stringId) {
            this.stringId = stringId;
        }
    }

    public String[] getNBTEditOptions() {
        NBTEditOption[] values = NBTEditOption.values();
        int len = values.length;
        String[] options = new String[len];
        for (int i = 0; i < len; i++) {
            options[i] = getString(values[i].stringId);
        }
        return options;
    }

    public enum RootNBTEditOption {

        ADD_NBT_TAG(R.string.edit_root_add),
        PASTE_SUB_TAG(R.string.edit_root_paste_sub_tag),
        REMOVE_ALL_TAGS(R.string.edit_root_remove_all);

        public final int stringId;

        RootNBTEditOption(int stringId) {
            this.stringId = stringId;
        }

    }

    public String[] getRootNBTEditOptions() {
        RootNBTEditOption[] values = RootNBTEditOption.values();
        int len = values.length;
        String[] options = new String[len];
        for (int i = 0; i < len; i++) {
            options[i] = getString(values[i].stringId);
        }
        return options;
    }


    public static Tag clipboard;


    //returns true if there is a tag in content with a name equals to key.
    boolean checkKeyCollision(String key, List<Tag> content) {
        if (content == null || content.isEmpty()) return false;
        if (key == null) key = "";
        String tagName;
        for (Tag tag : content) {
            tagName = tag.getName();
            if (tagName == null) tagName = "";
            if (tagName.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static class ChainTag {

        public Tag parent, self;

        public ChainTag(Tag parent, @NonNull Tag self) {
            this.parent = parent;
            this.self = self;
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        getActivity().setTitle(R.string.nbt_editor);

        Bundle bundle = new Bundle();
        bundle.putString("title", nbt.getRootTitle());

        editorFragmentListener.setDrawerEnabled(false);

        Log.logFirebaseEvent(getActivity(), Log.CustomFirebaseEvent.NBT_EDITOR_OPEN, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((WorldActivityInterface) getActivity()).showActionBar();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editorFragmentListener.setDrawerEnabled(true);
    }
}


class NBTAdapter extends RecyclerView.Adapter<NBTAdapter.ViewHolder> {
    private Context context;
    private List<NBTItem> itemList;
    //Tag rootTag;
    private EditableNBT nbt;


    public NBTAdapter(Context context, List<Tag> tagList,EditableNBT nbt) {
        this.context = context;
        this.itemList = new ArrayList<>();
        this.nbt = nbt;

        for(Tag tag:tagList){
            this.itemList.add(new NBTItem(tag));
        }
        //this.rootTag=rootTag;
        if(itemList!=null&&itemList.size()==1){
            NBTItem item = itemList.get(0);
            if(isCompositeTag(item.getType())){
                expand(item,0);
                item.setState(true);
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nbt_tag, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nbt.modified=true;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NBTItem item = itemList.get(position);
        holder.imageView.setImageResource(item.getImageResId());
        holder.textView.setText(item.getText());
        //holder.editText.setHint(item.getHint());

        switch (item.getType()) {

            case INT:
                holder.imageView.setImageResource(R.drawable.ic_tag_int);
                holder.editText.setVisibility(View.VISIBLE);
                break;
            case BYTE:
                holder.imageView.setImageResource(R.drawable.ic_tag_byte);
                holder.editText.setVisibility(View.VISIBLE);
                break;
            case LONG:
                holder.imageView.setImageResource(R.drawable.ic_tag_long);
                holder.editText.setVisibility(View.VISIBLE);
                break;
            case FLOAT:
                holder.imageView.setImageResource(R.drawable.ic_tag_float);
                holder.editText.setVisibility(View.VISIBLE);
                break;
            case SHORT:
                holder.imageView.setImageResource(R.drawable.ic_tag_short);
                holder.editText.setVisibility(View.VISIBLE);
                break;
            case DOUBLE:
                holder.imageView.setImageResource(R.drawable.ic_tag_double);
                holder.editText.setVisibility(View.VISIBLE);
                break;
            case STRING:
                holder.imageView.setImageResource(R.drawable.ic_tag_string);
                holder.editText.setVisibility(View.VISIBLE);
                break;



            case LIST:
                holder.imageView.setImageResource(R.drawable.ic_tag_list);
                holder.editText.setVisibility(View.GONE);
                break;
            case END:
            case COMPOUND:
                holder.imageView.setImageResource(R.drawable.ic_tag_compound);
                holder.editText.setVisibility(View.GONE);
                break;

            case INT_ARRAY:
                holder.imageView.setImageResource(R.drawable.ic_tag_int_array);
                holder.editText.setVisibility(View.GONE);
                break;

            case BYTE_ARRAY:
                holder.imageView.setImageResource(R.drawable.ic_tag_byte_array);
                holder.editText.setVisibility(View.GONE);
                break;

            case SHORT_ARRAY:
                holder.imageView.setImageResource(R.drawable.ic_tag_default);
                holder.editText.setVisibility(View.GONE);
                break;


            //break;

            default:
                throw new RuntimeException("wtf????");
        }
        switch (item.getType()) {
            case LIST:
            case COMPOUND:
                holder.itemView.setOnClickListener(v -> {
                    //展开/收起复合标签的子项

                    if(!item.isOpen()){
                        //展开
                        //不能使用position 会在错误的位置插入子项
                        expand(item,holder.getAdapterPosition());

                        item.setState(true);
                    }else{
                        //收起

                        retract(item,position,true);

                        item.setState(false);
                    }

                });
                break;
            default:
                holder.itemView.setOnClickListener(null);
        }

        holder.editText.removeTextChangedListener(holder.textWatcher);
        if(!isCompositeTag(item.getType())){

            holder.editText.setText(item.getTag().getValue().toString());
            holder.editText.addTextChangedListener(holder.textWatcher);

        }

        holder.editText.setTag(item);


        holder.itemView.setOnLongClickListener(v ->{
            List<String> items = Arrays.asList("复制", "粘贴（覆盖该标签）", "粘贴（作为该标签的子标签）", "删除", "重命名","添加子标签");
            ListDialog dialog = new ListDialog(context, items, false,item.getText()+" NBT标签选项");
            dialog.setOnConfirmListener(selectedItems -> {
                if (selectedItems.contains("复制")) {
                    // 复制为JSON
                    String json = NBTToJsonConverter.convertTagToJson(item.getTag());
                    copyToClipboard(json);
                    //Toast.makeText(context, "已复制为JSON", Toast.LENGTH_SHORT).show();
                    Snackbar.make(v, "已复制为JSON", Snackbar.LENGTH_SHORT).show();

                }
                if (selectedItems.contains("粘贴（覆盖该标签）") || selectedItems.contains("粘贴（作为该标签的子标签）")) {
                    String json = getClipboardText();
                    if (json == null) {
                        //Toast.makeText(context, "剪贴板无有效JSON", Toast.LENGTH_SHORT).show();
                        Snackbar.make(v, "剪贴板无有效JSON", Snackbar.LENGTH_LONG).show();

                        return;
                    }

                    try {
                        Tag newTag = JsonToNBTConverter.parseJsonToTag(json);

                        if (selectedItems.contains("粘贴（覆盖该标签）")) {
                            replaceTag(item, newTag,position); // 覆盖当前标签
                        } else {
                            addAsChild(item, newTag,position); // 添加为子标签
                        }

                        Snackbar.make(v, "粘贴成功", Snackbar.LENGTH_SHORT).show();

                    } catch (JSONException e) {

                        Snackbar.make(v, "JSON解析失败: " + e.getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                }
                if(selectedItems.contains("删除")){
                    deleteTag(item,position);
                }
                if(selectedItems.contains("添加子标签")){
                    if(isCompositeTag(item.getType())){
                        AddNBTDialog(item,position);
                    }else{
                        Snackbar.make(v, "只能在CompoundTag或ListTag中添加子标签", Snackbar.LENGTH_LONG).show();
                    }

                }
                if(selectedItems.contains("重命名")){
                    renameTag(item,position);

                }
                nbt.modified=true;
            });
            dialog.show();
            return true;
        });



        // 设置左边距
        int marginLeft = item.getMarginLeft();

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        layoutParams.leftMargin = marginLeft;
        holder.itemView.setLayoutParams(layoutParams);
    }
    // 复制到粘贴板
    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("NBT JSON", text);
        clipboard.setPrimaryClip(clip);
    }

    //协助expand方法 获取一个复合标签的所有子项 以及是复合标签且展开的子项的子项 类型是NBTItem 自带缩进
    public List<NBTItem> getComprehensiveChildNBTItem(NBTItem item){

        List<NBTItem> ChildNBTItemList = item.getChildNBTItem();
        for(int i = 0;i<ChildNBTItemList.size();i++){
            NBTItem eachItem = ChildNBTItemList.get(i);
            if(isCompositeTag(eachItem.getType())&&eachItem.getState()){
                ChildNBTItemList.addAll(i+1,eachItem.getChildNBTItem());

            }

        }
        return ChildNBTItemList;
    }
    //展开
    public void expand(NBTItem item, int position){

        List<NBTItem> ChildNBTItemList = getComprehensiveChildNBTItem(item);

        ArrayList<NBTItem> copyNBTItemList = new ArrayList<>(itemList);


        itemList.addAll(position+1,ChildNBTItemList);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NbtDiffCallback(copyNBTItemList, itemList));
        diffResult.dispatchUpdatesTo(this);

    }
    //收起
    //state决定再次展开时子项已经子项的子项等复合标签是否保持原有的展开/收起状态
    //state=true是保持原有状态 false是不保持 也即全部不展开
    public void retract(NBTItem item, int position,boolean state){
        if(!item.getState()){return;}
        ArrayList<NBTItem> copyNBTItemList = new ArrayList<>(itemList);


        List<NBTItem> NewChildNBTItem = new ArrayList<>();
        int initSize = itemList.size();
        //倒序遍历删除子项 防止报错
        for (int i = itemList.size() - 1; i >= 0; i--) {
            NBTItem eachItem = itemList.get(i);
            if(item.getID()==eachItem.getParentID()){
                if(isCompositeTag(eachItem.getType())&&eachItem.getState()){
                    //eachItem.setChildCache(retract(eachItem, (initSize - i) + position));
                    retract(eachItem, (initSize - i) + position,state);
                    eachItem.setState(state);
                }


                NewChildNBTItem.add(eachItem);
                itemList.remove(i);
            }
        }

        Collections.reverse(NewChildNBTItem);
        item.setChildCache(NewChildNBTItem);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NbtDiffCallback(copyNBTItemList, itemList));
        diffResult.dispatchUpdatesTo(this);
        return;
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //是否为复合标签
    public boolean isCompositeTag(NBTConstants.NBTType type){
        return type.equals(NBTConstants.NBTType.COMPOUND)||type.equals(NBTConstants.NBTType.LIST);
    }
    public List<NBTItem> getItemList(){
        return itemList;
    }
    // 获取剪贴板文本
    private String getClipboardText() {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            return clipboard.getPrimaryClip().getItemAt(0).getText().toString();
        }
        return null;
    }

    // 替换标签
    private void replaceTag(NBTItem oldItem, Tag newTag,int position) {
        NBTItem parentItem = null;

        for (NBTItem parent : itemList) {
            if (parent.getID().equals(oldItem.getParentID())) {
                parentItem = parent;
            }
        }
        Tag parentTag = findParentTag(oldItem);
        if(parentTag==null){//根标签
            NBTItem newItem = new NBTItem(newTag);
            newItem.setLevels(0);
            itemList.remove(position);
            itemList.add(position,newItem);
            nbt.addRootTag(newTag);
            notifyItemChanged(position);
            return;
        }
        if(isCompositeTag(oldItem.getType())&&oldItem.getState()){
            retract(oldItem,position,true);
            oldItem.setState(false);
        }
        if (parentTag instanceof CompoundTag) {
            ArrayList<Tag> children = ((CompoundTag) parentTag).getValue();
            children.remove(oldItem.getTag());
            children.add(newTag);
        } else if (parentTag instanceof ListTag) {
            ArrayList<Tag> elements = ((ListTag) parentTag).getValue();
            int index = elements.indexOf(oldItem.getTag());
            elements.set(index, newTag);
        }

        NBTItem newItem = new NBTItem(newTag);
        newItem.setLevels(parentItem.getLevels()+1);
        newItem.setParentID(parentItem.getID());
        itemList.add(position,newItem);
        notifyItemChanged(position);
    }

    // 添加为子标签
    private void addAsChild(NBTItem parentItem, Tag newTag,int position) {

        if (parentItem.getTag() instanceof CompoundTag) {
            ((CompoundTag) parentItem.getTag()).getValue().add(newTag);
        } else if (parentItem.getTag() instanceof ListTag) {
            ((ListTag) parentItem.getTag()).getValue().add(newTag);
        }
        if(parentItem.getState()){
            NBTItem item = new NBTItem(newTag);
            item.setLevels(parentItem.getLevels()+1);
            item.setParentID(parentItem.getID());
            itemList.add(position+1,item);
            notifyItemRangeInserted(position+1,1);
        }else{
            parentItem.clearChildCache();
        }

    }

    // 查找父标签
    private Tag findParentTag(NBTItem item) {
        for (NBTItem parent : itemList) {
            if (parent.getID().equals(item.getParentID())) {
                return parent.getTag();
            }
        }
        return null; // 如果未找到，则默认为根标签
    }
    //删除标签
    public void deleteTag(NBTItem item,int position){
        if(isCompositeTag(item.getType())&&item.getState()){
            retract(item,position,true);
            item.setState(false);
        }
        Tag parentTag = findParentTag(item);
        if(parentTag==null){
            nbt.removeRootTag(item.getTag());
            notifyItemRangeRemoved(position,1);
            return;
        }

        //NBTItem parentItem = new NBTItem(parentTag);
        ArrayList<Tag> children = (ArrayList<Tag>)parentTag.getValue();
        children.remove(children.indexOf(item.getTag()));
        notifyItemRangeRemoved(position,1);
    }

    public void renameTag(NBTItem item,int position){
        Tag tag = item.getTag();
        final EditText NameEditText = new EditText(context);
        NameEditText.setText(tag.getName());
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("重命名")
                .setView(NameEditText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        String name = NameEditText.getText().toString();
                        tag.setName(name);
                        item.setText(name);
                        notifyItemChanged(position);
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        dialog.show();
    }

    //添加标签
    private void AddNBTDialog(NBTItem item,int position) {
        // 创建自定义布局的 Dialog


        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_add_nbt_tag);

        // 获取布局中的控件
        EditText editText = dialog.findViewById(R.id.editText);
        Spinner spinner = dialog.findViewById(R.id.spinner);
        Button buttonOk = dialog.findViewById(R.id.buttonOk);

        // 初始化 Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 设置确定按钮的点击事件
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入内容
                String name = editText.getText().toString();

                // 获取选中的下拉选项
                String selectedOption = spinner.getSelectedItem().toString();


                Tag newTag = null;
                switch(selectedOption){
                    case "ByteTag":
                        newTag = new ByteTag(name,(byte)0);
                        break;
                    case "ShortTag":
                        newTag = new ShortTag(name,(short)0);
                        break;
                    case "IntTag":
                        newTag = new IntTag(name,0);
                        break;
                    case "LongTag":
                        newTag = new LongTag(name,0L);
                        break;
                    case "FloatTag":
                        newTag = new FloatTag(name,0F);
                        break;
                    case "DoubleTag":
                        newTag = new DoubleTag(name,0D);
                        break;
                    case "ByteArrayTag":
                        newTag = new ByteArrayTag(name,new byte[]{});
                        break;
                    case "StringTag":
                        newTag = new StringTag(name,"");
                        break;
                    case "ListTag":
                        newTag = new ListTag(name,new ArrayList<>());
                        break;
                    case "CompoundTag":
                        newTag = new CompoundTag(name,new ArrayList<>());
                        break;
                    case "IntArrayTag":
                        newTag = new IntArrayTag(name,new int[]{});
                        break;

                    case "ShortArrayTag":
                        newTag = new ShortArrayTag(name,new short[]{});
                        break;

                }
                ((ArrayList<Tag>)item.getTag().getValue()).add(newTag);
                if(item.getState()){
                    NBTItem childItem = new NBTItem(newTag);
                    childItem.setLevels(item.getLevels()+1);
                    childItem.setParentID(item.getID());
                    itemList.add(position+1,childItem);
                    notifyItemRangeInserted(position+1,1);
                }else{
                    item.clearChildCache();
                }

                // 关闭对话框
                dialog.dismiss();
            }
        });

        // 设置对话框的圆角背景
        //dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);

        // 显示对话框
        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        EditText editText;
        TextWatcher textWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.item_text);
            editText = itemView.findViewById(R.id.item_edit);


            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {

                    NBTItem  realItem = ((NBTItem)editText.getTag());
                    Tag tag = realItem.getTag();

                    try{
                        String value = s.toString();

                        switch (realItem.getType()) {
                            case INT:
                                ((IntTag) tag).setValue(Integer.parseInt(value));
                                break;
                            case STRING:
                                ((StringTag) tag).setValue(value);
                                break;
                            case LONG:
                                ((LongTag) tag).setValue(Long.parseLong(value));
                                break;
                            // 其他类型...
                            case FLOAT:
                                ((FloatTag) tag).setValue(Float.parseFloat(value));
                                break;
                            case SHORT:
                                ((ShortTag) tag).setValue(Short.parseShort(value));
                                break;
                            case DOUBLE:
                                ((DoubleTag) tag).setValue(Double.parseDouble(value));
                                break;
                            case BYTE:
                                ((ByteTag) tag).setValue(Byte.parseByte(value));
                                break;
                        }



                    } catch (NumberFormatException e) {
                        editText.setError("Invalid format "+textView.getText());
                    }
                }
            };
        }
    }
}
// 定义一个继承自 DiffUtil.Callback 的类 用于recyclerView的数据源发生复杂变化时 通知其更新
// 尽量避免为了省事使用notifyDataSetChanged() 除了会导致整个列表刷新 性能较低外 还没有灵动的变化动画 显得僵硬
class NbtDiffCallback extends DiffUtil.Callback {
    private final List<NBTItem> oldList;
    private final List<NBTItem> newList;

    public NbtDiffCallback(List<NBTItem> oldList, List<NBTItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getID().equals(newList.get(newItemPosition).getID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }


}




class NBTItem {
    private Tag tag;
    private int imageResId; // 图片资源ID
    private String text;    // 文本内容
    private String hint;    // 编辑框提示
    private int marginLeft; // 左边距（单位：像素）
    private int levels = 0;
    private boolean state = false;
    private String ID;
    private String parentID;
    private List<NBTItem> childCache = null;
    private boolean isModified = false;


    public NBTItem(Tag tag) {
        this.tag = tag;
        this.imageResId = R.drawable.ic_launcher_background;
        this.text = tag.getName();
        this.hint = tag.getType().name();
        this.marginLeft = 0;
        UUID randomUUID = UUID.randomUUID();
        ID = randomUUID.toString();

    }

    public int getImageResId() {
        return imageResId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text){
        this.text=text;
    }

    public List<NBTItem> getChildNBTItem(){
        if(childCache!=null){return childCache;}
        List<NBTItem> ChildNBTItemList = new ArrayList<>();

        for(Tag tag:(List<Tag>)tag.getValue()){
            NBTItem item = new NBTItem(tag);
            item.setLevels(this.getLevels()+1);
            item.setParentID(ID);
            ChildNBTItemList.add(item);
        }
        childCache = ChildNBTItemList;
        return ChildNBTItemList;
    }

    public void setChildCache(List<NBTItem> childCache){
        this.childCache=childCache;
    }

    public Tag getTag(){
        return tag;
    }

    public boolean isOpen(){
        return this.state;
    }

    public void setState(boolean state){
        this.state = state;
    }

    public boolean getState(){
        return this.state;
    }

    public String getParentID(){
        return this.parentID;
    }

    public void setParentID(String parentID){
        this.parentID = parentID;
    }
    /*
    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean isModified){
        this.isModified=isModified;
        if(isModified){

        }
    }
    */
    public String getID(){
        return this.ID;
    }

    public int getLevels(){
        return this.levels;
    }

    public void setLevels(int levels){
        this.levels = levels;
        this.marginLeft = 50 * levels;
    }

    public String getTypeName(){
        return tag.getType().name();
    }

    public NBTConstants.NBTType getType(){
        return tag.getType();
    }

    public boolean hasName(){
        return !(tag.getName()==null||tag.getName().equals(""));
    }

    public String getHint() {
        return hint;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void clearChildCache() {
        this.childCache = null;
    }
}
