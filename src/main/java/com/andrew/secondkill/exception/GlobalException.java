package com.andrew.secondkill.exception;

import com.andrew.secondkill.result.CodeMsg;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/23
 */
public class GlobalException extends RuntimeException{

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super(codeMsg.getMsg());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
