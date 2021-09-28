/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 */

package com.huawei.apm.bootstrap.interceptors;

import com.huawei.apm.bootstrap.common.BeforeResult;

import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import org.apache.skywalking.apm.agent.core.plugin.interceptor.enhance.StaticMethodsAroundInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * <p>将skyWalking的拦截器定义{@link StaticMethodsAroundInterceptor}, {@link InstanceConstructorInterceptor}, {@link InstanceMethodsAroundInterceptor}
 * 转换成 {@link Interceptor}</p>
 * <p>提供三个静态方法用于转换,分别如下: </p>
 * <p>{@link InterceptorAdapterFactory#adapter(StaticMethodsAroundInterceptor)} 得到 {@link StaticMethodInterceptor}</p>
 * <p>{@link InterceptorAdapterFactory#adapter(InstanceConstructorInterceptor)} 得到 {@link ConstructorInterceptor}</p>
 * <p>{@link InterceptorAdapterFactory#adapter(InstanceMethodsAroundInterceptor)} 得到 {@link InstanceMethodInterceptor}</p>
 *
 * @author y30010171
 * @since 2021-09-28
 **/
public class InterceptorAdapterFactory {

    public static Interceptor adapter(StaticMethodsAroundInterceptor staticMethodsAroundInterceptor) {
        return new StaticMethodInterceptorAdapter(staticMethodsAroundInterceptor);
    }

    public static Interceptor adapter(InstanceConstructorInterceptor instanceConstructorInterceptor) {
        return new ConstructorInterceptorAdapter(instanceConstructorInterceptor);
    }

    public static Interceptor adapter(InstanceMethodsAroundInterceptor instanceMethodsAroundInterceptor) {
        return new InstanceMethodInterceptorAdapter(instanceMethodsAroundInterceptor);
    }

    public static Class[] getAllClasses(Object[] allObjects) {
        if (allObjects == null || allObjects.length == 0) {
            return new Class[0];
        }
        Class[] allClasses = new Class[allObjects.length];
        for (int i = 0; i < allObjects.length; i++) {
            allClasses[i] = allObjects[i].getClass();
        }
        return allClasses;
    }

    static class StaticMethodInterceptorAdapter implements StaticMethodInterceptor {

        private final StaticMethodsAroundInterceptor staticMethodsAroundInterceptor;

        private StaticMethodInterceptorAdapter(StaticMethodsAroundInterceptor staticMethodsAroundInterceptor) {
            this.staticMethodsAroundInterceptor = staticMethodsAroundInterceptor;
        }

        @Override
        public void before(Class<?> clazz, Method method, Object[] arguments, BeforeResult beforeResult) throws Exception {
            MethodInterceptResult methodInterceptResult = new MethodInterceptResult();
            staticMethodsAroundInterceptor.beforeMethod(clazz, method, arguments, getAllClasses(arguments), methodInterceptResult);
            if (!methodInterceptResult.isContinue()) {
                beforeResult.setResult(methodInterceptResult._ret());
            }
        }

        @Override
        public Object after(Class<?> clazz, Method method, Object[] arguments, Object result) throws Exception {
            return staticMethodsAroundInterceptor.afterMethod(clazz, method, arguments, getAllClasses(arguments), result);
        }

        @Override
        public void onThrow(Class<?> clazz, Method method, Object[] arguments, Throwable t) {
            staticMethodsAroundInterceptor.handleMethodException(clazz, method, arguments, getAllClasses(arguments), t);
        }
    }

    static class ConstructorInterceptorAdapter implements ConstructorInterceptor {

        private final InstanceConstructorInterceptor instanceConstructorInterceptor;

        private ConstructorInterceptorAdapter(InstanceConstructorInterceptor instanceConstructorInterceptor) {
            this.instanceConstructorInterceptor = instanceConstructorInterceptor;
        }

        @Override
        public void onConstruct(Object obj, Object[] allArguments) {
            instanceConstructorInterceptor.onConstruct((EnhancedInstance) obj, allArguments);
        }
    }

    static class InstanceMethodInterceptorAdapter implements InstanceMethodInterceptor {

        private final InstanceMethodsAroundInterceptor instanceMethodsAroundInterceptor;

        private InstanceMethodInterceptorAdapter(InstanceMethodsAroundInterceptor instanceMethodsAroundInterceptor) {
            this.instanceMethodsAroundInterceptor = instanceMethodsAroundInterceptor;
        }

        @Override
        public void before(Object obj, Method method, Object[] arguments, BeforeResult beforeResult) throws Exception {
            MethodInterceptResult methodInterceptResult = new MethodInterceptResult();
            try {
                instanceMethodsAroundInterceptor.beforeMethod((EnhancedInstance) obj, method, arguments, getAllClasses(arguments), methodInterceptResult);
            } catch (Throwable throwable) {
                throw new Exception(throwable);
            }
            if (!methodInterceptResult.isContinue()) {
                beforeResult.setResult(methodInterceptResult._ret());
            }
        }

        @Override
        public Object after(Object obj, Method method, Object[] arguments, Object result) throws Exception {
            try {
                return instanceMethodsAroundInterceptor.afterMethod((EnhancedInstance) obj, method, arguments, getAllClasses(arguments), result);
            } catch (Throwable throwable) {
                throw new Exception(throwable);
            }
        }

        @Override
        public void onThrow(Object obj, Method method, Object[] arguments, Throwable t) {
            instanceMethodsAroundInterceptor.handleMethodException((EnhancedInstance) obj, method, arguments, getAllClasses(arguments), t);
        }
    }
}
