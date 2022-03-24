package com.company.iendo.mineui.offline.entitydb;
import com.company.iendo.green.db.downcase.CaseDBBean;

import java.util.ArrayList;

/**
 * 组数据的实体类
 */
public class GroupEntity {

    private String header;
    private String footer;
    private ArrayList<CaseDBBean> children;

    public GroupEntity(String header, String footer, ArrayList<CaseDBBean> children) {
        this.header = header;
        this.footer = footer;
        this.children = children;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public ArrayList<CaseDBBean> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<CaseDBBean> children) {
        this.children = children;
    }
}
