import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router";
import api from "../../api/axios";
import "./style/SingleCategoryPage.css";
import PostItem from "./component/PostItem";

export default function SingleCategoryPage() {
  const { id } = useParams();
  const {
    data: category,
    isPending,
    isError,
  } = useQuery({
    queryKey: ["category_" + id],
    queryFn: async () => {
      const response = await api.get("/forums/categories/" + id);

      return response.data;
    },
  });

  if (isPending || isError) return <></>;

  return (
    <div className="singleCategoryContainer">
      <header className="singleCategoryHeader">
        <h1>{category.title}</h1>
        <p>{category.description}</p>
      </header>

      <div className="categoryPostsContainer">
        <header className="categoryPostsHeader"></header>
        <CategoryPosts category={category} />
      </div>
    </div>
  );
}

function CategoryPosts({ category }) {
  const { data, isPending, isError } = useQuery({
    queryKey: ["posts_" + category.id],
    queryFn: async () => {
      const response = await api.get(
        `/forums/posts?page=0&size=10&categoryId=${category.id}`
      );

      return response.data;
    },
  });

  if (isError || isPending) return <></>;

  return (
    <div className="categoryPosts">
      {data.content.map((post) => {
        return <PostItem key={post.id} post={post} />;
      })}
    </div>
  );
}
