package com.company.iendo.bean;

import com.google.gson.annotations.SerializedName;

/**
 * company：江西神州医疗设备有限公司
 * author： LoveLin
 * time：2022/3/29 17:16
 * desc：{"username":"test"}
 * 返回的是用户名字,判断和当前用户名字是否相同,是的话tag设置为true,更新本地权限,不然不做处理
 */
public class UserReloChanged {
    @SerializedName("username")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserReloChanged{" +
                "username='" + username + '\'' +
                '}';
    }
}
