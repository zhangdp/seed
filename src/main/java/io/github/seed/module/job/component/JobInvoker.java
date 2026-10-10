package io.github.seed.module.job.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 按{@code beanName.methodName}反射调用spring bean
 * <br>只提供调用能力，不做校验、不写日志、不碰库；传入的目标找不到时直接抛异常，由编排层记进执行日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JobInvoker {

    private final ApplicationContext applicationContext;
    private final JsonMapper jsonMapper;

    /**
     * 调用任务目标
     *
     * @param invokeTarget 执行目标，格式beanName.methodName
     * @param params       方法参数，json数组；无参方法传null或空
     */
    public void invoke(String invokeTarget, String params) {
        if (invokeTarget == null || invokeTarget.isBlank()) {
            throw new IllegalArgumentException("执行目标为空");
        }
        int dot = invokeTarget.lastIndexOf('.');
        if (dot <= 0 || dot == invokeTarget.length() - 1) {
            throw new IllegalArgumentException("执行目标格式应为beanName.methodName：" + invokeTarget);
        }
        String beanName = invokeTarget.substring(0, dot);
        String methodName = invokeTarget.substring(dot + 1);
        Object bean = this.applicationContext.getBean(beanName);
        List<Object> rawArgs = this.parseParams(params);
        Method method = this.resolveMethod(bean, methodName, rawArgs.size());
        Object[] args = this.convertArgs(method, rawArgs);
        ReflectionUtils.makeAccessible(method);
        try {
            method.invoke(bean, args);
        } catch (Exception e) {
            // 目标方法内部抛出的异常包一层才是根因，抛出原始cause便于日志记录
            throw new IllegalStateException("任务执行异常：" + invokeTarget, e.getCause() == null ? e : e.getCause());
        }
    }

    /**
     * 解析json数组形式的参数
     *
     * @param params json数组字符串，为空时表示无参
     * @return 参数列表
     */
    private List<Object> parseParams(String params) {
        if (params == null || params.isBlank()) {
            return List.of();
        }
        return jsonMapper.readValue(params, new TypeReference<List<Object>>() {
        });
    }

    /**
     * 按方法名与参数个数找方法；存在重载时取第一个匹配，任务目标应避免写重载方法
     *
     * @param bean      目标bean
     * @param methodName 方法名
     * @param argCount  参数个数
     * @return 目标方法
     */
    private Method resolveMethod(Object bean, String methodName, int argCount) {
        Method matched = null;
        // 取的是代理对象也无所谓：代理类同样继承/实现了目标方法
        for (Method method : bean.getClass().getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == argCount) {
                matched = method;
                break;
            }
        }
        if (matched == null) {
            throw new IllegalArgumentException("找不到方法：" + bean.getClass().getName() + "." + methodName
                    + "(" + argCount + "个参数)");
        }
        return matched;
    }

    /**
     * 把json解析出来的参数转成方法声明的类型
     *
     * @param method 目标方法
     * @param args   原始参数
     * @return 转换后的参数
     */
    private Object[] convertArgs(Method method, List<Object> args) {
        Class<?>[] types = method.getParameterTypes();
        Object[] converted = new Object[types.length];
        for (int i = 0; i < types.length; i++) {
            Object value = args.get(i);
            if (value == null || types[i].isAssignableFrom(value.getClass())) {
                converted[i] = value;
            } else {
                converted[i] = jsonMapper.convertValue(value, types[i]);
            }
        }
        return converted;
    }
}
