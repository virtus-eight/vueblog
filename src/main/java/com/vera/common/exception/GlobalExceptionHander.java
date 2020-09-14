package com.vera.common.exception;


import com.vera.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
/*拦截异常并统一处理*/
@RestControllerAdvice
public class GlobalExceptionHander {
    /*抛出异常代码给前端*/
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    /*捕获运行期异常*/
    @ExceptionHandler(value = ShiroException.class)
    public Result handler(ShiroException e){
        log.error("运行时异常：------------{}");
        return Result.fail("401",e.getMessage(),null);
    }

    /*抛出异常代码给前端*/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /*捕获运行期异常*/
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e){
        log.error("实体校验时异常：------------{1}");
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(objectError.getDefaultMessage());
    }

    /*抛出异常代码给前端*/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /*捕获运行期异常*/
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e){
        log.error("IllegalArgumentException");
        Result fail = Result.fail(e.getMessage());
        System.out.println(fail);
        return fail;
    }


    /*抛出异常代码给前端*/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    /*捕获运行期异常*/
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e){
        log.error("运行时异常：------------{}");
        return Result.fail(e.getMessage());
    }
}
