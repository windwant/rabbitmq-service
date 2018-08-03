package org.windwant.rabbitmq.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 18-8-3.
 */
public interface Constants {
    List<String> routeKeys = new ArrayList(){{
        add("dinfo");
        add("dwarning");
        add("derror");
    }};

    Map<String, String> routekey_msgtype = new HashMap(){{
        put("dinfo", "info");
        put("dwarning", "warning");
        put("derror", "error");
    }};
}
