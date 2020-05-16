package com.andrew.secondkill.result;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/20
 */
public class Result <T> {

    private int code;
    private String msg;
    private T data;

    /**
     * 成功返回 只考虑数据
     * @param data 返回的数据
     * @param <T>
     * @return 封装返回结果
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * 失败返回 考虑失败码和失败信息
     * @param codeMsg 失败码和失败信息
     * @param <T>
     * @return 封装返回结果
     */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    private Result(T data){
        this.code = CodeMsg.SUCCESS.getCode();
        this.msg = CodeMsg.SUCCESS.getMsg();
        this.data = data;
    }

    private Result(CodeMsg codeMsg){
        if(codeMsg==null){
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
