import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import { getAccountPosts } from "../../api/api";
import { useState } from "react";
import "./style/AccountPostsPage.css";
import Button from "../../component/Button";
import PostItem from "../forums/component/PostItem";
import useTitle from "../../hooks/useTitle";

export default function AccountPostsPage() {
  const { id } = useParams();
  const [page, setPage] = useState(0);
  const query = useQuery({
    queryKey: ["accountPosts_" + id, page],
    queryFn: () => getAccountPosts({ accountId: id, page }),
  });

  useTitle(query.data?.page.totalElements + " Posts");

  if (query.isPending || query.isError) return <></>;

  const posts = query.data.content;
  const pagination = query.data.page;

  return (
    <div className="accountPostsContainer">
      <header>
        <h1>Posts</h1>
      </header>
      <div className="accountPostItems">
        {posts.map((post) => {
          return <PostItem key={post.id} post={post} />;
        })}
      </div>
      <div className="accountPostsControls">
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
  );
}
