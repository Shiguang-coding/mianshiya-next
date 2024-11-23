package com.shiguang.mianshiya.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiguang.mianshiya.model.dto.questionbank.QuestionBankQueryRequest;
import com.shiguang.mianshiya.model.entity.QuestionBank;
import com.shiguang.mianshiya.model.vo.QuestionBankVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题库服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 校验数据
     *
     * @param questionbank
     * @param add 对创建的数据进行校验
     */
    void validQuestionBank(QuestionBank questionbank, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionbankQueryRequest
     * @return
     */
    QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionbankQueryRequest);
    
    /**
     * 获取题库封装
     *
     * @param questionbank
     * @param request
     * @return
     */
    QuestionBankVO getQuestionBankVO(QuestionBank questionbank, HttpServletRequest request);

    /**
     * 分页获取题库封装
     *
     * @param questionbankPage
     * @param request
     * @return
     */
    Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionbankPage, HttpServletRequest request);
}
