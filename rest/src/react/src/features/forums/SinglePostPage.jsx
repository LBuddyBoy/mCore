import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import api from "../../api/axios";
import "./style/SinglePostPage.css";
import PostReplies from "./component/PostReplies";
import Username from "../../component/Username";

export default function SinglePostPage() {
  const { id } = useParams();
  const {
    data: post,
    isPending,
    isError,
  } = useQuery({
    queryKey: ["post_" + id],
    queryFn: async () => {
      const response = await api.get(`/forums/posts/${id}`);

      return response.data;
    },
  });

  if (isError || isPending) return <></>;

  const { title, content, createdBy } = post;
  const createdAt = new Date(post.createdAt);

  console.log("Post: ", post);

  return (
    <div className="singlePostContainer">
      <div className="singePostInfo">
        <div className="singlePostAuthor">
          <img src={createdBy.avatarUrl} />
          <Username account={createdBy} />
        </div>
        <div className="singlePostStats">
          <span>{createdAt.toLocaleDateString()}</span>
          <span>•</span>
          <span>Replies: 0</span>
        </div>
      </div>
      <header className="singlePostHeader">
        <h1>{title}</h1>
        <p>{content}</p>
      </header>
      <PostReplies post={post} />
    </div>
  );
}
