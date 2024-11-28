"use client";

import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import { Card, List } from "antd";
import Link from "next/link";
import TagList from "@/components/TagList";
import "./index.css";

interface Props {
  questionBankId?: number;
  questionList: API.QuestionVO[];
  cardTitle?: String;
}

/**
 * 题目列表组件
 * @param props
 * @constructor
 */
const QuestionList = (props: Props) => {
  const { questionList = [], cardTitle, questionBankId } = props;

  return (
    <Card className="question-list" title={cardTitle}>
      <List
        dataSource={questionList}
        renderItem={(item) => (
          <List.Item extra={<TagList tagList={item.tagList} />}>
            <List.Item.Meta
              title={
                <Link
                  href={
                    questionBankId
                      ? `/bank/${questionBankId}/question/${item.id}`
                      : `/question/${item.id}`
                  }
                >
                  {item.title}
                </Link>
              }
            />
          </List.Item>
        )}
      />
    </Card>
  );
};

export default QuestionList;
