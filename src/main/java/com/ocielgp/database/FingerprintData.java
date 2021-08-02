package com.ocielgp.database;

import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import com.ocielgp.app.AppController;
import com.ocielgp.files.ConfigFiles;
import com.ocielgp.fingerprint.Fingerprint;
import com.ocielgp.utilities.DateFormatter;
import com.ocielgp.utilities.Notifications;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

public class FingerprintData {
    private static final int fingerprintsDaysLimit;

    static {
        fingerprintsDaysLimit = Integer.parseInt(Objects.requireNonNull(ConfigFiles.readProperty(ConfigFiles.File.APP, "fingerprintsDaysLimit")));
    }

    public static int searchFingerprint(Fmd fingerprint) {
        Connection con;
        PreparedStatement ps;
        ResultSet rs;
        try {
            con = DataServer.getConnection();
            ps = con.prepareStatement("SELECT MF.fingerprint AS 'fingerprint', MP.photo AS 'photo', M.idMember AS 'idMember', M.name AS 'name', M.lastName AS 'lastName', G.name AS 'gymName', PM.description AS 'description', PM.endDate AS 'endDate' FROM MEMBER_FINGERPRINTS MF JOIN MEMBERS M on MF.idMember = M.idMember LEFT JOIN MEMBER_PHOTOS MP on M.idMember = MP.idMember JOIN GYMS G on M.idGym = G.idGym JOIN PAYMENT_MEMBERSHIPS PM on M.idMember = PM.idMember WHERE DATE_ADD(PM.endDate, INTERVAL ? DAY) >= CURDATE() AND ( M.flag = 1 AND PM.flag = 1 ) ORDER BY PM.startDate DESC");
            ps.setInt(1, fingerprintsDaysLimit);
            rs = ps.executeQuery();

            boolean userFound = false;
            while (rs.next()) {
                try {
                    Fmd bdFingerprint = UareUGlobal.GetImporter().ImportFmd(rs.getBytes("fingerprint"), Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
                    boolean fingerprintMatch = Fingerprint.compareFingerprint(fingerprint, bdFingerprint);
                    if (fingerprintMatch) {
                        userFound = true;
                        String style = MembersData.getStyle(rs.getInt("idMember"));

                        AppController.showUserInfo(
                                style,
                                rs.getBytes("photo"),
                                rs.getString("idMember"),
                                rs.getString("name") + " " + rs.getString("lastName"),
                                rs.getString("gymName"),
                                rs.getString("description") + " (" + DateFormatter.getDateWithDayName(LocalDate.parse(rs.getString("endDate"))) + ")"
                        );

                        if (style.equals("creative-style")) {
                            // TODO: NECESITA PAGAR SU DEUDA
                        } else if (style.equals("danger-style")) {
                            // NO DEJARLO PASAR
                        } else {
                            Notifications.notify("gmi-verified-user", "Usuario encontrado", "Ingreso registrado.", 2);
                            MembersData.checkIn(rs.getInt("idMember"));
                            // TODO: GUARDAR REGISTRO EN LA BASE DE DATOS DEL INGRESO SI AUN NO VENCE
                        }
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
            if (!userFound) {
                Notifications.warn("Lector de Huellas", "Huella no encontrada", 2);
            }
            System.out.println("termino");
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
