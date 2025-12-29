package io.github.seed.common.component;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.data.R;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.exception.ForbiddenException;
import io.github.seed.common.exception.NotFoundException;
import io.github.seed.common.exception.UnauthorizedException;
import io.github.seed.model.ParamsError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2023/4/3 全局异常处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandleAdvice {

    /**
     * 全局异常
     * <br>输出http状态码：500
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> exception(Exception e, HttpServletRequest request) {
        // 如果是spring security的AccessDeniedException异常则继续往外抛，由配置的AccessDeniedHandler处理
        if (e instanceof AccessDeniedException accessDeniedException) {
            throw accessDeniedException;
        }
        log.error("全局异常：uri={}", request.getRequestURI(), e);
        return R.fail(ErrorCode.SERVER_ERROR);
    }

    /**
     * 非法异常，正常是断言产生的
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> illegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法异常：uri={}", request.getRequestURI(), e);
        return new R<>(ErrorCode.BAD_REQUEST.code(), StrUtil.defaultIfEmpty(e.getMessage(), ErrorCode.BAD_REQUEST.message()));
    }

    /**
     * 数据库异常
     * <br>输出http状态码：500
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({SQLException.class, BadSqlGrammarException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> sqlException(Exception e, HttpServletRequest request) {
        log.error("SQL异常：uri={}", request.getRequestURI(), e);
        return R.fail(ErrorCode.SQL_ERROR);
    }


    /**
     * 请求路径不存在异常
     * <br>输出http状态码：404
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> noResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("请求路径不存在异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.NOT_FOUND);
    }

    /**
     * 请求http method不对
     * <br>输出http状态码：405
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求http method不符合异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return new R<>(ErrorCode.BAD_REQUEST.code(), "请求Http Method \"" + e.getMethod() + "\"不支持");
    }

    /**
     * servlet异常，httpMethod不对，contentType不对等
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> servletException(ServletException e, HttpServletRequest request) {
        log.warn("servlet异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.BAD_REQUEST);
    }

    /**
     * 读取请求体异常
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> httpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("Request Body不可读异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.REQUEST_BODY_NOT_FOUND);
    }

    /**
     * 参数校验异常
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<List<ParamsError>> validException(Exception e, HttpServletRequest request) {
        List<ParamsError> errors;
        List<FieldError> fieldErrors;
        if (e instanceof MethodArgumentNotValidException) {
            fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        } else {
            fieldErrors = ((BindException) e).getBindingResult().getFieldErrors();
        }
        errors = new ArrayList<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            ParamsError pe = new ParamsError(fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(pe);
        }
        String errMsg = joinParamsErrors(errors);
        log.warn("参数校验错误异常信息：uri={}, error={}", request.getRequestURI(), errMsg);
        return new R<>(ErrorCode.PARAMS_VALID_FAILED.code(), "参数错误：" + errMsg, errors);
    }

    /**
     * 参数校验异常
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<List<ParamsError>> constraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        List<ParamsError> errors = e.getConstraintViolations()
                .stream()
                .map(v -> new ParamsError(null, v.getMessage()))
                .toList();
        String errMsg = joinParamsErrors(errors);
        log.warn("参数校验错误异常信息：uri={}, error={}", request.getRequestURI(), errMsg);
        return new R<>(ErrorCode.PARAMS_VALID_FAILED.code(), "参数错误：" + errMsg);
    }

    /**
     * 参数类型转换错误
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        String msg = e.getName() + (e.getRequiredType() != null ? "需为" + e.getRequiredType().getSimpleName() + "类型" : "类型错误");
        List<ParamsError> errors = new ArrayList<>();
        errors.add(new ParamsError(e.getName(), msg));
        return new R<>(ErrorCode.PARAMETER_TYPE_ERROR.code(), "参数错误：" + msg, errors);
    }

    /**
     * 缺少参数异常
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少参数异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        String errMsg = e.getParameterName() + "不能为空";
        List<ParamsError> errors = new ArrayList<>();
        errors.add(new ParamsError(e.getParameterName(), errMsg));
        return new R<>(ErrorCode.PARAMETER_MISSING.code(), "参数错误：" + errMsg, errors);
    }

    /**
     * 资源不存在异常
     * <br>输出http状态码：404
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> notFoundException(NotFoundException e, HttpServletRequest request) {
        log.warn("资源不存在异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return new R<>(e.getCode(), e.getMessage());
    }

    /**
     * 权限不足异常
     * <br>输出http状态码：403
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> forbiddenException(ForbiddenException e, HttpServletRequest request) {
        log.warn("权限不足异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return new R<>(e.getCode(), e.getMessage());
    }

    /**
     * 未登录异常
     * <br>输出http状态码：401
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<?> unauthorizedException(UnauthorizedException e, HttpServletRequest request) {
        log.warn("未登录异常：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return new R<>(e.getCode(), e.getMessage());
    }

    /**
     * 自定义异常即业务异常
     * <br>输出http状态码：409
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public R<?> bizException(BizException e, HttpServletRequest request) {
        log.warn("自定义业务异常：uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage());
        return new R<>(e.getCode(), e.getMessage());
    }

    /**
     * 参数错误列表连接成单个字符串描述
     *
     * @param errors
     * @return
     */
    private String joinParamsErrors(List<ParamsError> errors) {
        return errors.stream().map(ParamsError::getMessage).collect(Collectors.joining("、"));
    }

}
