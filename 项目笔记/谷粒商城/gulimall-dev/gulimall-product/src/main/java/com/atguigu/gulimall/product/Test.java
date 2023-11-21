package com.atguigu.gulimall.product;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: wanzenghui
 * @Date: 2022/6/4 22:16
 */
public class Test {

    public static void main(String[] args) {
        String s = new String("123#456");
        String[] split = s.split("#");
        System.out.println(split[0]);
        System.out.println(split.length);


        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        map.put("11", "2");
        map.remove("11");

        String regx = "/^(?i)use\\s+.*/i";
        System.out.println("use database".matches(regx));

        System.out.println("----");

        String rr = "(?i)use\\s+\\S+";
        List<String> strings = Arrays.asList("use DDD", "USE x_x", "USE x_x 1", "USE 中文", "USE x_x1");
        strings.forEach(item -> System.out.println(item.matches(rr)));

        System.out.println("----");

        String rr2 = "(?i)set\\s+\\S+\\s+=\\s+\\S+";
        strings = Arrays.asList("SET autocommit=1", "SET autocommit = 1", "set autocommit = 1", "SET application_name = 'PostgreSQL JDBC Driver'",
                "SET search_path = \"$user\",privatedb,public");
        strings.forEach(item -> System.out.println(item.matches(rr2)));

        System.out.println("----");
        String rr3 = "^set(\\s+)(\\S+)";
        strings.forEach(item -> System.out.println(item.matches(rr3)));
        System.out.println("----");


        Pattern p = Pattern.compile("^(?i)SET(\\s+)(\\S+)");
        strings.forEach(item -> {
                    Matcher m = p.matcher(item);
                    System.out.println(m.find());
                }
        );
        System.out.println("----");

        Pattern compilePattern = Pattern.compile("^SET\\s+application_name\\s+=\\s+'(.+)'", Pattern.CASE_INSENSITIVE);
        Matcher matcher = compilePattern.matcher("SET application_name = 'DBeaver 7.1.2 - Main <testdb>'");
        // DEVELOPER-1084需求的特殊要求
        System.out.println(matcher.find());
        System.out.println(matcher.group(0));
        System.out.println(matcher.group(1));
    }
}