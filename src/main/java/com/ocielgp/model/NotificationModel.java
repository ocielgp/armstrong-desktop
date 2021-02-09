package com.ocielgp.model;

public class NotificationModel {
    private String icon;
    private String title;
    private String content;
    private int time;
    private String style;

    public NotificationModel(String icon, String title, String content, int time, String style) {
        this.icon = icon;
        this.title = title;
        this.content = content;
        this.time = time;
        this.style = style;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
