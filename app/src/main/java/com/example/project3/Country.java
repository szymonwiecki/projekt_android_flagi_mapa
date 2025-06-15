package com.example.project3;

import java.util.List;
import java.util.Map;

public class Country {
    private Map<String, String> flags;
    private List<Double> latlng;

    public Map<String, String> getFlags() {
        return flags;
    }

    public void setFlags(Map<String, String> flags) {
        this.flags = flags;
    }

    public List<Double> getLatlng() {
        return latlng;
    }

    public void setLatlng(List<Double> latlng) {
        this.latlng = latlng;
    }
}