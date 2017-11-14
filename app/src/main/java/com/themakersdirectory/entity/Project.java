package com.themakersdirectory.entity;

import com.google.firebase.database.ValueEventListener;
import com.themakersdirectory.AppManager;
import com.themakersdirectory.firebase.FirebaseDataManager;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by xlethal on 3/28/16.
 */

public class Project implements Serializable {
    public String key;
    public Map<String, Object> tools;
    public Map<String, Object> materials;
    public String description;
    public String description_ar;
    public String img;
    public String name;
    public String name_ar;

    public Project() {

    }

    public Project(Map<String, Object> tools, Map<String, Object> materials, String description, String description_ar, String img, String name, String name_ar) {
        this.tools = tools;
        this.materials = materials;
        this.description = description;
        this.description_ar = description_ar;
        this.img = img;
        this.name = name;
        this.name_ar = name_ar;
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

    public void loadMaterials(ValueEventListener valueEventListener) {
        if (materials != null) {
            Object[] keys = materials.keySet().toArray();
            for (Object key : keys) {
                FirebaseDataManager.init().loadMaterialFirebase((String) key, valueEventListener);
            }
        }

    }

    public void loadTools(ValueEventListener valueEventListener) {
        if (tools != null) {
            Object[] keys = tools.keySet().toArray();
            for (Object key : keys) {
                FirebaseDataManager.init().loadToolFirebase((String) key, valueEventListener);
            }
        }

    }
}
