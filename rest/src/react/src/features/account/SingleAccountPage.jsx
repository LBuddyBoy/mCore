import { useQuery } from "@tanstack/react-query";
import { Link, useParams } from "react-router";
import api from "../../api/axios";
import "./style/SingleAccountPage.css";
import Username from "../../component/Username";
import { getAccountStats } from "../../api/api";
import useTitle from "../../hooks/useTitle";

export default function SingleAccountPage() {
  const { id } = useParams();
  const {
    data: account,
    isPending,
    isError,
  } = useQuery({
    queryKey: ["account_" + id],
    queryFn: async () => {
      const response = await api.get(`/accounts/${id}`);

      return response.data;
    },
  });

  useTitle(account?.username);

  if (isPending || isError) return <></>;

  const user = account.user;
  const firstJoinAt = new Date(user ? user.firstJoinAt : account.createdAt);

  console.log(user);

  return (
    <div className="userPageContainer">
      <div className="userPageBanner">
        <img
          src={account.bannerUrl || "/default-banner.png"}
          alt="Banner"
          className="bannerImage"
        />
      </div>

      <div className="userPageHeader">
        <img
          src={account.avatarUrl}
          alt="Minecraft Avatar"
          className="userPageAvatar"
        />
        <div className="userPageContent">
          <Username account={account} />
          <p>Joined: {firstJoinAt.toLocaleDateString()}</p>
          <div className="userPageStatus">
            <h4>Status:</h4>
            {user && user.online ? (
              <p className="userStatus online">Online ({user.currentServer})</p>
            ) : (
              <p className="userStatus offline">Offline</p>
            )}
          </div>
        </div>
      </div>
      <AccountStats account={account} />
    </div>
  );
}

function AccountStats({ account }) {
  const query = useQuery({
    queryKey: ["accountStats_" + account.id],
    queryFn: () => getAccountStats(account.id),
  });

  if (query.isPending || query.isError) return <></>;

  return (
    <div className="userPageStats">
      <div className="userPageStat">
        <p>Messages</p>
        <Link to={`/accounts/${account.id}/messages`}>
          {query.data.messages}
        </Link>
      </div>
      <div className="userPageStat">
        <p>Posts</p>
        <Link to={`/accounts/${account.id}/posts`}>{query.data.posts}</Link>
      </div>
    </div>
  );
}
