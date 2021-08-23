module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires org.kordamp.ikonli.material;
    requires org.kordamp.ikonli.javafx;

    requires com.jfoenix;

    requires java.sql;
    requires mysql.connector.java;

    requires java.desktop;
    requires javafx.swing;

    exports com.digitalpersona.uareu;
    exports animatefx.animation;
    exports animatefx.util;

    opens com.ocielgp;
    opens com.ocielgp.app;
    opens com.ocielgp.controller;
    opens com.ocielgp.files;
    opens com.ocielgp.fingerprint;
    opens com.ocielgp.utilities;

    opens com.ocielgp.database;
    opens com.ocielgp.database.members;
    opens com.ocielgp.database.memeberships;
    opens com.ocielgp.database.payments;
    opens com.ocielgp.database.staff;
    opens com.ocielgp.database.system;
}