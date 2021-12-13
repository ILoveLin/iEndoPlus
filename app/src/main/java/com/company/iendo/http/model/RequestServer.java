package com.company.iendo.http.model;

import com.company.iendo.other.AppConfig;
import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2020/10/02
 *    desc   : 服务器配置
 */
public class RequestServer implements IRequestServer {

    @Override
    public String getHost() {
        return AppConfig.getHostUrl();
    }

    @Override
    public String getPath() {
        return "http://192.168.131.114:7001";
    }

    @Override
    public BodyType getType() {
        // 以表单的形式提交参数
        return BodyType.FORM;
    }
}