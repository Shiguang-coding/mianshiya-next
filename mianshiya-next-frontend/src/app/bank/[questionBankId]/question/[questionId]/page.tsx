"use server";

import {Flex, Menu, message} from "antd";
import {getQuestionBankVoByIdUsingGet} from "@/api/questionBankController";
import Title from "antd/es/typography/Title";
import {getQuestionVoByIdUsingGet} from "@/api/questionController";
import Sider from "antd/es/layout/Sider";
import {Content} from "antd/es/layout/layout";
import QuestionCard from "@/components/QuestionCard";
import "./index.css";
import Link from "next/link";

/**
 * 题库题目详情页
 * @constructor
 */
export default async function BankQuestionPage({params}) {
    const {questionBankId, questionId} = params;

    // 获取题库详情
    let bank = undefined;

    try {
        const res = await getQuestionBankVoByIdUsingGet({
            id: questionBankId,
            needQueryQuestionList: true,
            pageSize: 200,
        });
        bank = res.data;
    } catch (error: any) {
        message.error("获取题库详情数据失败," + error.message);
    }

    if (!bank) {
        return <div>获取题库详情失败</div>;
    }

    // 获取题目详情
    let question = undefined;

    try {
        const res = await getQuestionVoByIdUsingGet({
            id: questionId,
        });
        question = res.data;
    } catch (error: any) {
        message.error("获取题目详情数据失败," + error.message);
    }

    if (!question) {
        return <div>获取题目详情失败</div>;
    }

    // 题目菜单列表
    const questionMenuItemList = (bank.questionPage?.records || []).map((q) => {
        return {
            label: (
                <Link href={`/bank/${questionBankId}/question/${q.id}`}>{q.title}</Link>
            ),
            key: q.id,
        };
    });

    return (
        <div id="bankQuestionPage">
            <Flex gap={24}>
                <Sider width={240} theme="light" style={{padding: "24px 0", minHeight: "100vh"}}>
                    <Title level={4} style={{padding: "0 20px"}}>
                        {bank.title}
                    </Title>
                    <Menu items={questionMenuItemList} selectedKeys={[question.id]}/>
                </Sider>
                <Content>
                    <QuestionCard question={question}/>
                </Content>
            </Flex>
        </div>
    );
}
