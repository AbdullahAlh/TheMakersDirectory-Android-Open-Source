package com.themakersdirectory.entity;

import com.themakersdirectory.AppManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xlethal on 3/28/16.
 */
public class Place implements Serializable {
    public String key;
    public double lat;
    public double lng;
    public String img;
    public String logo;
    public String name;
    public String name_ar;
    public String url;
    public ArrayList<Object> phone_number;
    public String type;
    public String type_ar;
    public ArrayList<Object> branch;

    public Place() {

    }

    public Place(double lat, double lng, String img, String logo, String name, String name_ar, String url,
                 ArrayList<Object> phone_number, String type, String type_ar, ArrayList<Object> branch) {
        this.lat = lat;
        this.lng = lng;
        this.img = img;
        this.logo = logo;
        this.name = name;
        this.name_ar = name_ar;
        this.url = url;
        this.phone_number = phone_number;
        this.type = type;
        this.type_ar = type_ar;
        loadBranches();
    }

    public String name() {
        if (AppManager.isArabic())
            return name_ar;
        return name;
    }

    public String type() {
        if (AppManager.isArabic())
            return type_ar;
        return type;


    }

    public void loadBranches() {
        ArrayList<Object> list = new ArrayList<>();

        if (branch != null)
            for (int i = 0; i < branch.size(); i++) {
                HashMap<String, Object> object = (HashMap<String, Object>) branch.get(i);
                Branch current_branch = new Branch((Double) object.get("lat"), (Double) object.get("lng"), (String) object.get("name"), (String) object.get("name_ar"));
                if (object.containsKey("phone_number"))
                    current_branch.setPhoneNumber((ArrayList<Object>) object.get("phone_number"));
                list.add(current_branch);

            }

        branch = list;
    }

    public static Place fromAlgoliaJson(JSONObject jsonObject) {
        Place place = new Place();

        try {
            if (jsonObject.has("lat"))
                place.lat = jsonObject.getDouble("lat");

            if (jsonObject.has("lng"))
                place.lng = jsonObject.getDouble("lng");

            if (jsonObject.has("logo"))
                place.logo = jsonObject.getString("logo");

            place.img = jsonObject.getString("img");
            place.name = jsonObject.getString("name");
            place.name_ar = jsonObject.getString("name_ar");
            place.type = jsonObject.getString("type");
            place.type_ar = jsonObject.getString("type_ar");
            place.key = jsonObject.getString("objectID");

            if (jsonObject.has("url"))
                place.url = jsonObject.getString("url");

            if (jsonObject.has("phone_number")) {
                JSONArray object = jsonObject.getJSONArray("phone_number");
                ArrayList<Object> list = new ArrayList<>();

                for (int i = 0; i < object.length(); i++) {
                    list.add(object.get(i));

                }

                place.phone_number = list;
            }

            if (jsonObject.has("branch")) {
                JSONArray object = jsonObject.getJSONArray("branch");
                ArrayList<Object> branches_list = new ArrayList<>();

                for (int i = 0; i < object.length(); i++) {
                    JSONObject branch = object.getJSONObject(i);
                    Branch branch1 = new Branch(branch.getDouble("lat"), branch.getDouble("lng"), branch.getString("name"), branch.getString("name_ar"));

                    if (branch.has("phone_number")) {
                        JSONArray phone_number = branch.getJSONArray("phone_number");
                        ArrayList<Object> phone_number_list = new ArrayList<>();

                        for (int j = 0; j < phone_number.length(); j++) {
                            phone_number_list.add(phone_number.get(j));

                        }

                        branch1.setPhoneNumber(phone_number_list);
                    }

                    branches_list.add(branch1);
                }

                place.branch = branches_list;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }

}
