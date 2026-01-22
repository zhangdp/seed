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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
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
 * 全局异常处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandleAdvice {

    public static final String ERROR = "系统异常，请稍后再试";

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
        log.warn("IllegalArgumentException：uri={}", request.getRequestURI(), e);
        return new R<>(ErrorCode.BAD_REQUEST.code(), StrUtil.defaultIfEmpty(e.getMessage(), ErrorCode.BAD_REQUEST.message()));
    }

    /**
     * redis 异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> redisConnectionFailureException(RedisConnectionFailureException e, HttpServletRequest request) {
        log.error("RedisConnectionFailureException：uri={}", request.getRequestURI(), e);
        return new R<>(ErrorCode.REDIS_ERROR.code(), ERROR);
    }

    /**
     * 数据库异常
     * <br>输出http状态码：500
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({SQLException.class, DataIntegrityViolationException.class, BadSqlGrammarException.class,
            CannotGetJdbcConnectionException.class, QueryTimeoutException.class, UncategorizedSQLException.class,
            JdbcUpdateAffectedIncorrectNumberOfRowsException.class, InvalidResultSetAccessException.class,
            IncorrectResultSetColumnCountException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> sqlException(Exception e, HttpServletRequest request) {
        log.error("SQLException：uri={}", request.getRequestURI(), e);
        return new R<>(ErrorCode.SQL_ERROR.code(), ERROR);
    }

    /**
     * 兜底中间件异常，包括数据库、redis等中间件
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> dataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error("DataAccessException：uri={}", request.getRequestURI(), e);
        return new R<>(ErrorCode.MIDDLEWARE_ERROR.code(), ERROR);
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
        log.warn("NoResourceFoundException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.NOT_FOUND);
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
        log.warn("MissingServletRequestParameterException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        String errMsg = e.getParameterName() + "不能为空";
        List<ParamsError> errors = new ArrayList<>();
        errors.add(new ParamsError(e.getParameterName(), errMsg));
        return new R<>(ErrorCode.PARAMETER_MISSING.code(), "参数错误：" + errMsg, errors);
    }

    /**
     * 兜底servlet异常，httpMethod不对，contentType不对等
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(ServletException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> servletException(ServletException e, HttpServletRequest request) {
        log.warn("ServletException：uri={}, error={}", request.getRequestURI(), e.getMessage());
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
        log.warn("HttpMessageNotReadableException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.REQUEST_BODY_NOT_READABLE);
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
        log.warn("ConstraintViolationException：uri={}, error={}", request.getRequestURI(), errMsg);
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
    public R<List<ParamsError>> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("MethodArgumentTypeMismatchException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        String msg = e.getName() + (e.getRequiredType() != null ? "需为" + e.getRequiredType().getSimpleName() + "类型" : "类型错误");
        List<ParamsError> errors = new ArrayList<>();
        errors.add(new ParamsError(e.getName(), msg));
        return new R<>(ErrorCode.PARAMETER_TYPE_ERROR.code(), "参数错误：" + msg, errors);
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<List<ParamsError>> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ParamsError> errors = new ArrayList<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            ParamsError pe = new ParamsError(fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(pe);
        }
        String errMsg = joinParamsErrors(errors);
        log.warn("MethodArgumentNotValidException：uri={}, error={}", request.getRequestURI(), errMsg);
        return new R<>(ErrorCode.PARAMS_VALID_FAILED.code(), "参数错误：" + errMsg, errors);
    }

    /**
     * 参数校验异常
     * <br>输出http状态码：400
     *
     * @param e
     * @param request
     * @return R
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<List<ParamsError>> bindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ParamsError> errors = new ArrayList<>(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            ParamsError pe = new ParamsError(fieldError.getField(), fieldError.getDefaultMessage());
            errors.add(pe);
        }
        String errMsg = joinParamsErrors(errors);
        log.warn("BindException：uri={}, error={}", request.getRequestURI(), errMsg);
        return new R<>(ErrorCode.PARAMS_VALID_FAILED.code(), "参数错误：" + errMsg, errors);
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
        log.warn("NotFoundException：uri={}, error={}", request.getRequestURI(), e.getMessage());
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
        log.warn("ForbiddenException：uri={}, error={}", request.getRequestURI(), e.getMessage());
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
        log.warn("UnauthorizedException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return new R<>(e.getCode(), e.getMessage());
    }

    /**
     * spring security 认证失败异常
     * <br>输出http状态码：401
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<?> authenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("AuthenticationException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        if (e instanceof BadCredentialsException) {
            return R.fail(ErrorCode.USERNAME_NOT_FOUND_OR_BAD_CREDENTIALS);
        } else if (e instanceof DisabledException) {
            return R.fail(ErrorCode.ACCOUNT_DISABLED);
        } else if (e instanceof LockedException) {
            return R.fail(ErrorCode.ACCOUNT_LOCKED);
        } else {
            return R.fail(ErrorCode.LOGIN_FAILURE);
        }
    }

    /**
     * spring security 权限不足异常
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<?> accessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("AccessDeniedException：uri={}, error={}", request.getRequestURI(), e.getMessage());
        return R.fail(ErrorCode.FORBIDDEN);
    }

    /**
     * 自定义异常即业务异常
     * <br>输出http状态码：200
     *
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(BizException.class)
    public R<?> bizException(BizException e, HttpServletRequest request) {
        log.warn("BizException：uri={}, code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage());
        return new R<>(e.getCode(), e.getMessage());
    }

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
        log.error("INTERNAL_SERVER_ERROR：uri={}", request.getRequestURI(), e);
        return new R<>(ErrorCode.SERVER_ERROR.code(), ERROR);
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
