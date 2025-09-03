import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../../context/AuthContext";
import api from "../../../api/axios";
import Button from "../../../component/Button";
import Username from "../../../component/Username";
import { Link } from "react-router";

export default function SyncDetails() {
  const { account } = useAuth();
  const { data } = useQuery({
    queryKey: ["syncDetails"],
    queryFn: async () => {
      const response = await api.get(`/users/${account?.minecraftUUID}`);
      return response.data;
    },
    enabled: !!account,
  });

  if (!account || !data) return <p>No sync details available.</p>;

  const { user } = data;

  return (
    <div className="syncDetails">
      <h2>Sync Details</h2>
      {user ? (
        <div>
          <img
            src={`https://mc-heads.net/avatar/${user.name}/150`}
            alt="Minecraft Avatar"
            className="syncDetailsAvatar"
          />
          <Username account={account} />
          <div className="syncDetailsButtons">
            <Link to={"/accounts/" + account.id}>
              <Button>View Profile</Button>
            </Link>
            <Button bgColor="#dc3545">Unsync</Button>
          </div>
        </div>
      ) : (
        <p>No sync details available.</p>
      )}
    </div>
  );
}
