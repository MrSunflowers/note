package com.atguigu.gulimall.order.aspect;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.exception.TokenException;
import com.atguigu.gulimall.order.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 处理防重复提交token切面
 */
@Slf4j
@Aspect
@Component
public class TokenAspect {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    // 切入点签名
    @Pointcut("@annotation(com.atguigu.gulimall.order.annotation.TokenVerify)")
    private void tokenPoint() {
    }

    // 环绕通知
    @Around(value = "tokenPoint()")
    public Object tokenVerify(ProceedingJoinPoint joinPoint) throws Throwable {
        //String token = request.getHeader(tokenUtil.IDEMPOTENT_TOKEN_HEADER_KEY);
        String token = request.getParameter(TokenUtil.IDEMPOTENT_TOKEN_parameter_KEY);
        //String sessionId = request.getSession().getId();
        PrintWriter printWriter = null;
        if (tokenUtil.verifyToken(token)) {
            // 验证成功
            return joinPoint.proceed();// 进入业务逻辑
        } else {
            try {
                // 验证失败
                response.setCharacterEncoding("utf-8");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                printWriter = response.getWriter();
                printWriter.write(BizCodeEnume.IDEMPOTENT_TOKEN_VERIFY_EXCEPTION.getMsg());
                printWriter.flush();
            } catch (Exception e) {
                log.error("处理token，返回错误信息时异常", e);
                throw new TokenException();
            } finally {
                if (printWriter != null) {
                    printWriter.close();
                }
            }
        }
        return null;
    }
}
