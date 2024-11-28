package com.shiguang.mianshiya.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.mianshiya.annotation.AuthCheck;
import com.shiguang.mianshiya.common.BaseResponse;
import com.shiguang.mianshiya.common.DeleteRequest;
import com.shiguang.mianshiya.common.ErrorCode;
import com.shiguang.mianshiya.common.ResultUtils;
import com.shiguang.mianshiya.constant.UserConstant;
import com.shiguang.mianshiya.exception.BusinessException;
import com.shiguang.mianshiya.exception.ThrowUtils;
import com.shiguang.mianshiya.model.dto.question.QuestionQueryRequest;
import com.shiguang.mianshiya.model.dto.questionbank.QuestionBankAddRequest;
import com.shiguang.mianshiya.model.dto.questionbank.QuestionBankEditRequest;
import com.shiguang.mianshiya.model.dto.questionbank.QuestionBankQueryRequest;
import com.shiguang.mianshiya.model.dto.questionbank.QuestionBankUpdateRequest;
import com.shiguang.mianshiya.model.entity.Question;
import com.shiguang.mianshiya.model.entity.QuestionBank;
import com.shiguang.mianshiya.model.entity.User;
import com.shiguang.mianshiya.model.vo.QuestionBankVO;
import com.shiguang.mianshiya.model.vo.QuestionVO;
import com.shiguang.mianshiya.service.QuestionBankService;
import com.shiguang.mianshiya.service.QuestionService;
import com.shiguang.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题库接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@RestController
@RequestMapping("/questionbank")
@Slf4j
public class QuestionBankController {

    @Resource
    private QuestionBankService questionbankService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionService questionService;

    // region 增删改查

    /**
     * 创建题库（仅管理员可用）
     *
     * @param questionbankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionbankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionbankAddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionbank = new QuestionBank();
        BeanUtils.copyProperties(questionbankAddRequest, questionbank);
        // 数据校验
        questionbankService.validQuestionBank(questionbank, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        questionbank.setUserId(loginUser.getId());
        // 写入数据库
        boolean result = questionbankService.save(questionbank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankId = questionbank.getId();
        return ResultUtils.success(newQuestionBankId);
    }

    /**
     * 删除题库（仅管理员可用）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBank oldQuestionBank = questionbankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionbankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库（仅管理员可用）
     *
     * @param questionbankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionbankUpdateRequest) {
        if (questionbankUpdateRequest == null || questionbankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionbank = new QuestionBank();
        BeanUtils.copyProperties(questionbankUpdateRequest, questionbank);
        // 数据校验
        questionbankService.validQuestionBank(questionbank, false);
        // 判断是否存在
        long id = questionbankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionbankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionbankService.updateById(questionbank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库（封装类）
     *
     * @param questionBankQueryRequest
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVOById(QuestionBankQueryRequest questionBankQueryRequest, HttpServletRequest request) {

        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = questionBankQueryRequest.getId();
        boolean needQueryQuestionList = questionBankQueryRequest.isNeedQueryQuestionList();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 查询数据库
        QuestionBank questionbank = questionbankService.getById(id);
        ThrowUtils.throwIf(questionbank == null, ErrorCode.NOT_FOUND_ERROR);
        // 查询题库封装类
        QuestionBankVO questionBankVO = questionbankService.getQuestionBankVO(questionbank, request);

        // 是否要关联查询题库下的题目列表
        if (needQueryQuestionList) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setQuestionBankId(id);
            //根据需要支持更多的题目搜索参数,如分页
            questionQueryRequest.setPageSize(questionBankQueryRequest.getPageSize());
            questionQueryRequest.setCurrent(questionBankQueryRequest.getCurrent());
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
            questionBankVO.setQuestionPage(questionVOPage);
        }

        // 获取封装类
        return ResultUtils.success(questionBankVO);
    }

    /**
     * 分页获取题库列表（仅管理员可用）
     *
     * @param questionbankQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionbankQueryRequest) {
        long current = questionbankQueryRequest.getCurrent();
        long size = questionbankQueryRequest.getPageSize();
        // 查询数据库
        Page<QuestionBank> questionbankPage = questionbankService.page(new Page<>(current, size),
                questionbankService.getQueryWrapper(questionbankQueryRequest));
        return ResultUtils.success(questionbankPage);
    }

    /**
     * 分页获取题库列表（封装类）
     *
     * @param questionbankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionbankQueryRequest,
                                                                       HttpServletRequest request) {
        long current = questionbankQueryRequest.getCurrent();
        long size = questionbankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBank> questionbankPage = questionbankService.page(new Page<>(current, size),
                questionbankService.getQueryWrapper(questionbankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionbankService.getQuestionBankVOPage(questionbankPage, request));
    }

    /**
     * 分页获取当前登录用户创建的题库列表
     *
     * @param questionbankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionbankQueryRequest,
                                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(questionbankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionbankQueryRequest.setUserId(loginUser.getId());
        long current = questionbankQueryRequest.getCurrent();
        long size = questionbankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionBank> questionbankPage = questionbankService.page(new Page<>(current, size),
                questionbankService.getQueryWrapper(questionbankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionbankService.getQuestionBankVOPage(questionbankPage, request));
    }

    /**
     * 编辑题库（给用户使用）
     *
     * @param questionbankEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionbankEditRequest, HttpServletRequest request) {
        if (questionbankEditRequest == null || questionbankEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        QuestionBank questionbank = new QuestionBank();
        BeanUtils.copyProperties(questionbankEditRequest, questionbank);
        // 数据校验
        questionbankService.validQuestionBank(questionbank, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionbankEditRequest.getId();
        QuestionBank oldQuestionBank = questionbankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionbankService.updateById(questionbank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
