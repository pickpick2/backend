package com.picpic.server.common.util;

import com.github.f4b6a3.tsid.TsidCreator;

public class IdUtils {

    public static long generateId() {
        return TsidCreator.getTsid().toLong();
    }
}
