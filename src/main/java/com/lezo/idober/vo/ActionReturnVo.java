package com.lezo.idober.vo;

import java.io.Serializable;

import org.codehaus.jackson.map.annotate.JsonView;

import com.lezo.idober.view.ReturnView;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2014年11月25日
 */
public class ActionReturnVo implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Integer CODE_OK = 200;
    public static final String MSG_OK = "SUCCESS";
    public static final Integer CODE_PARAM = 400;
    public static final Integer CODE_FAIL = 500;

    @JsonView(ReturnView.StatusCode.class)
    private int code = CODE_OK;
    @JsonView(ReturnView.StatusCode.class)
    private String msg = MSG_OK;
    @JsonView(ReturnView.StatusCodeWithData.class)
    private Serializable data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }
}
