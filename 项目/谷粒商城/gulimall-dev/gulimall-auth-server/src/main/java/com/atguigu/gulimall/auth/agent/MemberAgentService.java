package com.atguigu.gulimall.auth.agent;

import com.atguigu.common.to.member.WBSocialUserTO;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.auth.WBSocialUserVO;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class MemberAgentService {

    @Autowired
    MemberFeignService memberFeignService;

    public R oauthLogin(WBSocialUserVO user) {
        WBSocialUserTO param = new WBSocialUserTO();
        param.setAccessToken(user.getAccess_token());
        param.setExpiresIn(user.getExpires_in());
        param.setRemindIn(user.getRemind_in());
        param.setIsRealName(user.getIsRealName());
        param.setUid(user.getUid());
        return memberFeignService.oauthLogin(param);
    }
}
