package com.shiguang.mianshiya.model.vo;

import com.shiguang.mianshiya.model.entity.QuestionBankQuestion;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题库题目视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionBankQuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param questionbankquestionVO
     * @return
     */
    public static QuestionBankQuestion voToObj(QuestionBankQuestionVO questionbankquestionVO) {
        if (questionbankquestionVO == null) {
            return null;
        }
        QuestionBankQuestion questionbankquestion = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionbankquestionVO, questionbankquestion);
        return questionbankquestion;
    }

    /**
     * 对象转封装类
     *
     * @param questionbankquestion
     * @return
     */
    public static QuestionBankQuestionVO objToVo(QuestionBankQuestion questionbankquestion) {
        if (questionbankquestion == null) {
            return null;
        }
        QuestionBankQuestionVO questionbankquestionVO = new QuestionBankQuestionVO();
        BeanUtils.copyProperties(questionbankquestion, questionbankquestionVO);
        return questionbankquestionVO;
    }
}
