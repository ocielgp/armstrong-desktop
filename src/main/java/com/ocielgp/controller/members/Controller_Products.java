package com.ocielgp.controller.members;

import animatefx.animation.FadeInRight;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.ocielgp.app.Application;
import com.ocielgp.controller.Popup;
import com.ocielgp.dao.JDBC_Member;
import com.ocielgp.dao.JDBC_Product;
import com.ocielgp.models.Model_Product;
import com.ocielgp.utilities.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Products implements Initializable {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox boxRoot;

    @FXML
    private VBox boxProducts;
    @FXML
    private JFXComboBox<Model_Product> comboBoxProducts;

    @FXML
    private VBox boxHistorical;
    @FXML
    private Label labelHistorical;
    @FXML
    private Label labelDateTime;
    @FXML
    private Label labelAdmin;

    @FXML
    private VBox boxProductDetail;
    @FXML
    private JFXTextField fieldName;
    @FXML
    private JFXTextField fieldPrice;
    @FXML
    private HBox boxButtonDelete;
    @FXML
    private JFXButton buttonDelete;

    @FXML
    private HBox boxEndButtons;
    @FXML
    private JFXButton buttonSave;
    @FXML
    private JFXButton buttonClear;

    @FXML
    private JFXButton buttonCreate;
    @FXML
    private JFXButton buttonEdit;

    // attributes
    private FormChangeListener formChangeListener;
    private Model_Product modelProduct = new Model_Product();

    private void configureForm() {
        createFormChangeListener();
        InputProperties.getScrollEvent(this.scrollPane);

        InputProperties.createVisibleAnimation(this.boxProducts, false);
        InputProperties.createVisibleAnimation(this.boxHistorical, false);
        InputProperties.createVisibleAnimation(this.boxProductDetail, false);
        InputProperties.createVisibleAnimation(this.boxButtonDelete, false);
        InputProperties.createVisibleAnimation(this.boxEndButtons, false);

        // properties binding
        this.comboBoxProducts.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                this.modelProduct = newValue;
                fillProductData(newValue);
            }
        });
    }

    private void configureData() {
        JDBC_Product.ReadProducts().thenAccept(model_products -> {
            if (model_products.isEmpty()) this.buttonEdit.setDisable(true);
            else this.comboBoxProducts.setItems(model_products);
            Loading.isChildLoaded.set(true);
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureForm();
        configureData();

        // event handlers
        this.buttonCreate.setOnAction(actionEvent -> eventCreate());
        this.buttonEdit.setOnAction(actionEvent -> eventEdit());

//        this.buttonDelete.setDisable(true);
        this.buttonDelete.setOnAction(actionEvent -> eventDelete());

        // end buttons
        this.buttonSave.setOnAction(actionEvent -> eventSave());
        this.buttonClear.setOnAction(actionEvent -> clearForm(true));
    }

    private void fillProductData(Model_Product modelProduct) {
        int idMember;
        if (modelProduct.getUpdatedBy() == 0) {
            Platform.runLater(() -> this.labelHistorical.setText("Creado"));
            idMember = modelProduct.getCreatedBy();
        } else {
            Platform.runLater(() -> this.labelHistorical.setText("Modificado"));
            idMember = modelProduct.getUpdatedBy();
        }
        JDBC_Member.ReadMember(idMember).thenAccept(model_member -> Platform.runLater(() -> {
            this.labelDateTime.setText(DateTime.getDateWithDayName(
                    (modelProduct.getUpdatedBy() == 0) ? modelProduct.getCreatedAt() : modelProduct.getUpdatedAt()
            ));
            this.labelAdmin.setText(model_member.getName() + " " + model_member.getLastName());
            this.boxHistorical.setVisible(true);

            this.fieldName.setText(modelProduct.getName());
            this.fieldPrice.setText(modelProduct.getPrice().toString());
            this.boxProductDetail.setVisible(true);

            this.formChangeListener.setListen(true);

            this.buttonSave.setDisable(true);
            this.boxEndButtons.setVisible(true);
        }));
    }

    private void clearForm(boolean animation) {
        this.formChangeListener.setListen(false);

        this.boxProducts.setVisible(false);
        this.comboBoxProducts.setValue(null);
        this.boxHistorical.setVisible(false);

        this.boxProductDetail.setVisible(false);
        this.fieldName.setText("");
        this.fieldPrice.setText("");

        this.boxButtonDelete.setVisible(false);
        this.buttonSave.setDisable(false);
        this.boxEndButtons.setVisible(false);

        if (animation) {
            new FadeInRight(this.scrollPane).play();
        }
    }

    private void eventCreate() {
        clearForm(false);
        this.boxProductDetail.setVisible(true);

        this.boxEndButtons.setVisible(true);
        this.buttonSave.setText("Crear");
        this.fieldName.requestFocus();
    }

    private void createProduct() {
        Loading.show();
        Model_Product modelProduct = new Model_Product();
        modelProduct.setName(InputProperties.capitalizeFirstLetter(this.fieldName.getText()));
        modelProduct.setPrice(new BigDecimal(this.fieldPrice.getText()));
        int idProduct = JDBC_Product.CreateProduct(modelProduct);
        if (idProduct > 0) {
            Notifications.Success("Producto", "El producto ha sido creado");
            clearForm(true);
            configureData();
            Loading.closeNow();
        }
    }

    private void eventEdit() {
        clearForm(false);
        this.boxProducts.setVisible(true);
        this.boxProducts.requestFocus();
        if (Application.GetModelAdmin().getIdAdmin() <= 2) this.boxButtonDelete.setVisible(true);
        this.buttonSave.setText("Guardar");
    }

    private void eventSave() {
        if (Validator.emptyValidator(this.fieldName, this.fieldPrice) && Validator.moneyValidator(this.fieldPrice, true)) {
            if (this.comboBoxProducts.getSelectionModel().getSelectedIndex() == -1) { // create product
                createProduct();
            } else { // save product
                saveProduct();
            }
        }
    }

    private void eventDelete() {
        Popup popup = new Popup();
        popup.password();
        if (popup.showAndWait()) {
            this.formChangeListener.change("productDelete", false);
        }
    }

    private void saveProduct() {
        Loading.show();
        if (this.formChangeListener.isListen()) {
            Model_Product newModelProduct = new Model_Product();
            newModelProduct.setIdProduct(this.modelProduct.getIdProduct());
            if (this.formChangeListener.isChanged("productDelete")) {
                boolean isOk = JDBC_Product.DeleteProduct(this.modelProduct.getIdProduct());
                if (isOk) {
                    clearForm(true);
                    Notifications.Warn("Productos", "Producto eliminado");
                    Loading.closeNow();
                }
            } else {
                if (this.formChangeListener.isChanged("name")) {
                    newModelProduct.setName(InputProperties.capitalizeFirstLetter(this.fieldName.getText()));
                }
                if (this.formChangeListener.isChanged("price")) {
                    newModelProduct.setPrice(new BigDecimal(this.fieldPrice.getText()));
                }
                boolean isOk = JDBC_Product.UpdateProduct(newModelProduct);
                if (isOk) {
                    Platform.runLater(() -> {
                        configureData();
                        clearForm(true);
                        Loading.closeNow();
                    });
                    Notifications.Success("Productos", "Nuevos cambios aplicados");
                }
            }
        }
    }

    private void createFormChangeListener() {
        this.formChangeListener = new FormChangeListener(this.buttonSave);
        this.formChangeListener.add("name");
        this.formChangeListener.add("price");
        this.formChangeListener.add("productDelete");
        this.fieldName.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "name",
                        Validator.compare(this.fieldName.getText(), this.modelProduct.getName())
                );
            }
        });
        this.fieldPrice.setOnKeyTyped(keyEvent -> {
            if (this.formChangeListener.isListen()) {
                this.formChangeListener.change(
                        "price",
                        Validator.compare(this.fieldPrice.getText(), this.modelProduct.getPrice().toString())
                );
            }
        });
    }
}
