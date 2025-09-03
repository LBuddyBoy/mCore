import { useQuery } from "@tanstack/react-query";
import "./style/PostReplies.css";
import api from "../../../api/axios";
import ReplyItem from "./ReplyItem";
import PostReplyForm from "./PostReplyForm";
import { useState } from "react";
import Button from "../../../component/Button";
import ReplyProvider from "../../../context/RepliesContext";

export default function PostReplies({ post }) {
  const [page, setPage] = useState(0);
  const { data, isPending, isError } = useQuery({
    queryKey: ["postReplies_" + post.id, page],
    queryFn: async () => {
      return (
        await api.get(`/forums/replies?page=${page}&size=10&postId=${post.id}`)
      ).data;
    },
  });

  if (isPending || isError) return <></>;

  const { content: replies, page: pagination } = data;

  console.log(data);

  return (
    <ReplyProvider>
      <div className="postReplies">
        <header className="postRepliesHeader">
          <h2>Replies</h2>
        </header>

        <PostReplyForm post={post} />

        <div className="postRepliesItems">
          {replies.length > 0 ? (
            replies.map((reply) => {
              return <ReplyItem key={reply.id} reply={reply} />;
            })
          ) : (
            <p>There are no replies to view.</p>
          )}
        </div>
        <div className="postRepliesControls">
          <Button
            onClick={() => setPage((prev) => prev - 1)}
            disabled={page == 0}
          >
            Previous Page
          </Button>
          <p>Page {page + 1}</p>
          <Button
            onClick={() => setPage((prev) => prev + 1)}
            disabled={page >= pagination.totalPages - 1}
          >
            Next Page
          </Button>
        </div>
      </div>
    </ReplyProvider>
  );
}
