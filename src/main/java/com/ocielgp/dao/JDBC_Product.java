package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Product;
import com.ocielgp.utilities.DateTime;
import com.ocielgp.utilities.Notifications;
import com.ocielgp.utilities.ParamBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class JDBC_Product {
    public static int CreateProduct(Model_Product modelProduct) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO PRODUCTS(name, price, createdBy) VALUE (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, modelProduct.getName()); // name
            ps.setBigDecimal(2, modelProduct.getPrice()); // price
            ps.setInt(3, Application.GetModelAdmin().getIdMember()); // idAdmin
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) { // return new id membership
                return rs.getInt(1);
            }
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return 0;
    }

    public static CompletableFuture<ObservableList<Model_Product>> ReadProducts() {
        return CompletableFuture.supplyAsync(() -> {
            Connection con = DataServer.GetConnection();
            ObservableList<Model_Product> modelProductsList = FXCollections.observableArrayList();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                String sql = "SELECT idProduct, name, price, createdAt, createdBy, updatedAt, updatedBy FROM PRODUCTS WHERE flag = 1";
                ps = con.prepareStatement(sql + " ORDER BY price");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Model_Product modelProduct = new Model_Product();
                    modelProduct.setIdProduct(rs.getInt("idProduct"));
                    modelProduct.setName(rs.getString("name"));
                    modelProduct.setPrice(rs.getBigDecimal("price"));
                    modelProduct.setCreatedAt(DateTime.MySQLToJava(rs.getString("createdAt")));
                    modelProduct.setCreatedBy(rs.getInt("createdBy"));
                    modelProduct.setUpdatedAt(DateTime.MySQLToJava(rs.getString("updatedAt")));
                    modelProduct.setUpdatedBy(rs.getInt("updatedBy"));
                    modelProductsList.add(modelProduct);
                }
                if (modelProductsList.isEmpty()) {
                    Notifications.Danger("Productos", "No hay productos registrados");
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return modelProductsList;
        });
    }

    public static boolean UpdateProduct(Model_Product modelProduct) {
        ParamBuilder paramBuilder = new ParamBuilder("PRODUCTS", "idProduct", modelProduct.getIdProduct());
        paramBuilder.addParam("name", modelProduct.getName());
        paramBuilder.addParam("price", modelProduct.getPrice());
        return paramBuilder.executeUpdate();
    }

    public static boolean DeleteProduct(int idProduct) {
        ParamBuilder paramBuilder = new ParamBuilder("PRODUCTS", "idProduct", idProduct);
        paramBuilder.addParam("updatedBy", Application.GetModelAdmin().getIdMember());
        paramBuilder.addParam("flag", 0);
        return paramBuilder.executeUpdate();
    }
}
