package com.zboss.suiyuan.chat;

import java.util.HashMap;
import java.util.Map;

public class ChatConstant {
    public static final String IP_PORT = "http://101.200.127.223:8082/";
    public static final String DEFAULT_STR = "";
    
    public static final String PIC = "图片";
    public static final String SEND = "发送";
    
    
    public static final String UPLOADINFO = "uploadinfo";
    public static final String CLOSE_CON = "closecon";
    public static final String MORE_IMAGE = "moreimage";
    public static final String CRAWL_TYPE = "crawltypes";
    public static final String MORE_NEWS = "morenews";
    public static final String NEWS_CRAWL_TYPE = "newscrawltypes";
    public static final String UPLOAD_APP_INFO = "upload";
    

    public static final Map<String, String> PATH_MAP = new HashMap<String, String>();
    static {
        PATH_MAP.put(UPLOADINFO, "suiyuan/uploadinfo?");
        PATH_MAP.put(CLOSE_CON, "suiyuan/closecon?");
        PATH_MAP.put(MORE_IMAGE, "moreimage?");
        PATH_MAP.put(CRAWL_TYPE, "crawltypes?");
        PATH_MAP.put(MORE_NEWS, "morenews?");
        PATH_MAP.put(NEWS_CRAWL_TYPE, "newscrawltypes?");
        PATH_MAP.put(UPLOAD_APP_INFO, "appinfo/upload?");
    }
    

}
