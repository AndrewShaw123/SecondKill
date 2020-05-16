package com.andrew.secondkill.exception;

import com.andrew.secondkill.result.CodeMsg;
import com.andrew.secondkill.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Class
 *
 * @author andrew
 * @date 2020/3/23
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHander {


    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException exception = (GlobalException)e;
            return Result.error(exception.getCodeMsg());
        } else if(e instanceof BindException){
            BindException exception = (BindException) e;
            List<ObjectError> allErrors = exception.getAllErrors();
            ObjectError error = allErrors.get(0);
            String defaultMessage = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(defaultMessage));
        }
        return null;
    }

}
