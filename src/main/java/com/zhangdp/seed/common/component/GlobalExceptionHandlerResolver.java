package com.zhangdp.seed.common.component;

import cn.dev33.satoken.exception.*;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.model.ParamsError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;
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
        log.error("全局异常：uri=" + request.getRequestURI() + ", ex=" + e.getLocalizedMessage(), e);
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
        log.error("SQL异常：uri=" + request.getRequestURI() + ", ex=" + e.getLocalizedMessage(), e);
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
            log.info("参数校验错误异常信息：uri={}, ex={}", request.getRequestURI(), errors.stream().map(ParamsError::getMessage).collect(Collectors.joining("、")));
        }
        return new R<>(ErrorCode.PARAMS_VALID_FAILED.code(),
                CollUtil.isEmpty(errors) ? "参数错误" : errors.get(0).getMessage(),
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
            log.info("参数校验错误异常信息：uri={}, ex={}", request.getRequestURI(),
                    errors.stream().map(ParamsError::getMessage).collect(Collectors.joining("、")));
        }
        return new R<>(ErrorCode.PARAMS_VALID_FAILED.code(),
                CollUtil.isEmpty(errors) ? "参数错误" : errors.get(0).getMessage(),
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
        if (log.isInfoEnabled()) {
            log.info("参数类型异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        return new R<>(ErrorCode.PARAMETER_TYPE_ERROR, "参数错误：" + e.getName() + "需为" + e.getRequiredType().getSimpleName() + "类型");
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
        if (log.isInfoEnabled()) {
            log.info("缺少参数异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        return new R<>(ErrorCode.PARAMETER_MISSING, "参数错误：" + e.getParameterName() + "不能为空");
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
        log.warn("非法参数异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        return new R<>(ErrorCode.BAD_REQUEST.code(), StrUtil.defaultIfBlank(e.getLocalizedMessage(), ErrorCode.BAD_REQUEST.message()));
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
        if (log.isInfoEnabled()) {
            log.info("未登录异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        return SaTokenHelper.resolveNoLoginError(e);
    }

    /**
     * sa-token 未Http Basic认证
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NotBasicAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<?> notBasicAuthException(NotBasicAuthException e, HttpServletRequest request, HttpServletResponse response) {
        if (log.isInfoEnabled()) {
            log.info("未Http Basic认证异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        response.setHeader("WWW-Authenticate", "Basic");
        return SaTokenHelper.resolveError(e);
    }

    /**
     * sa-token 角色不满足
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> notRoleException(NotRoleException e, HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("角色不满足异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        return SaTokenHelper.resolveError(e);
    }

    /**
     * sa-token 权限不足
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> notPermissionException(NotPermissionException e, HttpServletRequest request) {
        if (log.isInfoEnabled()) {
            log.info("权限不足异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        }
        return SaTokenHelper.resolveError(e);
    }

    /**
     * sa-token 异常兜底
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(SaTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> saTokenException(SaTokenException e, HttpServletRequest request) {
        log.warn("SaToken异常：uri={}, ex={}", request.getRequestURI(), e.getLocalizedMessage());
        return SaTokenHelper.resolveError(e);
    }

}
