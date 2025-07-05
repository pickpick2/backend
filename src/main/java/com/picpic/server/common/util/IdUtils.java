package com.picpic.server.common.util;

import com.github.f4b6a3.tsid.TsidCreator;

import java.util.UUID;

public class IdUtils {

    public static long generateTsid() {
        return TsidCreator.getTsid().toLong();
    }

    public static String generateRoomId() {
        return UUID.randomUUID().toString();
    }
}
