package com.mapsoft.aftersale.utils;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/26.
 * 序列化Map
 */

public class MapSerializable implements Serializable {
    private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
