package com.zboss.suiyuan.enums;

public enum TitleEnum {
    BUILD_CONNECTION("1", "建立连接"), CLOSE_CONNECTION("2", "释放连接"), PIC_SUCCESS("3", "图片上传成功"), PIC_FAIL("4", "图片上传失败");

    TitleEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private String status;
    private String desc;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
