package com.cict.core.util;

import at.favre.lib.bytes.Bytes;
import at.favre.lib.idmask.Config;
import at.favre.lib.idmask.IdMask;
import at.favre.lib.idmask.IdMasks;

import java.util.Properties;

public class IdConverter {

    public static final Properties defaultProperties = new Properties();

    private IdConverter() {
        throw new IllegalStateException("Utility class");
    }

    private static final byte[] ID_MASK_KEY = Bytes.parseHex(Constants.getProperty("application.id.mask")).array();
    private static final IdMask<Long> idMask = IdMasks.forLongIds(Config.builder(ID_MASK_KEY).randomizedIds(false).build());

    public static String fromId(long id) {
        return String.valueOf(id);
//        return idMask.mask(id);
    }

    public static long toId(String maskedId) {
        return Long.parseLong(maskedId);
        // if(maskedId == null || maskedId.isEmpty())
        //    return null;
//        try {
//            return idMask.unmask(maskedId);
//        } catch (Exception e) {
//            throw new RuntimeException("ID Encoding Error");
//        }

    }
}
