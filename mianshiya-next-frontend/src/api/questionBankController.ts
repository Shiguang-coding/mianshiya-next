// @ts-ignore
/* eslint-disable */
import request from "@/libs/request";

/** addQuestionBank POST /api/questionbank/add */
export async function addQuestionBankUsingPost(
  body: API.QuestionBankAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/api/questionbank/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteQuestionBank POST /api/questionbank/delete */
export async function deleteQuestionBankUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/questionbank/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** editQuestionBank POST /api/questionbank/edit */
export async function editQuestionBankUsingPost(
  body: API.QuestionBankEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/questionbank/edit", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getQuestionBankVOById GET /api/questionbank/get/vo */
export async function getQuestionBankVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionBankVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionBankVO_>("/api/questionbank/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listQuestionBankByPage POST /api/questionbank/list/page */
export async function listQuestionBankByPageUsingPost(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBank_>(
    "/api/questionbank/list/page",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** listQuestionBankVOByPage POST /api/questionbank/list/page/vo */
export async function listQuestionBankVoByPageUsingPost(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBankVO_>(
    "/api/questionbank/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** listMyQuestionBankVOByPage POST /api/questionbank/my/list/page/vo */
export async function listMyQuestionBankVoByPageUsingPost(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBankVO_>(
    "/api/questionbank/my/list/page/vo",
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      data: body,
      ...(options || {}),
    }
  );
}

/** updateQuestionBank POST /api/questionbank/update */
export async function updateQuestionBankUsingPost(
  body: API.QuestionBankUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/questionbank/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
