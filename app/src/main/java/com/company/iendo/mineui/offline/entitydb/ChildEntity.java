package com.company.iendo.mineui.offline.entitydb;

import com.company.iendo.green.db.downcase.CaseDBBean;

/**
 * 子项数据的实体类
 */
public class ChildEntity {

    private CaseDBBean bean;

    public ChildEntity(CaseDBBean bean) {
        this.bean = bean;
    }

    public CaseDBBean getBean() {
        return bean;
    }

    public void setBean(CaseDBBean bean) {
        this.bean = bean;
    }
}
