package com.andrew.secondkill.result;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/20
 */
public class CodeMsg {

    private int code;
    private String msg;

    /**
     * 通用异常
     */
    public static CodeMsg SUCCESS = new CodeMsg(0,"成功");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "非法请求");
    public static CodeMsg ACCESS_LIMIT_REACHED = new CodeMsg(500103, "访问太频繁");

    /**
     * 登录模块异常 500200
     */
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
    public static CodeMsg PASSWORD_UPDATE_ERROR = new CodeMsg(500216, "更新密码出错");

    /**
     * 商品模块异常 500300
     */

    /**
     * 订单模块异常 500400
     */
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");

    /**
     * 秒杀模块异常 500500
     */
    public static CodeMsg KILL_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
    public static CodeMsg REPEATE_KILL = new CodeMsg(500501, "不能重复秒杀");
    public static CodeMsg KILL_FAIL = new CodeMsg(500502, "秒杀失败");
    public static CodeMsg VERIFYCODE_WRONG = new CodeMsg(500503, "验证码错误");

    public CodeMsg fillArgs(Object... obj){
        this.code = code;
        String message = String.format(this.msg,obj);
        return new CodeMsg(code,message);
    }

    private CodeMsg(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
