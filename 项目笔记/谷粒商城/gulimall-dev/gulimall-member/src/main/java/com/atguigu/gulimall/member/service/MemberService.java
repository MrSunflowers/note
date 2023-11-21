package com.atguigu.gulimall.member.service;

import com.atguigu.common.to.member.MemberUserLoginTO;
import com.atguigu.common.to.member.MemberUserRegisterTO;
import com.atguigu.common.to.member.WBSocialUserTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.exception.PhoneException;
import com.atguigu.gulimall.member.exception.UsernameException;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:56:37
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册
     */
    void regist(MemberUserRegisterTO user) throws InterruptedException;

    /**
     * 校验手机号是否唯一
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 校验用户名是否唯一
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    /**
     * 登录
     */
    MemberEntity login(MemberUserLoginTO user);

    /**
     * 微博社交登录（登录和注册功能合并）
     */
    MemberEntity login(WBSocialUserTO user) throws Exception;
}

