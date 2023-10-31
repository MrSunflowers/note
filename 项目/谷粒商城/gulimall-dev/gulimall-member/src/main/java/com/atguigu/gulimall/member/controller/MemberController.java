package com.atguigu.gulimall.member.controller;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.to.member.MemberUserLoginTO;
import com.atguigu.common.to.member.MemberUserRegisterTO;
import com.atguigu.common.to.member.WBSocialUserTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.exception.PhoneException;
import com.atguigu.gulimall.member.exception.UsernameException;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:56:37
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    /**
     * openFeign测试接口
     */
    @RequestMapping("/coupons")
    public R test() {
        MemberEntity entity = new MemberEntity();
        entity.setNickname("张三");

        R membercoupons = couponFeignService.membercoupons();
        Object coupons = membercoupons.get("coupons");
        return R.ok().put("member", entity).put("coupons", coupons);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 注册
     */
    @PostMapping("/regist")
    public R regist(@RequestBody MemberUserRegisterTO user) {
        try {
            memberService.regist(user);
            return R.ok();
        } catch (PhoneException ex) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION);
        } catch (UsernameException ex) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION);
        } catch (Exception ex) {
            return R.error(ex.getMessage());
        }

    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberUserLoginTO user) {
        try {
            MemberEntity entity = memberService.login(user);
            if (entity == null) {
                return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION);
            }
            return R.ok().setData(entity);
        } catch (Exception ex) {
            return R.error(ex.getMessage());
        }

    }

    /**
     * 微博社交登录
     */
    @PostMapping("/weibo/oauth2/login")
    public R oauthLogin(@RequestBody WBSocialUserTO user) {
        try {
            MemberEntity entity = memberService.login(user);
            return R.ok().setData(entity);
        } catch (Exception ex) {
            return R.error(ex.getMessage());
        }
    }

}
