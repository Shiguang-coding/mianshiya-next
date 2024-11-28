"use client";

import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import "./index.css";
import { Card } from "antd";
import React from "react";
import Title from "antd/es/typography/Title";
import TagList from "@/components/TagList";
import MdViewer from "@/components/MdViewer";

interface Props {
  question: API.QuestionVO;
}

/**
 * 题目卡片组件
 * @param props
 * @constructor
 */
const QuestionCard = (props: Props) => {
  const { question } = props;

  return (
    <div className="question-card">
      <Card>
        <Title level={1} style={{ fontSize: 24 }}>
          {question.title}
        </Title>
        <TagList tagList={question.tagList} />
        <div style={{ marginBottom: 16 }}></div>
        <MdViewer value={question.content}></MdViewer>
      </Card>
      <div style={{ marginBottom: 16 }}></div>
      <Card title="推荐答案">
        <MdViewer value={question.answer}></MdViewer>
      </Card>
    </div>
  );
};

export default QuestionCard;
