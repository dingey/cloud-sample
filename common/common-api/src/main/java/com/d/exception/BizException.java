package com.d.exception;

import com.d.base.ResultCode;

/**
 * 业务异常，不打印堆栈
 */
public class BizException extends CheckedException {
    public BizException(String message) {
        super(message);
    }

    public BizException(ResultCode resultCode, Object... args) {
        initResultCode(resultCode, args);
    }

    public BizException(int code, String message) {
        super(code, message);
    }
}