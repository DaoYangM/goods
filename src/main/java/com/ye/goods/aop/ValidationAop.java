package com.ye.goods.aop;

import com.ye.goods.common.ServerResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@Aspect
@Component
public class ValidationAop {

    @Pointcut("@annotation(com.ye.goods.anno.ValidateFields)")
    private void validation() {
    }

    @Around("validation()")
    public ServerResponse validate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        System.out.println("AOP");
        for (Object object : args) {
            if (object instanceof BindingResult) {
                BindingResult bindingResult = (BindingResult) object;
                if (bindingResult.hasErrors()) {
                    StringBuilder errors = new StringBuilder();
                    for (ObjectError error : bindingResult.getAllErrors()) {
                        errors.append(error.getDefaultMessage() + "; ");
                    }
                    return ServerResponse.ERROR_ILLEGAL_ARGUMENT(errors.toString());
                }
            }
        }
        return (ServerResponse) joinPoint.proceed();
    }
}
