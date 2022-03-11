package com.ocielgp.utilities;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Fingerprint_Log {
    public static final String logFileName = "fingerprint";
    private static final Logger LOGGER = Logger.getLogger(Fingerprint_Log.logFileName);

    static {
        Fingerprint_Log.LOGGER.setUseParentHandlers(false);

        try {
            FileHandler fileHandler = new FileHandler(Fingerprint_Log.logFileName + ".log", true);

            Fingerprint_Log.LOGGER.addHandler(fileHandler);
            fileHandler.setFormatter(new Formatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public String format(LogRecord record) {
                    return String.format(
                            format,
                            System.currentTimeMillis(),
                            record.getLevel().getLocalizedName(),
                            record.getMessage()
                    );
                }
            });

        } catch (SecurityException | IOException exception) {
            Notifications.CatchException(MethodHandles.lookup().lookupClass().getSimpleName(), Thread.currentThread().getStackTrace()[1], exception);
        }
    }

    public static void generateLog(String content) {
        Fingerprint_Log.LOGGER.info(content);
    }
}
