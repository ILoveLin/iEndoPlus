package com.company.iendo.bean;
import java.io.Serializable;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2021/11/4 9:32
 * desc：方案实体类
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