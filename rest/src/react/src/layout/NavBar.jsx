import { Link, NavLink } from "react-router";
import "./NavBar.css";
import { useAuth } from "../context/AuthContext.jsx";

export default function NavBar() {
  const { token } = useAuth();

  return (
    <div className={"navbar"}>
      <header>
        <Link to="/">
          <img src="/logo.png" />
          <h3>MineVale</h3>
        </Link>
      </header>
      <nav className={"navbarLinks"}>
        {token ? (
          <>
            <NavLink to={"/forums"}>Forums</NavLink>
            <AccountLink />
          </>
        ) : (
          <>
            <NavLink to={"/login"}>Login</NavLink>
            <NavLink to={"/register"}>Register</NavLink>
          </>
        )}
      </nav>
    </div>
  );
}

function AccountLink() {
  const { account } = useAuth();

  if (!account) return <></>;

  return (
    <>
      {!account.minecraftUUID && <NavLink to={"/sync"}>Sync</NavLink>}{" "}
      <NavLink to={"/account"}>
        {account.avatarUrl ? (
          <>
            <span className="accountLinkText">Account</span>
            <img src={account.avatarUrl} />
          </>
        ) : (
          "Account"
        )}
      </NavLink>
    </>
  );
}
