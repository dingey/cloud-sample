package com.d.exception;

import com.d.base.ResultCode;
import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

/**
 * 自定义异常,默认打印堆栈
 */
@Getter
@Setter
@SuppressWarnings("unused")
public class CheckedException extends RuntimeException {
    private int code;
    private String msg;

    public CheckedException() {
    }

    public CheckedException(String message) {
        super(message);
        this.msg = message;
        this.code = 1;
    }

    @Deprecated
    public CheckedException code(int code) {
        this.code = code;
        return this;
    }

    /**
     * 支持形如 “用户{0}不存在”等模板变量替换
     *
     * @param resultCode 通用返回代码
     * @param args       占位参数
     */
    public CheckedException(ResultCode resultCode, Object... args) {
        initResultCode(resultCode, args);
        fillInStackTrace();
    }

    public CheckedException(int code, String message) {
        this.code = code;
        this.msg = message;
        fillInStackTrace();
    }

    public void initResultCode(ResultCode resultCode, Object... args) {
        this.code = resultCode.getCode();
        if (args != null && args.length > 0) {
            this.msg = MessageFormat.format(resultCode.getMessage(), args);
        } else {
            this.msg = resultCode.getMessage();
        }
    }
}
