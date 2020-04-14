package com.d.base;

import com.d.exception.CheckedException;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@SuppressWarnings({"unchecked", "unused"})
public class Result<T> {
    @ApiModelProperty("错误代码：0成功；其他失败")
    private int code;

    @ApiModelProperty("错误提示")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @ApiModelProperty("数据")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public Result() {
    }

    Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public T getData() {
        if (ResultCode.throwable(code)) {
            throw new CheckedException(code, message);
        } else {
            return data;
        }
    }

    public static <T> Result success(T t) {
        return new Result(0, null, t);
    }

    public static <T> Result fail(String message) {
        return new Result(1, message, null);
    }

    public static <T> Result error(String message) {
        return new Result(1, message, null);
    }

    public static <T> Result code(ResultCode code) {
        return new Result(code.getCode(), code.getMessage(), null);
    }

    public static <T> Result fail(int code, String message) {
        return new Result(code, message, null);
    }

    public static <T> Result fail(CheckedException e) {
        return new Result(e.getCode(), e.getMessage(), null);
    }

    public static <T> Result fallback(T t) {
        RequestContext.setFallback();
        return new Result(ResultCode.SERVER_FALLBACK.getCode(), "服务熔断", t);
    }
}
