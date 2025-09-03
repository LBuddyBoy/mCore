import { useQuery } from "@tanstack/react-query";
import api from "../../api/axios";
import CategoryItem from "./component/CategoryItem";
import "./style/CategoriesPage.css";
import { Link } from "react-router";
import Button from "../../component/Button";

export default function CategoriesPage() {
  const {
    data: categories,
    isPending,
    isError,
  } = useQuery({
    queryKey: ["postCategories"],
    queryFn: async () => {
      const response = await api.get("/forums/categories");

      return response.data;
    },
  });

  if (isPending || isError) return <></>;

  return (
    <div className="categoriesPageContainer">
      <header>
        <h1>Forums</h1>
        <Link to={"/forums/create-post"}>
          <Button>Create Post</Button>
        </Link>
      </header>
      <div className="categoryItems">
        {categories.map((category) => {
          return <CategoryItem key={category.id} category={category} />;
        })}
      </div>
    </div>
  );
}
