import { createContext, useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import api from "../api/axios";

const AuthContext = createContext(null);

export default function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token") || null);
  const [account, setAccount] = useState(null);

  const navigate = useNavigate();

  console.log("Account:", account);

  useEffect(() => {
    if (!token) return;

    const auth = async () => {
      try {
        const response = await api.post("/auth/me", {
          jwt: token,
        });

        setToken(token);
        setAccount(response.data);
      } catch (error) {
        setToken(null);
        setAccount(null);
        localStorage.removeItem("token");
        console.error("Failed to authenticate user:", error);
        navigate("/login");
      }
    };

    auth();
  }, [token]);

  const login = async (credentials) => {
    const response = await api.post(`/auth/login`, credentials);

    if (response.status === 200) {
      localStorage.setItem("token", response.data);
      setToken(response.data);
      navigate("/");
      return;
    }

    return response.data;
  };

  const register = async (credentials) => {
    const response = await api.post(`/auth/register`, credentials);

    if (response.status === 200) {
      navigate("/login");
      return;
    }

    throw new Error(response.data.message);
  };

  const logout = async () => {
    localStorage.removeItem("token");
    setAccount(null);
    setToken(null);
  };

  const exports = {
    account,
    token,
    setAccount,
    logout,
    register,
    login,
  };

  return (
    <AuthContext.Provider value={exports}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within a AuthProvider");
  }

  return context;
}
