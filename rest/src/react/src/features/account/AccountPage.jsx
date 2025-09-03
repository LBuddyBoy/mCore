import { useAuth } from "../../context/AuthContext";
import EditAccountForm from "./component/EditAccountForm";
import "./style/AccountPage.css";
import Button from "../../component/Button";
import SyncDetails from "./component/SyncDetails";

export default function AccountPage() {
  const { account, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
  };

  return (
    <div className="accountPageContainer">
      <header>
        <h1>Account Page</h1>
        <p>Manage your account details and sync status.</p>
      </header>
      <div className="accountDetails">
        {account ? (
          <>
            <h2>Edit Your Account</h2>

            <div className="accountEditForm">
              <EditAccountForm />
            </div>

            <Button className="logoutBtn" onClick={handleLogout} bgColor="#dc3545">Logout</Button>
          </>
        ) : (
          <p>Please log in to view your account details.</p>
        )}
      </div>
      <SyncDetails />
    </div>
  );
}