package com.mithrilmania.blocktopograph.util;

import com.mithrilmania.blocktopograph.nbt.convert.NBTConstants;
import com.mithrilmania.blocktopograph.nbt.tags.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonToNBTConverter {

    public static Tag parseJsonToTag(String jsonStr) throws JSONException {
        try {
            JSONObject rootJson = new JSONObject(jsonStr);
            if (rootJson.length() != 1) {
                throw new JSONException("根对象必须包含且仅包含一个键");
            }
            String rootKey = rootJson.keys().next();
            Object content = rootJson.get(rootKey);

            // 关键修改：允许根键值为JSONArray
            return parseJsonValue(content, rootKey);
        } catch (JSONException e) {
            throw new JSONException("无效的JSON格式: " + e.getMessage());
        }
    }
    private static Tag parseJsonValue(Object jsonValue, String name) throws JSONException {
        if (jsonValue instanceof JSONObject) {
            // 处理CompoundTag
            JSONObject jsonObj = (JSONObject) jsonValue;
            ArrayList<Tag> children = new ArrayList<>();
            Iterator<String> keys = jsonObj.keys(); // 获取键的 Iterator
            while (keys.hasNext()) {
                String key = keys.next(); // 获取下一个键
                Object childValue = jsonObj.get(key);
                children.add(parseJsonValue(childValue, key));
            }
            return new CompoundTag(name, children);

        } else if (jsonValue instanceof JSONArray) {
            // 处理ListTag
            JSONArray jsonArr = (JSONArray) jsonValue;
            ArrayList<Tag> elements = new ArrayList<>();
            NBTConstants.NBTType listType = null;
            for (int i = 0; i < jsonArr.length(); i++) {
                Tag elementTag = parseJsonValue(jsonArr.get(i), "");
                if (listType == null) listType = elementTag.getType();
                elements.add(elementTag);
            }
            return new ListTag(name, elements);

        } else if (jsonValue instanceof String) {
            String strValue = (String) jsonValue;
            // 解析后缀
            if (strValue.endsWith("b")) {
                return parseNumberWithSuffix(strValue, "b", name, Byte.MIN_VALUE, Byte.MAX_VALUE);
            } else if (strValue.endsWith("s")) {
                return parseNumberWithSuffix(strValue, "s", name, Short.MIN_VALUE, Short.MAX_VALUE);
            } else if (strValue.endsWith("f")) {
                return new FloatTag(name, Float.parseFloat(strValue.replace("f", "")));
            } else if (strValue.endsWith("l")) {
                return new LongTag(name, Long.parseLong(strValue.replace("l", "")));
            } else if (strValue.endsWith("d")) {
                return new DoubleTag(name, Double.parseDouble(strValue.replace("d", "")));
            } else {
                return new StringTag(name, strValue);
            }

        } else if (jsonValue instanceof Number) {
            // 无后缀默认处理为Int或Double
            Number num = (Number) jsonValue;
            if (num instanceof Integer) {
                return new IntTag(name, num.intValue());
            } else {
                return new DoubleTag(name, num.doubleValue());
            }

        } else {
            throw new JSONException("Unsupported type: " + jsonValue.getClass());
        }
    }
    private static <T extends Number> Tag parseNumberWithSuffix(
            String strValue,
            String suffix,
            String name,
            T minValue,
            T maxValue
    ) throws JSONException {
        String raw = strValue.replace(suffix, "");
        try {
            if (suffix.equals("b")) {
                byte value = Byte.parseByte(raw);
                return new ByteTag(name, value);
            } else if (suffix.equals("s")) {
                short value = Short.parseShort(raw);
                return new ShortTag(name, value);
            } else if (suffix.equals("f")) {
                float value = Float.parseFloat(raw);
                return new FloatTag(name, value);
            } else if (suffix.equals("l")) {
                long value = Long.parseLong(raw);
                return new LongTag(name, value);
            } else if (suffix.equals("d")) {
                double value = Double.parseDouble(raw);
                return new DoubleTag(name, value);
            } else{
                return null;
            }

        } catch (NumberFormatException e) {
            throw new JSONException("Invalid value for " + suffix + ": " + raw);
        }
    }
    /*
   public class JsonToNBTConverter {

    public static Tag parseJsonToTag(String jsonStr) throws JSONException {
        JSONObject rootJson = new JSONObject(jsonStr);
        String rootKey = rootJson.keys().next();
        Object content = rootJson.get(rootKey);
        return parseJsonValue(content, rootKey);
    }



    // 通用数值解析方法（处理溢出）

}

*/

}
