import React from "react";
import "./index.css"

/**
 * 全局底部栏组件
 * @constructor
 */
export default function GlobalFooter() {
  const currentYear = new Date().getFullYear();
  return (
    <div className="global-footer">
      <div>© {currentYear} 面试刷题平台</div>
      <div>
        <a
          href="https://github.com/shiguang-coding/mianshiya-next"
          target="_blank"
        >
          作者: 時光
        </a>
      </div>
    </div>
  );
}
