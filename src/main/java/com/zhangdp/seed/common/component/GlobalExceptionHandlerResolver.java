package com.zhangdp.seed.common.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.common.exception.ForbiddenException;
import com.zhangdp.seed.common.exception.NotFoundException;
import com.zhangdp.seed.common.exception.UnauthorizedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2023/4/3
 * 全局异常处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlerResolver {

    /**
     * 全局异常.
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("全局异常信息：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage(), e);
        return new R<>().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMsg("系统异常：" + StrUtil.maxLength(e.getLocalizedMessage(), CommonConst.MAX_ERROR_MSG_LENGTH));
    }

    /**
     * AccessDeniedException
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("拒绝授权异常信息：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage(), e);
        return new R<>().setCode(HttpStatus.FORBIDDEN.value()).setMsg("访问受限：" + StrUtil.maxLength(e.getLocalizedMessage(), CommonConst.MAX_ERROR_MSG_LENGTH));
    }

    /**
     * validation Exception
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleValidException(Exception e, HttpServletRequest request) {
        List<FieldError> fieldErrors;
        if (e instanceof MethodArgumentNotValidException) {
            fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        } else {
            fieldErrors = ((BindException) e).getBindingResult().getFieldErrors();
        }
        String allErrorMsgs = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("、"));
        log.warn("参数校验错误异常信息：uri={}, ex={}", request.getRequestURI(), allErrorMsgs, e);
        String msg = fieldErrors.get(0).getDefaultMessage();
        return new R<>().setCode(HttpStatus.BAD_REQUEST.value()).setMsg("参数错误：" + msg);
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R constraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String allErrorMsgs = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("、"));
        log.warn("参数校验错误异常信息：uri={}, ex={}", request.getRequestURI(), allErrorMsgs, e);
        String msg = CollUtil.getFirst(e.getConstraintViolations()).getMessage();
        return new R<>().setCode(HttpStatus.BAD_REQUEST.value()).setMsg("参数错误：" + msg);
    }

    /**
     * servlet异常，httpMethod不对，contentType不对等
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handlerServletException(ServletException e, HttpServletRequest request) {
        log.warn("请求异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R<>().setCode(HttpStatus.BAD_REQUEST.value()).setMsg(e.getLocalizedMessage());
    }

    /**
     * 参数类型转换错误
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        String msg = "参数错误：" + e.getName() + "需为" + e.getRequiredType().getSimpleName() + "类型";
        return new R<>().setCode(HttpStatus.BAD_REQUEST.value()).setMsg(msg);
    }

    /**
     * 缺少参数异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少参数异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R<>().setCode(HttpStatus.BAD_REQUEST.value()).setMsg("参数异常：" + e.getParameterName() + "不能为空");
    }

    /**
     * 非法参数异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R illegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R<>().setCode(HttpStatus.BAD_REQUEST.value()).setMsg(e.getLocalizedMessage());
    }

    /**
     * 自定义业务异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R BizException(BizException e, HttpServletRequest request) {
        log.error("业务异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R().setCode(e.getCode()).setMsg(e.getMessage());
    }

    /**
     * 资源不存在异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R fstiNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.error("资源不存在异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R().setCode(e.getCode()).setMsg(e.getMessage());
    }

    /**
     * 未认证异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R fstiUnauthorizedException(UnauthorizedException e, HttpServletRequest request) {
        log.error("未认证异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R().setCode(e.getCode()).setMsg(e.getMessage());
    }

    /**
     * 权限不足异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R fstiForbiddenException(ForbiddenException e, HttpServletRequest request) {
        log.error("权限不足异常：uri={}, ex={}", request.getRequestURI(), e.getMessage(), e);
        return new R().setCode(e.getCode()).setMsg(e.getMessage());
    }

}
