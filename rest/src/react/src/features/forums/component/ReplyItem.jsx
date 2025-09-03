import { Link } from "react-router";
import TimeAgo from "../../../component/TimeAgo";
import Username from "../../../component/Username";
import "./style/ReplyItem.css";
import Button from "../../../component/Button";
import { useAuth } from "../../../context/AuthContext";
import { useReply } from "../../../context/RepliesContext";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateReply } from "../../../api/api";
import { useState } from "react";

export default function ReplyItem({ reply, showView = false, ...props }) {
  const { author, createdAt, updatedAt } = reply;
  const { account } = useAuth();
  const { editing, setEditing } = useReply();
  const queryClient = useQueryClient();
  const mutation = useMutation({
    mutationFn: updateReply,
    onSuccess: () =>
      queryClient.invalidateQueries({
        queryKey: ["postReplies_" + reply.postId],
      }),
  });
  const [message, setMessage] = useState(reply.message);

  const handleSave = (e) => {
    e.preventDefault();

    mutation.mutate({
      message,
      replyId: reply.id,
    });
    setEditing(null);
  };

  return (
    <div className="replyItem" {...props}>
      <div className="replyImage">
        <img src={author.avatarUrl} />
        <div className="editButtons">
          {account.id === author.id && !editing && editing !== reply.id && (
            <button onClick={() => setEditing(reply.id)}>Edit</button>
          )}
          {editing && editing === reply.id && (
            <>
              <button onClick={handleSave}>Save</button>
              <button
                onClick={() => {
                  setEditing(null);
                  setMessage(reply.message);
                }}
              >
                Cancel
              </button>
            </>
          )}
        </div>
      </div>
      <div className="replyDetails">
        <div className="replyAuthor">
          <Username account={author} fitContent={true} />
          {updatedAt > 0 && <p className="replyEditedText">Edited</p>}
          {showView && (
            <Link to={`/forums/posts/${reply.postId}`}>
              <Button>View Post</Button>
            </Link>
          )}
        </div>
        <div className="replyMessage">
          {!editing || editing !== reply.id ? (
            <p>{message}</p>
          ) : (
            <input
              type="text"
              name="message"
              defaultValue={message}
              onChange={(e) => setMessage(e.target.value)}
            ></input>
          )}
          <TimeAgo timeStamp={createdAt} />
        </div>
      </div>
    </div>
  );
}
