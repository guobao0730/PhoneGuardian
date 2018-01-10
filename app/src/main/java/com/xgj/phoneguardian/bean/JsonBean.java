package com.xgj.phoneguardian.bean;

/**
 * @author Administrator
 * @version $Rev$
 * @time 2016/8/6 0006 14:47
 * @des ${根据应用程序版本的JSON数据创建的对应的类}
 */
public class JsonBean {

    private String version;
    private String url;
    private String desc;


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
