package com.shiguang.mianshiya.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.mianshiya.annotation.AuthCheck;
import com.shiguang.mianshiya.common.BaseResponse;
import com.shiguang.mianshiya.common.DeleteRequest;
import com.shiguang.mianshiya.common.ErrorCode;
import com.shiguang.mianshiya.common.ResultUtils;
import com.shiguang.mianshiya.constant.UserConstant;
import com.shiguang.mianshiya.exception.BusinessException;
import com.shiguang.mianshiya.exception.ThrowUtils;
import com.shiguang.mianshiya.model.dto.questionbankquestion.QuestionBankQuestionAddRequest;
import com.shiguang.mianshiya.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.shiguang.mianshiya.model.dto.questionbankquestion.QuestionBankQuestionRemoveRequest;
import com.shiguang.mianshiya.model.dto.questionbankquestion.QuestionBankQuestionUpdateRequest;
import com.shiguang.mianshiya.model.entity.QuestionBankQuestion;
import com.shiguang.mianshiya.model.entity.User;
import com.shiguang.mianshiya.model.vo.QuestionBankQuestionVO;
import com.shiguang.mianshiya.service.QuestionBankQuestionService;
import com.shiguang.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库题目接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/questionbankquestion")
@Slf4j
public class QuestionBankQuestionController {

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建题库题目关联
     *
     * @param questionbankquestionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest questionbankquestionAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionbankquestionAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionbankquestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionbankquestionAddRequest, questionbankquestion);
        // 数据校验
        questionBankQuestionService.validQuestionBankQuestion(questionbankquestion, true);

        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        questionbankquestion.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionBankQuestionService.save(questionbankquestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankQuestionId = questionbankquestion.getId();
        return ResultUtils.success(newQuestionBankQuestionId);
    }

    /**
     * 删除题库题目关联
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBankQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankQuestionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库题目（仅管理员可用）
     *
     * @param questionbankquestionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBankQuestion(@RequestBody QuestionBankQuestionUpdateRequest questionbankquestionUpdateRequest) {
        if (questionbankquestionUpdateRequest == null || questionbankquestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionbankquestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionbankquestionUpdateRequest, questionbankquestion);
        // 数据校验
        questionBankQuestionService.validQuestionBankQuestion(questionbankquestion, false);
        // 判断是否存在
        long id = questionbankquestionUpdateRequest.getId();
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankQuestionService.updateById(questionbankquestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库题目（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionBankQuestion questionbankquestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(questionbankquestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVO(questionbankquestion, request));
    }

    /**
     * 分页获取题库题目列表（仅管理员可用）
     *
     * @param questionbankquestionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(@RequestBody QuestionBankQuestionQueryRequest questionbankquestionQueryRequest) {
        long current = questionbankquestionQueryRequest.getCurrent();
        long size = questionbankquestionQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBankQuestion> questionbankquestionPage = questionBankQuestionService.page(new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionbankquestionQueryRequest));
        return ResultUtils.success(questionbankquestionPage);
    }

    /**
     * 分页获取题库题目列表（封装类）
     *
     * @param questionbankquestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionbankquestionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionbankquestionQueryRequest.getCurrent();
        long size = questionbankquestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBankQuestion> questionbankquestionPage = questionBankQuestionService.page(new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionbankquestionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVOPage(questionbankquestionPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题库题目列表
     *
     * @param questionbankquestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankQuestionVO>> listMyQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionbankquestionQueryRequest,
                                                                 HttpServletRequest request) {
        ThrowUtils.throwIf(questionbankquestionQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionbankquestionQueryRequest.setUserId(loginUser.getId());
        long current = questionbankquestionQueryRequest.getCurrent();
        long size = questionbankquestionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBankQuestion> questionbankquestionPage = questionBankQuestionService.page(new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionbankquestionQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVOPage(questionbankquestionPage, request));
    }

    /**
     * 移除题目题库关联
     * @param questionBankQuestionRemoveRequest
     * @return
     */
    @PostMapping("/remove")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeQuestionBankQuestion(
            @RequestBody QuestionBankQuestionRemoveRequest questionBankQuestionRemoveRequest
    ) {
        // 参数校验
        ThrowUtils.throwIf(questionBankQuestionRemoveRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = questionBankQuestionRemoveRequest.getQuestionBankId();
        Long questionId = questionBankQuestionRemoveRequest.getQuestionId();
        ThrowUtils.throwIf(questionBankId == null || questionId == null, ErrorCode.PARAMS_ERROR);
        // 构造查询
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionId, questionId)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
        boolean result = questionBankQuestionService.remove(lambdaQueryWrapper);
        return ResultUtils.success(result);
    }
    // endregion
}
