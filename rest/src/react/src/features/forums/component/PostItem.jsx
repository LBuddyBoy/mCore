import { useQuery } from "@tanstack/react-query";
import "./style/PostItem.css";
import api from "../../../api/axios";
import { Link } from "react-router";
import TimeAgo from "../../../component/TimeAgo";

export default function PostItem({ post }) {
  const { data, isPending, isError } = useQuery({
    queryKey: ["postStats_" + post.id],
    queryFn: async () => {
      const response = await api.get(`/forums/posts/${post.id}/stats`);

      return response.data;
    },
  });

  if (isPending || isError) return <></>;

  console.log(post);

  return (
    <Link to={"/forums/posts/" + post.id} className="postItem">
      <header className="postItemHeader">
        <h3>{post.title}</h3>
        <div className="postItemDetails">
          <p>{post.createdBy.username}</p>
          <span>•</span>
          <TimeAgo timeStamp={post.createdAt} />
        </div>
      </header>

      <div className="postItemStats">
        <div className="postItemStat">
          <p>Replies</p>
          <span>{data.replies}</span>
        </div>
        <div className="postItemStat">
          <p>Views</p>
          <span>{data.views}</span>
        </div>
      </div>
    </Link>
  );
}
