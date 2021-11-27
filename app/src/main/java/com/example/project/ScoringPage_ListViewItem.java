package com.example.project;

public class ScoringPage_ListViewItem {

    private int wrongAll;
    private String titleStr ;
    private String descStr ;

    public void setIcon(int wrong) {
        wrongAll = wrong ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public int getWrongAll() {
        return this.wrongAll ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
}
