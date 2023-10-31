package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.to.member.MemberUserRegisterTO;
import com.atguigu.common.to.member.WBSocialUserTO;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.auth.UserLoginVO;
import com.atguigu.common.vo.auth.UserRegisterVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 会员服务
 * @Author: wanzenghui
 * @Date: 2021/11/28 19:52
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 注册
     */
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegisterVO user);

    /**
     * 登录
     */
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVO vo);

    /**
     * 微博社交登录
     */
    @PostMapping("/member/member/weibo/oauth2/login")
    public R oauthLogin(@RequestBody WBSocialUserTO user);


}