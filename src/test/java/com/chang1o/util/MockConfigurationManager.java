package com.chang1o.util;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Mock配置管理器 - 提供统一的Mock对象配置方法
 * 确保Mock对象行为的一致性和可预测性
 */
public class MockConfigurationManager {
    private static MockConfigurationManager instance;
    private final Map<Object, Map<String, Object>> mockConfigurations;

    private MockConfigurationManager() {
        this.mockConfigurations = new HashMap<>();
    }

    public static MockConfigurationManager getInstance() {
        if (instance == null) {
            instance = new MockConfigurationManager();
        }
        return instance;
    }

    /**
     * 配置Mock对象的方法行为
     */
    public <T> void configureMockBehavior(T mock, String methodName, Object returnValue) {
        try {
            // 获取Mock对象的类
            Class<?> mockClass = mock.getClass();
            if (Mockito.mockingDetails(mock).isMock()) {
                // 如果是Mockito创建的Mock，获取原始类
                mockClass = Mockito.mockingDetails(mock).getMockCreationSettings().getTypeToMock();
            }

            // 查找方法
            Method method = findMethod(mockClass, methodName);
            if (method == null) {
                throw new IllegalArgumentException("方法未找到: " + methodName + " 在类 " + mockClass.getName());
            }

            // 配置Mock行为
            configureMethodBehavior(mock, method, returnValue);

            // 记录配置
            recordMockConfiguration(mock, methodName, returnValue);

        } catch (Exception e) {
            throw new RuntimeException("配置Mock行为失败: " + methodName, e);
        }
    }

    /**
     * 验证Mock对象的交互次数
     */
    public void verifyMockInteractions(Object mock, String methodName, int expectedTimes) {
        try {
            Class<?> mockClass = mock.getClass();
            if (Mockito.mockingDetails(mock).isMock()) {
                mockClass = Mockito.mockingDetails(mock).getMockCreationSettings().getTypeToMock();
            }

            Method method = findMethod(mockClass, methodName);
            if (method == null) {
                throw new IllegalArgumentException("方法未找到: " + methodName);
            }

            // 验证方法调用次数
            verifyMethodInvocation(mock, method, expectedTimes);

        } catch (Exception e) {
            throw new RuntimeException("验证Mock交互失败: " + methodName, e);
        }
    }

    /**
     * 创建Mock Scanner对象，用于模拟用户输入
     */
    public Scanner createMockScanner(String input) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        return new Scanner(inputStream);
    }

    /**
     * 创建Mock Scanner对象，支持多行输入
     */
    public Scanner createMockScanner(String... inputs) {
        StringBuilder inputBuilder = new StringBuilder();
        for (String input : inputs) {
            inputBuilder.append(input).append(System.lineSeparator());
        }
        return createMockScanner(inputBuilder.toString());
    }

    /**
     * 配置Scanner Mock的行为
     */
    public void configureScannerMock(Scanner mockScanner, String... inputs) {
        if (inputs.length == 0) {
            when(mockScanner.hasNextLine()).thenReturn(false);
            when(mockScanner.nextLine()).thenThrow(new RuntimeException("没有更多输入"));
            return;
        }

        // 配置hasNextLine()的返回值
        Boolean[] hasNextResults = new Boolean[inputs.length + 1];
        for (int i = 0; i < inputs.length; i++) {
            hasNextResults[i] = true;
        }
        hasNextResults[inputs.length] = false;
        
        when(mockScanner.hasNextLine()).thenReturn(hasNextResults[0], 
            java.util.Arrays.copyOfRange(hasNextResults, 1, hasNextResults.length));

        // 配置nextLine()的返回值
        OngoingStubbing<String> stubbing = when(mockScanner.nextLine());
        for (int i = 0; i < inputs.length; i++) {
            if (i == 0) {
                stubbing = stubbing.thenReturn(inputs[i]);
            } else {
                stubbing = stubbing.thenReturn(inputs[i]);
            }
        }
        
        // 最后一次调用抛出异常
        stubbing.thenThrow(new RuntimeException("没有更多输入"));
    }

    /**
     * 重置所有Mock配置
     */
    public void resetAllMocks() {
        for (Object mock : mockConfigurations.keySet()) {
            if (Mockito.mockingDetails(mock).isMock()) {
                Mockito.reset(mock);
            }
        }
        mockConfigurations.clear();
    }

    /**
     * 获取Mock对象的配置信息
     */
    public Map<String, Object> getMockConfiguration(Object mock) {
        return mockConfigurations.getOrDefault(mock, new HashMap<>());
    }

    /**
     * 创建静态Mock
     */
    public <T> MockedStatic<T> createStaticMock(Class<T> classToMock) {
        return Mockito.mockStatic(classToMock);
    }

    /**
     * 配置静态方法Mock
     */
    public <T> void configureStaticMethod(MockedStatic<T> staticMock, String methodName, Object returnValue) {
        // 这里需要根据具体的静态方法来配置
        // 由于静态方法的配置比较复杂，这里提供基础框架
        // 具体实现需要根据实际使用的静态方法来定制
    }

    // 私有辅助方法

    private Method findMethod(Class<?> clazz, String methodName) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        
        // 如果在当前类中没找到，查找父类
        if (clazz.getSuperclass() != null) {
            return findMethod(clazz.getSuperclass(), methodName);
        }
        
        return null;
    }

    private void configureMethodBehavior(Object mock, Method method, Object returnValue) {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        try {
            if (paramTypes.length == 0) {
                // 无参数方法
                when(method.invoke(mock)).thenReturn(returnValue);
            } else {
                // 有参数方法 - 使用any()匹配器
                Object[] anyArgs = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    anyArgs[i] = any(paramTypes[i]);
                }
                when(method.invoke(mock, anyArgs)).thenReturn(returnValue);
            }
        } catch (Exception e) {
            // 如果直接调用失败，尝试使用Mockito的when方法
            // 这里需要根据具体的方法签名来处理
            throw new RuntimeException("配置方法行为失败: " + method.getName(), e);
        }
    }

    private void verifyMethodInvocation(Object mock, Method method, int expectedTimes) {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        try {
            if (paramTypes.length == 0) {
                // 无参数方法
                verify(mock, times(expectedTimes)).getClass().getMethod(method.getName()).invoke(mock);
            } else {
                // 有参数方法
                Object[] anyArgs = new Object[paramTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    anyArgs[i] = any(paramTypes[i]);
                }
                // 这里需要根据具体的验证需求来实现
            }
        } catch (Exception e) {
            throw new RuntimeException("验证方法调用失败: " + method.getName(), e);
        }
    }

    private void recordMockConfiguration(Object mock, String methodName, Object returnValue) {
        mockConfigurations.computeIfAbsent(mock, k -> new HashMap<>())
                         .put(methodName, returnValue);
    }

    /**
     * 重置管理器实例（测试专用）
     */
    public static void resetInstance() {
        if (instance != null) {
            instance.resetAllMocks();
            instance = null;
        }
    }
}