import { useQuery } from "@tanstack/react-query";
import api from "../../../api/axios";
import "./style/CategoryItem.css";
import { Link } from "react-router";

export default function CategoryItem({ category }) {
  const { data, isPending, isError } = useQuery({
    queryFn: async () => {
      const response = await api.get(`/forums/categories/${category.id}/stats`);

      return response.data;
    },
    queryKey: ["categoryStats_" + category.id],
  });

  if (isPending || isError) return <></>;

  return (
    <Link to={"/forums/categories/" + category.id} className="categoryItem">
      <h3>{category.title}</h3>

      <div className="categoryStats">
        <div className="categoryStat">
          <p>Replies</p>
          <span>{data.replies}</span>
        </div>
        <div className="categoryStat">
          <p>Posts</p>
          <span>{data.posts}</span>
        </div>
      </div>
    </Link>
  );
}
