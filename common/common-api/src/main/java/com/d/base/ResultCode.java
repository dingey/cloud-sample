package com.d.base;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(0, "成功"), //
    FAIL(1, "失败"),
    SERVER_ERROR(2, "系统错误，请稍后再试"),
    SERVER_BUSY(3, "服务器繁忙，请稍后再试"),
    NOT_FOUND(4, "资源不存在"),
    SERVER_FALLBACK(5, "服务熔断"),;

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultCode fromCode(int code) {
        for (ResultCode rc : values()) {
            if (rc.getCode() == code) {
                return rc;
            }
        }
        return null;
    }

    static boolean throwable(int code) {
        return code == SERVER_ERROR.getCode() || code == NOT_FOUND.getCode();
    }
}
