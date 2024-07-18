package com.kivi.huidada.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kivi.huidada.model.dto.scoring_result.ScoringResultAddRequestDTO;
import com.kivi.huidada.model.dto.scoring_result.ScoringResultItem;
import com.kivi.huidada.model.entity.ScoringResult;
import com.kivi.huidada.model.entity.User;
import com.kivi.huidada.service.ScoringResultService;
import com.kivi.huidada.mapper.ScoringResultMapper;
import com.kivi.huidada.service.UserService;
import org.apache.ibatis.annotations.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author Kivi
* @description 针对表【scoring_result(评分结果表)】的数据库操作Service实现
* @createDate 2024-07-03 14:49:05
*/
@Service
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult>
    implements ScoringResultService{
    @Resource
    private UserService userService;

    @Override
    public Boolean add(ScoringResultAddRequestDTO scoringResultAddRequestDTO, HttpServletRequest request) {
        // 获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        // 转成实体类
        for(ScoringResultItem scoringResultItem : scoringResultAddRequestDTO.getScoringResults()){
            ScoringResult scoringResult = new ScoringResult();
            BeanUtils.copyProperties(scoringResultItem, scoringResult);
            scoringResult.setTestPaperId(scoringResultAddRequestDTO.getTestPaperId());
            scoringResult.setResultProp(JSONUtil.toJsonStr(scoringResultItem.getResultProp()));
            scoringResult.setUserId(loginUser.getId());
            if(!this.save(scoringResult))return false;
        }
        return true;
    }
}




