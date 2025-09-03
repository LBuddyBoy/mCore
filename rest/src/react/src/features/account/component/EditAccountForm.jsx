import { useState } from "react";
import { useAuth } from "../../../context/AuthContext";
import { MdOutlineMailOutline } from "react-icons/md";
import { IoPersonCircleOutline } from "react-icons/io5";
import "./style/EditAccountForm.css";
import Button from "../../../component/Button";

export default function EditAccountForm() {
  const { account } = useAuth();
  const [formData, setFormData] = useState({
    username: account?.username || "",
    email: account?.email || "",
    avatarUrl: account?.avatarUrl || "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Here you would typically send the updated data to your API
    console.log("Updated account data:", formData);
  };
  return (
    <form onSubmit={handleSubmit} className="editAccountForm">
      <label className="editAccountField">
        <span className="editFieldLabel">Username</span>
        <div className="editInputContainer">
          <IoPersonCircleOutline className="editInputIcon" />
          <input
            className="inputField"
            type="text"
            name="username"
            defaultValue={formData.username}
            onChange={handleChange}
          />
        </div>
      </label>
      <label className="editAccountField">
        <span className="editFieldLabel">Email</span>
        <div className="editInputContainer">
          <MdOutlineMailOutline className="editInputIcon" />
          <input
            type="email"
            name="email"
            defaultValue={formData.email}
            onChange={handleChange}
          />
        </div>
      </label>
      <label className="editAccountField">
        <span className="editFieldLabel">Avatar</span>
        <div className="editInputContainer">
          <input type="file" name="avatarUrl" onChange={handleChange} />
        </div>
      </label>
      <Button type="submit">Save Changes</Button>
    </form>
  );
}
