package io.github.seed.common.util;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * 2025/1/21 spring SpEL表达式工具类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class SpELUtils {

    private static final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    /**
     * 解析并获取表达式的值
     *
     * @param expressionStr 要解析的表达式
     * @param rootObject    根对象
     * @param resultType    返回值的类型
     * @param <T>           返回值的泛型类型
     * @return 表达式的解析结果
     */
    public static <T> T parseExpression(String expressionStr, Object rootObject, Class<T> resultType) {
        Expression expression = spelExpressionParser.parseExpression(expressionStr);
        return expression.getValue(rootObject, resultType);
    }

    /**
     * 解析并获取表达式的值
     *
     * @param expressionStr 要解析的表达式
     * @param variables     上下文变量
     * @param resultType    返回值的类型
     * @param <T>           返回值的泛型类型
     * @return 表达式的解析结果
     */
    public static <T> T parseExpression(String expressionStr, Map<String, Object> variables, Class<T> resultType) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        variables.forEach(evaluationContext::setVariable);
        Expression expression = spelExpressionParser.parseExpression(expressionStr);
        return expression.getValue(evaluationContext, resultType);
    }

    /**
     * 解析并获取表达式的值（无上下文）
     *
     * @param expressionStr 要解析的表达式
     * @param resultType    返回值的类型
     * @param <T>           返回值的泛型类型
     * @return 表达式的解析结果
     */
    public static <T> T parseExpression(String expressionStr, Class<T> resultType) {
        Expression expression = spelExpressionParser.parseExpression(expressionStr);
        return expression.getValue(resultType);
    }

}
