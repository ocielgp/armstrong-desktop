package com.ocielgp.database;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.model.MembersModel;
import com.ocielgp.utilities.NotificationHandler;

import java.sql.*;

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
                        NotificationHandler.sucess("Hola", rs.getString("name"), 3);
                        break;
                    }
                } catch (UareUException e) {
                    e.printStackTrace();
                }
            }
            NotificationHandler.warn("Lector de Huellas", "Huella no encontrada", 2);
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[FingerprintData][searchFingerprint]: Error al buscar huella.", 5);
            throwables.printStackTrace();
        }
        return 0;
    }
}
