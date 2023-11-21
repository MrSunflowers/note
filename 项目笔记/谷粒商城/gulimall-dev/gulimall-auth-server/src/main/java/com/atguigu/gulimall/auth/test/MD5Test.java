package com.atguigu.gulimall.auth.test;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author: wanzenghui
 * @Date: 2021/11/28 19:13
 */
public class MD5Test {

    public static void main(String[] args) {
        // MD5
        System.out.println(DigestUtils.md5Hex("123456"));// 固定MD5值：e10adc3949ba59abbe56e057f20f883e

        // MD5 + salt
        System.out.println(Md5Crypt.md5Crypt("123456".getBytes()));// 随机盐值，随机MD5值：【盐值：USI.JoH2】【MD5值：$1$USI.JoH2$6hK88QXt9ijipsa/VcnbR0】
        System.out.println(Md5Crypt.md5Crypt("123456".getBytes()));// 随机盐值，随机MD5值：【盐值：tCYQRfTB】【MD5值：$1$tCYQRfTB$thopJ/8DcRSObDwXuKxvn1】
        System.out.println(Md5Crypt.md5Crypt("123456".getBytes(), "$1$123"));// 固定盐值，固定MD5值：【盐值：123】【MD5值：$1$123$7mft0jKnzzvAdU4t0unTG1】
        System.out.println(Md5Crypt.md5Crypt("123456".getBytes(), "$1$123"));// 固定盐值，固定MD5值：【盐值：123】【MD5值：$1$123$7mft0jKnzzvAdU4t0unTG1】

        // spring的MD5处理
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword1 = passwordEncoder.encode("123456");//$2a$10$s0yQ/Tz1aiexGqQGBNgmDuUFpCPjMx8L7TvJ60i9mQSBEmNXbSFEO
        String encodedPassword2 = passwordEncoder.encode("123456");//$2a$10$eXhMUTIjoS4cpCB3FRjhlu0QYGwTRgh93CefQSk48hPpvQzzDAvIS
        System.out.println(passwordEncoder.matches("123456", encodedPassword1));// 校验结果true
        System.out.println(passwordEncoder.matches("123456", encodedPassword2));// 校验结果true

    }
}