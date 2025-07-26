package com.mithrilmania.blocktopograph.util;

import com.mithrilmania.blocktopograph.nbt.tags.*;
import com.mithrilmania.blocktopograph.nbt.tags.Tag;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class NBTToJsonConverter {

    public static String convertTagToJson(Tag tag) {
        try {
            JSONObject root = new JSONObject();
            root.put(tag.getName(), convertInternal(tag));
            return root.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    private static Object convertInternal(Tag tag) throws Exception {
        if (tag == null) return null;

        switch (tag.getType()) {
            case BYTE:
                return ((ByteTag) tag).getValue() + "b"; // Byte: 5b

            case SHORT:
                return ((ShortTag) tag).getValue() + "s"; // Short: 10s

            case FLOAT:
                return ((FloatTag) tag).getValue() + "f"; // Float: 3.14f

            case INT:
                return ((IntTag) tag).getValue(); // Int: 100

            case LONG:
                return ((LongTag) tag).getValue() + "l"; // Long: 123456789l

            case DOUBLE:
                return ((DoubleTag) tag).getValue() + "d"; // Double: 3.14159d

            case COMPOUND:
                JSONObject compound = new JSONObject();

                for (Tag child : (List<Tag>) tag.getValue()) {
                    compound.put(child.getName(), convertInternal(child));
                }
                return compound;

            case LIST:
                JSONArray list = new JSONArray();
                for (Tag child : (List<Tag>) tag.getValue()) {
                    list.put(convertInternal(child));
                }
                return list;

            default:
                return tag.getValue().toString();
        }
    }
}