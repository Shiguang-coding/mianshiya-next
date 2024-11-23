package com.shiguang.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiguang.mianshiya.common.ErrorCode;
import com.shiguang.mianshiya.constant.CommonConstant;
import com.shiguang.mianshiya.exception.ThrowUtils;
import com.shiguang.mianshiya.mapper.QuestionBankQuestionMapper;
import com.shiguang.mianshiya.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.shiguang.mianshiya.model.entity.Question;
import com.shiguang.mianshiya.model.entity.QuestionBank;
import com.shiguang.mianshiya.model.entity.QuestionBankQuestion;
import com.shiguang.mianshiya.model.entity.User;
import com.shiguang.mianshiya.model.vo.QuestionBankQuestionVO;
import com.shiguang.mianshiya.model.vo.UserVO;
import com.shiguang.mianshiya.service.QuestionBankQuestionService;
import com.shiguang.mianshiya.service.QuestionBankService;
import com.shiguang.mianshiya.service.QuestionService;
import com.shiguang.mianshiya.service.UserService;
import com.shiguang.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库题目服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private QuestionService questionService;

    @Resource
    private QuestionBankService questionBankService;

    /**
     * 校验数据
     *
     * @param questionbankquestion
     * @param add                  对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionbankquestion, boolean add) {
        ThrowUtils.throwIf(questionbankquestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在
        Long questionId = questionbankquestion.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        Long questionBankId = questionbankquestion.getQuestionBankId();
        if (questionBankId != null) {
            QuestionBank questionBank = questionBankService.getById(questionBankId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionbankquestionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionbankquestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionbankquestionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionbankquestionQueryRequest.getId();
        Long notId = questionbankquestionQueryRequest.getNotId();
        String sortField = questionbankquestionQueryRequest.getSortField();
        String sortOrder = questionbankquestionQueryRequest.getSortOrder();
        Long userId = questionbankquestionQueryRequest.getUserId();
        Long questionId = questionbankquestionQueryRequest.getQuestionId();
        Long questionBankId = questionbankquestionQueryRequest.getQuestionBankId();

        // todo 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "questionBankId", questionBankId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库题目封装
     *
     * @param questionbankquestion
     * @param request
     * @return
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionbankquestion, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankQuestionVO questionbankquestionVO = QuestionBankQuestionVO.objToVo(questionbankquestion);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionbankquestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionbankquestionVO.setUser(userVO);
        // endregion

        return questionbankquestionVO;
    }

    /**
     * 分页获取题库题目封装
     *
     * @param questionbankquestionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> questionbankquestionPage, HttpServletRequest request) {
        List<QuestionBankQuestion> questionbankquestionList = questionbankquestionPage.getRecords();
        Page<QuestionBankQuestionVO> questionbankquestionVOPage = new Page<>(questionbankquestionPage.getCurrent(), questionbankquestionPage.getSize(), questionbankquestionPage.getTotal());
        if (CollUtil.isEmpty(questionbankquestionList)) {
            return questionbankquestionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankQuestionVO> questionbankquestionVOList = questionbankquestionList.stream().map(questionbankquestion -> {
            return QuestionBankQuestionVO.objToVo(questionbankquestion);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionbankquestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionbankquestionVOList.forEach(questionbankquestionVO -> {
            Long userId = questionbankquestionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionbankquestionVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionbankquestionVOPage.setRecords(questionbankquestionVOList);
        return questionbankquestionVOPage;
    }

}
