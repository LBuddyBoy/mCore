import api from "./axios";

export async function updateReply({ replyId, message }) {
  const response = await api.put(`/forums/replies/${replyId}`, {
    message,
  });

  return response.data;
}

export async function createPost({ title, content, categoryId }) {
  const response = await api.post("/forums/posts", {
    title,
    content,
    categoryId,
  });

  return response.data;
}

export async function getCategories() {
  const response = await api.get("/forums/categories");

  return response.data;
}

export async function getAccountStats(accountId) {
  const response = await api.get(`/accounts/${accountId}/stats`);

  return response.data;
}

export async function getAccountReplies({ accountId, page }) {
  const response = await api.get(
    `/forums/replies?page=${page}&size=10&accountId=${accountId}`
  );

  return response.data;
}

export async function getAccountPosts({ accountId, page }) {
  const response = await api.get(
    `/forums/posts?page=${page}&size=10&accountId=${accountId}`
  );

  return response.data;
}
