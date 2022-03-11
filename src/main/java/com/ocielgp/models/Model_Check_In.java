package com.ocielgp.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model_Check_In {
    private final StringProperty dateTime = new SimpleStringProperty();
    private final StringProperty adminName = new SimpleStringProperty();
    private final StringProperty memberName = new SimpleStringProperty();
    private final StringProperty gymName = new SimpleStringProperty();
    private final BooleanProperty openedBySystem = new SimpleBooleanProperty();

    public String getDateTime() {
        return dateTime.get();
    }

    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime.set(dateTime);
    }

    public String getAdminName() {
        return adminName.get();
    }

    public StringProperty adminNameProperty() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName.set(adminName);
    }

    public String getMemberName() {
        return memberName.get();
    }

    public StringProperty memberNameProperty() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName.set(memberName);
    }

    public String getGymName() {
        return gymName.get();
    }

    public StringProperty gymNameProperty() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName.set(gymName);
    }

    public boolean isOpenedBySystem() {
        return openedBySystem.get();
    }

    public BooleanProperty openedBySystemProperty() {
        return openedBySystem;
    }

    public void setOpenedBySystem(boolean openedBySystem) {
        this.openedBySystem.set(openedBySystem);
    }
}
