import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import Button from "../../../component/Button";
import api from "../../../api/axios";
import "./style/PostReplyForm.css";

export default function PostReplyForm({ post }) {
  const queryClient = useQueryClient();
  const { mutate, isPending } = useMutation({
    mutationFn: async ({ postId, message }) => {
      const response = await api.post(`/forums/replies`, {
        message,
        postId,
      });

      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["postReplies_" + post.id] });
    },
  });
  const [message, setMessage] = useState(null);

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!message) {
      return;
    }

    mutate({
      message,
      postId: post.id,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="postReplyForm">
      <input
        type="text"
        placeholder="Message"
        onChange={(e) => setMessage(e.target.value)}
      />
      <Button type="submit" disabled={isPending}>
        Reply
      </Button>
    </form>
  );
}
