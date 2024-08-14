package com.kivi.huidada.mapper;

import com.kivi.huidada.model.entity.TestPaper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kivi.huidada.model.vo.TestCountVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Kivi
* @description 针对表【test_paper(试卷表)】的数据库操作Mapper
* @createDate 2024-07-03 14:49:05
* @Entity com.kivi.huidada.model.entity.TestPaper
*/
public interface TestPaperMapper extends BaseMapper<TestPaper> {

    List<TestCountVO> selectCountTop10();
}




