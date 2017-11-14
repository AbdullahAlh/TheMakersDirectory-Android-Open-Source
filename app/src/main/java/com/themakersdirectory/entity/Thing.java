package com.themakersdirectory.entity;

import com.google.firebase.database.ValueEventListener;
import com.themakersdirectory.AppManager;
import com.themakersdirectory.firebase.FirebaseDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xlethal on 8/6/16.
 */

public class Thing implements Serializable {
    public static final String TOOL_TYPE = "Tool";
    public static final String MATERIAL_TYPE = "Material";
    public String key;
    public Map<String, Object> location;
    public String description;
    public String description_ar;
    public String img;
    public String name;
    public String name_ar;
    public String type;

    public Thing() {

    }

    public Thing(Map<String, Object> things, String description, String description_ar, String img, String name, String name_ar) {
        this.location = things;
        this.description = description;
        this.description_ar = description_ar;
        this.img = img;
        this.name = name;
        this.name_ar = name_ar;
    }

    public Thing(Map<String, Object> things, String description, String description_ar, String img, String name, String name_ar, String type) {
        this.location = things;
        this.description = description;
        this.description_ar = description_ar;
        this.img = img;
        this.name = name;
        this.name_ar = name_ar;
        this.type = type;
    }

    public String name() {
        if (AppManager.isArabic())
            return name_ar;
        else
            return name;
    }

    public String description() {
        if (AppManager.isArabic())
            return description_ar;
        else
            return description;
    }

    public void loadPlaces(ValueEventListener valueEventListener) {
        if (location != null) {
            Object[] keys = location.keySet().toArray();
            for (Object key : keys) {
                FirebaseDataManager.init().loadPlaceFirebase((String) key, valueEventListener);
            }
        }

    }

    public static Thing fromAlgoliaJson(JSONObject jsonObject) {
        Thing thing = new Thing();
        try {
            if (jsonObject.has("description"))
                thing.description = jsonObject.getString("description");

            if (jsonObject.has("description_ar"))
                thing.description_ar = jsonObject.getString("description_ar");

            thing.img = jsonObject.getString("img");
            thing.name = jsonObject.getString("name");
            thing.name_ar = jsonObject.getString("name_ar");
            thing.type = jsonObject.getString("type");
            thing.key = jsonObject.getString("objectID");

            if (jsonObject.has("location")) {
                JSONObject object = jsonObject.getJSONObject("location");
                Iterator<String> keys = object.keys();
                Map<String, Object> list = new HashMap<>();

                while (keys.hasNext()) {
                    String key = keys.next();
                    list.put(key, object.get(key));

                }

                thing.location = list;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return thing;
    }

    public boolean isTypeMaterial() {
        return type.equals("Material");

    }

    public boolean isTypeTool() {
        return type.equals("Tool");

    }
    
}
