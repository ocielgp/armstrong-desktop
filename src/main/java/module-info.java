module app {
    requires javafx.swing;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;

    requires org.kordamp.ikonli.material;
    requires org.kordamp.ikonli.javafx;

    requires com.jfoenix;

    requires java.sql;
    requires com.zaxxer.hikari;

    requires java.desktop;
    requires java.prefs;

    exports com.digitalpersona.uareu;
    exports animatefx.animation;
    exports animatefx.util;

    opens com.ocielgp;
    opens com.ocielgp.app;
    opens com.ocielgp.dao;
    opens com.ocielgp.fingerprint;
    opens com.ocielgp.controller;
    opens com.ocielgp.controller.dashboard;
    opens com.ocielgp.models;
    opens com.ocielgp.utilities;
}