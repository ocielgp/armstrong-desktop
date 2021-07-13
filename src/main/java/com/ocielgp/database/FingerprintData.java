package com.ocielgp.database;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FingerprintData {
    public static int searchFingerprint(Fmd fingerprint) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            System.out.println("buscando huella...");
            con = DataServer.getConnection(); // TODO: ENHANCE QUERY, ordenar por fecha de registro
            ps = con.prepareStatement("SELECT MF.fingerprint, m.name, endDate FROM member_fingerprints MF JOIN payment_memberships pm on MF.idMember = pm.idMember JOIN members m on MF.idMember = m.idMember");
            rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    Fmd bdFingerprint = UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
                    boolean fingerprintMatch = Fingerprint.compareFingerprint(fingerprint, bdFingerprint);
                    if (fingerprintMatch) {
                        Notifications.success("Hola", rs.getString("name"), 3);
                        break;
                    }
                } catch (UareUException uareUException) {
                    Notifications.catchError(
                            MethodHandles.lookup().lookupClass().getSimpleName(),
                            Thread.currentThread().getStackTrace()[1],
                            uareUException.getMessage(),
                            uareUException
                    );
                    uareUException.printStackTrace();
                }
            }
            Notifications.warn("Lector de Huellas", "Huella no encontrada", 2);
        } catch (SQLException sqlException) {
            Notifications.catchError(
                    MethodHandles.lookup().lookupClass().getSimpleName(),
                    Thread.currentThread().getStackTrace()[1],
                    "[" + sqlException.getErrorCode() + "]: " + sqlException.getMessage(),
                    sqlException
            );
        }
        return 0;
    }
}
