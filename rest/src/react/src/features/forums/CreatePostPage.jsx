import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createPost, getCategories } from "../../api/api";
import { useState } from "react";
import Button from "../../component/Button";
import { useNavigate } from "react-router";

export default function CreatePostPage() {
  const queryClient = useQueryClient();
  const mutation = useMutation({
    mutationFn: createPost,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["posts"] }),
  });
  const query = useQuery({
    queryKey: ["categories"],
    queryFn: getCategories,
  });
  const [formData, setFormData] = useState({});
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    mutation.mutate(formData, {
      onSuccess: (data) => {
        navigate(`/forums/posts/${data}`);
      },
    });
  };

  if (query.isError || query.isPending) return <></>;

  return (
    <div className="createPostContainer">
      <header>
        <h1>Create a Post</h1>
      </header>
      <form onSubmit={handleSubmit}>
        <input
          name="title"
          type="text"
          placeholder="Title"
          onChange={handleChange}
        />
        <input
          name="content"
          type="text"
          placeholder="Content"
          onChange={handleChange}
        />
        <select name="categoryId" onChange={handleChange}>
          {query.data.map((category) => {
            return (
              <option key={category.id} value={category.id}>
                {category.title}
              </option>
            );
          })}
        </select>
        <Button>Create Post</Button>
      </form>
    </div>
  );
}
