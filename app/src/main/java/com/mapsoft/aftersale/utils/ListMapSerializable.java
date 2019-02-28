package com.mapsoft.aftersale.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/27.
 */

public class ListMapSerializable implements Serializable{
    private List<Map<String,String>> list;

    public List<Map<String, String>> getListMap() {
        return list;
    }

    public void setListMap(List<Map<String, String>> list) {
        this.list = list;
    }
}
