import { useState } from "react";
import "./style/Username.css";
import { Link } from "react-router";

export default function Username({ account = null, fitContent = false }) {
  const [viewing, setViewing] = useState(false);

  if (!account.user) {
    return <UnsyncedUsername account={account} />;
  }

  const { user } = account;

  return (
    <Link
      className={`usernameContainer ${fitContent ? "fitContent" : ""}`}
      onMouseEnter={() => setViewing(true)}
      onMouseLeave={() => setViewing(false)}
      to={`/accounts/${account.id}`}
    >
      <h4
        className="usernameDisplay"
        style={{
          color: user.rank.primaryColor,
          border: `2px solid ${user.rank.secondaryColor}`,
        }}
      >
        {user.rank.displayName}
      </h4>
      <p
        style={{
          fontSize: "1.2em",
          color: user.rank.primaryColor,
          fontWeight: "400",
        }}
      >
        {user.name}
      </p>
      {viewing && <></>}
    </Link>
  );
}

function UnsyncedUsername({ account }) {
  return (
    <Link to={"/accounts/" + account.id} className={`usernameContainer`}>
      <h4
        className="usernameDisplay"
        style={{
          color: "#a99e9e",
          border: `2px solid #989696`,
        }}
      >
        DEFAULT
      </h4>
      <p
        style={{
          fontSize: "1.2em",
          color: "#a99e9e",
          fontWeight: "400",
        }}
      >
        {account.username}
      </p>
    </Link>
  );
}
