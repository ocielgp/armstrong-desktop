package com.ocielgp.dao;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.app.UserPreferences;
import com.ocielgp.fingerprint.Fingerprint_Controller;
import com.ocielgp.utilities.Loading;
import com.ocielgp.utilities.Notifications;
import javafx.util.Pair;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.CompletableFuture;

public class JDBC_Member_Fingerprint {
    public static boolean isReaderAvailable = true;

    public static boolean CreateFingerprints(int idMember, ListIterator<Fmd> fingerprints) {
        Connection con = DataServer.GetConnection();
        try {
            PreparedStatement ps;
            assert con != null;
            ps = con.prepareStatement("DELETE FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
            ps.setInt(1, idMember);
            ps.executeUpdate(); // remove all previous fingerprints if exists

            while (fingerprints.hasNext()) {
                ps = con.prepareStatement("INSERT INTO MEMBERS_FINGERPRINTS(fingerprint, idMember) VALUE (?, ?)");
                ps.setBytes(1, fingerprints.next().getData());
                ps.setInt(2, idMember);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException sqlException) {
            Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            return false;
        } finally {
            DataServer.CloseConnection(con);
        }
    }

    synchronized public static void ReadFindFingerprint(Fmd fingerprint) {
        if (isReaderAvailable) {
            CompletableFuture.runAsync(() -> {
                Connection con = DataServer.GetConnection();
                try {
                    PreparedStatement ps;
                    ResultSet rs;
                    assert con != null;
                    ps = con.prepareStatement("SELECT MF.fingerprint, MF.idMember, A.idMember AS 'idAdmin' FROM MEMBERS_FINGERPRINTS MF JOIN PAYMENTS_MEMBERSHIPS PM on MF.idMember = PM.idMember LEFT JOIN ADMINS A on MF.idMember = A.idMember WHERE DATE_ADD(PM.endDateTime, INTERVAL ? DAY) >= CURDATE() ORDER BY PM.startDateTime");
                    ps.setInt(1, UserPreferences.GetPreferenceInt("MAX_DAYS_FINGERPRINTS"));
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        if (Fingerprint_Controller.CompareFingerprints(fingerprint, rs.getBytes("fingerprint"))) {
                            int idMember = rs.getInt("idMember");
                            if (rs.getBoolean("idAdmin")) {
                                JDBC_Check_In.ShowAdminInfo(idMember, 1);
                            } else {
                                JDBC_Check_In.ShowMemberInfo(idMember, 1);
                            }
                            return;
                        }
                    }
                    Notifications.Danger("gmi-fingerprint", "Lector de Huellas", "Huella no encontrada", 1.5);
                    Loading.closeNow();
                } catch (SQLException sqlException) {
                    Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
                } finally {
                    DataServer.CloseConnection(con);
                }
            });
        }
    }

    public static CompletableFuture<Pair<Integer, LinkedList<Fmd>>> ReadFingerprints(int idMember) {
        return CompletableFuture.supplyAsync(() -> {
            LinkedList<Fmd> fingerprints = new LinkedList<>();
            int fingerprintCounter = 0;
            Connection con = DataServer.GetConnection();
            try {
                PreparedStatement ps;
                ResultSet rs;
                assert con != null;
                ps = con.prepareStatement("SELECT fingerprint FROM MEMBERS_FINGERPRINTS WHERE idMember = ?");
                ps.setInt(1, idMember);
                rs = ps.executeQuery();

                while (rs.next()) {
                    try {
                        fingerprints.add(UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004));
                        fingerprintCounter++;
                    } catch (UareUException uareUException) {
                        Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], uareUException);
                    }
                }
            } catch (SQLException sqlException) {
                Notifications.CatchSqlException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], sqlException);
            } finally {
                DataServer.CloseConnection(con);
            }
            return new Pair<>(fingerprintCounter, fingerprints);
        });
    }


}