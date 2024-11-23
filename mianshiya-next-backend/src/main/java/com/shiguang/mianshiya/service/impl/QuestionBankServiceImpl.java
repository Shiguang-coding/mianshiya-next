package com.shiguang.mianshiya.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiguang.mianshiya.common.ErrorCode;
import com.shiguang.mianshiya.constant.CommonConstant;
import com.shiguang.mianshiya.exception.ThrowUtils;
import com.shiguang.mianshiya.mapper.QuestionBankMapper;
import com.shiguang.mianshiya.model.dto.questionbank.QuestionBankQueryRequest;
import com.shiguang.mianshiya.model.entity.QuestionBank;
import com.shiguang.mianshiya.model.entity.User;
import com.shiguang.mianshiya.model.vo.QuestionBankVO;
import com.shiguang.mianshiya.model.vo.UserVO;
import com.shiguang.mianshiya.service.QuestionBankService;
import com.shiguang.mianshiya.service.UserService;
import com.shiguang.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 题库服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param questionbank
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestionBank(QuestionBank questionbank, boolean add) {
        ThrowUtils.throwIf(questionbank == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = questionbank.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionbankQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionbankQueryRequest) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        if (questionbankQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionbankQueryRequest.getId();
        Long notId = questionbankQueryRequest.getNotId();
        String title = questionbankQueryRequest.getTitle();
        String searchText = questionbankQueryRequest.getSearchText();
        String sortField = questionbankQueryRequest.getSortField();
        String sortOrder = questionbankQueryRequest.getSortOrder();
        Long userId = questionbankQueryRequest.getUserId();
        String description = questionbankQueryRequest.getDescription();
        String picture = questionbankQueryRequest.getPicture();

        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(picture), "picture", picture);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题库封装
     *
     * @param questionbank
     * @param request
     * @return
     */
    @Override
    public QuestionBankVO getQuestionBankVO(QuestionBank questionbank, HttpServletRequest request) {
        // 对象转封装类
        QuestionBankVO questionbankVO = QuestionBankVO.objToVo(questionbank);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = questionbank.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionbankVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态

        // endregion

        return questionbankVO;
    }

    /**
     * 分页获取题库封装
     *
     * @param questionbankPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionbankPage, HttpServletRequest request) {
        List<QuestionBank> questionbankList = questionbankPage.getRecords();
        Page<QuestionBankVO> questionbankVOPage = new Page<>(questionbankPage.getCurrent(), questionbankPage.getSize(), questionbankPage.getTotal());
        if (CollUtil.isEmpty(questionbankList)) {
            return questionbankVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionBankVO> questionbankVOList = questionbankList.stream().map(questionbank -> {
            return QuestionBankVO.objToVo(questionbank);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionbankList.stream().map(QuestionBank::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        // 填充信息
        questionbankVOList.forEach(questionbankVO -> {
            Long userId = questionbankVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionbankVO.setUser(userService.getUserVO(user));
        });
        // endregion

        questionbankVOPage.setRecords(questionbankVOList);
        return questionbankVOPage;
    }

}
