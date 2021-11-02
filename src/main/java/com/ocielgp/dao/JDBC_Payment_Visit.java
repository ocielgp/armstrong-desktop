package com.ocielgp.dao;

import com.ocielgp.app.Application;
import com.ocielgp.models.Model_Membership;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBC_Payment_Visit {
    public static void CreatePaymentVisit(Model_Membership modelMembership) {
        Connection con = DataServer.getConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("INSERT INTO PAYMENTS_VISITS(price, idGym, idAdmin, idMembership) VALUE (?, ?, ?, ?)");
            ps.setBigDecimal(1, modelMembership.getPrice()); // price
            ps.setInt(2, Application.getCurrentGym().getIdGym()); // idGym
            ps.setInt(3, Application.getModelAdmin().getIdMember()); // idAdmin
            ps.setInt(4, modelMembership.getIdMembership()); // idMembership
            ps.executeUpdate();
        } catch (SQLException sqlException) {
            Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
        } finally {
            DataServer.closeConnection(con);
        }
    }

//    public static CompletableFuture<ArrayList<Model_Visit>> ReadPaymentsVisits() {
//        return CompletableFuture.supplyAsync(() -> {
//            Connection con = DataServer.getConnection();
//            ArrayList<Model_Visit> modelMembershipGuests = new ArrayList<>();
//            try {
//                PreparedStatement ps;
//                ResultSet rs;
//                assert con != null;
//                ps = con.prepareStatement("SELECT idMembershipGuest, price, name FROM MEMBERSHIPS_GUESTS WHERE flag = 1");
//                rs = ps.executeQuery();
//                while (rs.next()) {
//                    Model_Visit modelMembershipGuest = new Model_Visit();
//                    modelMembershipGuest.setIdVisit(rs.getInt("idMembershipGuest"));
//                    modelMembershipGuest.setPrice(rs.getBigDecimal("price"));
//                    modelMembershipGuest.setName(rs.getString("name"));
//                    modelMembershipGuests.add(modelMembershipGuest);
//                }
//                if (modelMembershipGuests.isEmpty()) {
//                    Notifications.Danger("Visitas", "No hay visitas registradas");
//                }
//            } catch (SQLException sqlException) {
//                Notifications.CatchError(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(), sqlException);
//            } finally {
//                DataServer.closeConnection(con);
//            }
//            return modelMembershipGuests;
//        });
//    }

}
