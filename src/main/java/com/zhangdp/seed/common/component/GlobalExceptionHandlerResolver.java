package com.zhangdp.seed.common.component;

import cn.dev33.satoken.exception.NotLoginException;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.model.ParamsError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
public class GlobalExceptionHandlerResolver {

    /**
     * 全局异常
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("全局异常：uri=" + request.getRequestURI(), e);
        return new R<>(ErrorCode.SERVER_ERROR);
    }

    /**
     * 数据库异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({SQLException.class, BadSqlGrammarException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleSQLException(Exception e, HttpServletRequest request) {
        log.error("SQL异常：uri=" + request.getRequestURI(), e);
        return new R<>(ErrorCode.SQL_ERROR);
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
    public R<List<ParamsError>> handleValidException(Exception e, HttpServletRequest request) {
        List<ParamsError> errors;
        List<FieldError> fieldErrors;
        if (e instanceof MethodArgumentNotValidException) {
            fieldErrors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors();
        } else {
            fieldErrors = ((BindException) e).getBindingResult().getFieldErrors();
        }
        errors = new ArrayList<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            ParamsError pe = new ParamsError();
            pe.setField(fieldError.getField());
            pe.setMessage(fieldError.getDefaultMessage());
            errors.add(pe);
        }
        if (log.isInfoEnabled()) {
            log.info("参数校验错误异常信息：uri={}, errorMsg={}", request.getRequestURI(), errors.stream().map(ParamsError::getMessage).collect(Collectors.joining("、")));
        }
        return new R<>(ErrorCode.VALID_PARAMS_FAILED.code(),
                CollUtil.isEmpty(errors) ? "参数校验失败" : errors.get(0).getMessage(),
                errors);
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
    public R<List<ParamsError>> constraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        List<ParamsError> errors = e.getConstraintViolations()
                .stream()
                .map(v -> new ParamsError(null, v.getMessage()))
                .toList();
        if (log.isInfoEnabled()) {
            log.info("参数校验错误异常信息：uri={}, errorMsg={}", request.getRequestURI(),
                    errors.stream().map(ParamsError::getMessage).collect(Collectors.joining("、")));
        }
        return new R<>(ErrorCode.VALID_PARAMS_FAILED.code(),
                CollUtil.isEmpty(errors) ? "参数校验失败" : errors.get(0).getMessage(),
                errors);
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
    public R<?> handlerServletException(ServletException e, HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("ServletException：uri={}, errorMessage={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        return new R<>(ErrorCode.BAD_REQUEST);
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
    public R<?> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("参数类型异常：uri={}, ex={}", request.getRequestURI(), e.getMessage());
        String msg = "参数错误：" + e.getName() + "需为" + e.getRequiredType().getSimpleName() + "类型";
        return new R<>(HttpStatus.BAD_REQUEST.value(), msg);
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
    public R<?> missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少参数异常：uri={}, ex={}", request.getRequestURI(), e.getMessage());
        return new R<>(HttpStatus.BAD_REQUEST.value(), "参数异常：" + e.getParameterName() + "不能为空");
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
    public R<?> illegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常：uri={}, ex={}", request.getRequestURI(), e.getMessage());
        return new R<>(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
    }

    /**
     * 自定义业务异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> bizException(BizException e, HttpServletRequest request) {
        log.warn("业务异常：uri={}", request.getRequestURI(), e);
        return new R<>(e.getCode(), e.getLocalizedMessage());
    }

    /**
     * sa-token 未登录异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<?> notLoginException(NotLoginException e, HttpServletRequest request) {
        log.warn("未登陆异常：uri={}, ex={}", request.getRequestURI(), e.getMessage());
        return new R<>(e.getCode() <= 0 ? ErrorCode.UNAUTHORIZED.code() : e.getCode(), "请先登录");
    }

}
