package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.models.Model_Product;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBC_Payment_Product {
    public static boolean CreatePaymentProduct(Model_Product modelProduct) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO PAYMENTS_PRODUCTS(price, idGym, idProduct, createdBy) VALUE (?, ?, ?, ?)");
            ps.setBigDecimal(1, modelProduct.getPrice()); // price
            ps.setInt(2, Application.GetCurrentGym().getIdGym()); // idGym
            ps.setInt(3, modelProduct.getIdProduct()); // idMembership
            ps.setInt(4, Application.GetModelAdmin().getIdMember()); // idAdmin
            ps.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
        } finally {
            DataServer.CloseConnection(con);
        }
        return false;
    }

}
