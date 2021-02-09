package com.ocielgp.model;

import com.jfoenix.controls.JFXButton;

public class CustomDialogModel {
    private String icon;
    private String title;
    private String content;
    //                      Type    Button
    private JFXButton[] buttons;

    public CustomDialogModel(String icon, String title, String content, JFXButton[] buttons) {
        this.icon = icon;
        this.title = title;
        this.content = content;
        this.buttons = buttons;
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

    public JFXButton[] getButtons() {
        return buttons;
    }

    public void setButtons(JFXButton[] buttons) {
        this.buttons = buttons;
    }
}