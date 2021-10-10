package com.example.alpha.Main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParsser {
    private HashMap<String,String> parseJsonObject(JSONObject object){
        // Initialize hash map
        HashMap<String,String> dataList = new HashMap<>();
        try {
            //get name from object
            String name = object.getString("name");

            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");

            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");

            dataList.put("name",name);
            dataList.put("lat",latitude);
            dataList.put("lng",longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List <HashMap<String, String>> datalist = new ArrayList<>();
        for(int i=0; i<jsonArray.length();i++){
            try {
                HashMap<String,String> data = parseJsonObject((JSONObject)jsonArray.get(i));
                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray = null;

        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}
