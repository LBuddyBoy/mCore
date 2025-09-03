import { useState } from "react";
import api from "../../api/axios";
import { useAuth } from "../../context/AuthContext";
import Button from "../../component/Button";

export default function SyncPage() {
  const [code, setCode] = useState(null);
  const { account, setAccount } = useAuth();

  const handleSubmit = async (event) => {
    event.preventDefault();

    const response = await api.post("/sync", {
      code,
    });

    if (response.status === 200) {
      console.log("Sync code found. Proceeding with sync...");
      console.log(response.data);
      setAccount(response.data.account);
      return;
    }

    console.log("Sync code not found. Error:", response.data);
  };

  console.log(code);

  return (
    <div className="syncPageContainer">
      <form onSubmit={handleSubmit}>
        <input
          name="code"
          type="number"
          placeholder="Code"
          onChange={(e) => setCode(e.target.value)}
          required
        />
        <button type="submit">Submit</button>
      </form>
      {account && account.minecraftUUID && (
        <div className="syncPageAccount">
          <h2>Account Synced</h2>
          <p>{account.minecraftUsername}</p>
          <img
            src={`https://mc-heads.net/avatar/${account.minecraftUUID}/100`}
          ></img>
        </div>
      )}
    </div>
  );
}
