package com.atguigu.common.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class ObjectUtil {

    /**
     * 判断对象是否为空
     * null				-> true
     * ""				-> true
     * " "				-> true
     * "null"			-> true
     * empty Collection	-> true
     * empty Array		-> true
     * others			-> false
     *
     * @param object
     * @return
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return StringUtils.isBlank((String) object);
        } /*else if (object instanceof Long) { //增加long参数的判断
            return ((Long) object).longValue() < 0l;
        } */ else if (object instanceof Collection<?>) {
            return CollectionUtils.isEmpty((Collection<?>) object);
        } else if (object instanceof Map<?, ?>) {
            return CollectionUtils.isEmpty((Map<?, ?>) object);
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else {
            return false;
        }
    }

    /**
     * 判断对象不为空
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * 所有数组元素向上转型
     *
     * @param objs  转换前对象数组
     * @param clazz 转换后数组对象类型
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] cast(Object[] objs, Class<T> clazz) {
        int length = objs.length;
        if (length == 0) {
            return (T[]) new Object[0];
        }
        T[] newArr = (T[]) Array.newInstance(clazz, objs.length);
        for (int i = 0; i < length; i++) {
            newArr[i] = clazz.cast(objs[i]);
        }

        return newArr;
    }

    //---------------------------------------------------------------------
    // 对象类型判断
    //---------------------------------------------------------------------

    public static boolean isCollection(Object obj) {
        return obj instanceof Collection;
    }

    public static boolean isMap(Object obj) {
        return obj instanceof Map;
    }

    public static boolean isNumber(Object obj) {
        return obj instanceof Number;
    }

    public static boolean isBoolean(Object obj) {
        return obj instanceof Boolean;
    }

    public static boolean isEnum(Object obj) {
        return obj instanceof Enum;
    }

    public static boolean isDate(Object obj) {
        return obj instanceof Date || obj instanceof TemporalAccessor;
    }

    public static boolean isCharSequence(Object obj) {
        return obj instanceof CharSequence;
    }

    /**
     * 判断对象是否为八大基本类型包装类除外即(boolean, byte, char, short, int, long, float, and double)<br/>
     *
     * @param obj
     * @return
     */
    public static boolean isPrimitive(Object obj) {
        return obj != null && obj.getClass().isPrimitive();
    }

    /**
     * 判断对象是否为包装类或者非包装类的基本类型
     *
     * @param obj
     * @return
     */
    public static boolean isWrapperOrPrimitive(Object obj) {
        return isPrimitive(obj) || isNumber(obj) || isCharSequence(obj) || isBoolean(obj);
    }

    /**
     * 判断一个对象是否为数组
     *
     * @param obj
     * @return
     */
    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    /**
     * 判断一个对象是否为基本类型数组即(int[], long[], boolean[], double[]....)
     *
     * @param obj
     * @return
     */
    public static boolean isPrimitiveArray(Object obj) {
        return isArray(obj) && obj.getClass().getComponentType().isPrimitive();
    }

    /**
     * 拷贝Bean到Map
     */
    public static Map<String, String> copyBean2Map(Object obj) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(obj.getClass()))
                .filter(itm -> !"class".equals(itm.getName()))
                .collect(HashMap::new,
                        (map, pd) -> {
                            Method method = pd.getReadMethod();
                            method.setAccessible(true);
                            Object value = ReflectionUtils.invokeMethod(method, obj);
                            if (ObjectUtil.isNotEmpty(value)) {
                                map.put(pd.getName(), String.valueOf(value));
                            }
                        },
                        HashMap::putAll);
    }

    /**
     * 拷贝map到Bean中【解决了String无法转Date类型的问题】
     */
    public static <T> void copyMap2Bean(Map<String, String> map, T obj) throws InvocationTargetException, IllegalAccessException {
        if (isNotEmpty(obj)) {
            DateUtils.DateTimeConverter dateTimeConvert = new DateUtils.DateTimeConverter();
            ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
            convertUtilsBean.deregister(Date.class);//注销时间转换器
            convertUtilsBean.register(dateTimeConvert, Date.class);// 设置时间转换器
            BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
            beanUtilsBean.populate(obj, map);// 转换map->obj
        } else {
        }
    }

    /**
     * 从HttpServletRequest中获取parameter参数并用map包装
     */
    public static Map<String, String> getParamMap(HttpServletRequest request) {
        HashMap<String, String> map = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue()[0]);
        }
        return map;
    }

    /**
     * 获取驼峰命名Map
     */
    public static HashMap<String, String> getCamelCaseMap(Map<String, String> paramMap) {
        HashMap<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            map.put(convertName(entry.getKey()), entry.getValue());
        }
        return map;
    }

    /**
     * 下换线key转换驼峰
     */
    public static String convertName(String snakeCaseName) {
        if (!snakeCaseName.contains("_")) {
            return snakeCaseName;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] name = snakeCaseName.split("_");
        for (int i = 0; i < name.length; i++) {
            String s = name[i];
            if (i != 0) {
                s = toUpperFirstChar(s);
            }
            stringBuilder.append(s);
        }
        return stringBuilder.toString();
    }

    /**
     * 首字母大写
     */
    public static String toUpperFirstChar(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] -= 32;
        return String.valueOf(charArray);
    }

    /**
     * BeanMerge，对象属性合并【非null属性不拷贝】
     */
    public static <M> void merge(M source, M target) throws Exception {
        if (source == null) {
            return;
        }
        //获取目标bean
        BeanInfo beanInfo = Introspector.getBeanInfo(source.getClass());
        // 遍历所有属性
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            // 如果是可写属性
            if (descriptor.getWriteMethod() != null) {
                Object defaultValue = descriptor.getReadMethod().invoke(source);
                //可以使用StringUtil.isNotEmpty(defaultValue)来判断
                if (isNotEmpty(defaultValue)) {
                    //用非空的defaultValue值覆盖到target去
                    descriptor.getWriteMethod().invoke(target, defaultValue);
                }
            }
        }
    }

    public static boolean isDigit(String str) {
        if (null == str || str.length() == 0) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            int c = str.charAt(i);
            if (c < 48 || c > 57) {
                return false;
            }
        }
        return true;
    }

    // 将一个map对象转化为bean
    public static void transMap2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * map转实体类
     */
    public static Object convertMap(Class type, Map map) {
        Object obj = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            obj = type.newInstance();
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    Object value = map.get(propertyName);
                    descriptor.getWriteMethod().invoke(obj, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}