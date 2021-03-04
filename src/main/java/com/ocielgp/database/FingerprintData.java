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
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT fingerprint, idMember FROM MEMBER_FINGERPRINTS");
            rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    Fmd bdFingerprint = UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
                    boolean fingerprintMatch = Fingerprint.compareFingerprint(fingerprint, bdFingerprint);
                    System.out.println(fingerprintMatch);
                } catch (UareUException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException throwables) {
            NotificationHandler.danger("Error", "[MembersData]: Error al crear un nuevo miembro.", 5);
            throwables.printStackTrace();
        }
        return 0;
    }
}
