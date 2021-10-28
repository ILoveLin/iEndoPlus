package com.company.iendo.mineui.bean;

import java.io.Serializable;

/**
 * 方案实体类
 * Created by YoKeyword on 15/12/29.
 */
public class ProgramEntity implements Serializable {

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
