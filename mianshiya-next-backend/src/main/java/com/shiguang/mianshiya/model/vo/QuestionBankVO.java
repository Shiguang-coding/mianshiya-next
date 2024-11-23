package com.shiguang.mianshiya.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shiguang.mianshiya.model.entity.Question;
import com.shiguang.mianshiya.model.entity.QuestionBank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题库视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionBankVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

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
     * 题库下的题目列表（分页）
     */
   private Page<Question> questionPage;

    /**
     * 封装类转对象
     *
     * @param questionbankVO
     * @return
     */
    public static QuestionBank voToObj(QuestionBankVO questionbankVO) {
        if (questionbankVO == null) {
            return null;
        }
        QuestionBank questionbank = new QuestionBank();
        BeanUtils.copyProperties(questionbankVO, questionbank);
        return questionbank;
    }

    /**
     * 对象转封装类
     *
     * @param questionbank
     * @return
     */
    public static QuestionBankVO objToVo(QuestionBank questionbank) {
        if (questionbank == null) {
            return null;
        }
        QuestionBankVO questionbankVO = new QuestionBankVO();
        BeanUtils.copyProperties(questionbank, questionbankVO);
        return questionbankVO;
    }
}
