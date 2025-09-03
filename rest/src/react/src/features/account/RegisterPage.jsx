import { useAuth } from "../../context/AuthContext.jsx";
import { useState } from "react";
import { MdOutlineMailOutline } from "react-icons/md";
import { GoKey } from "react-icons/go";
import { IoPersonCircleOutline } from "react-icons/io5";

import "./style/LoginPage.css";

export default function RegisterPage() {
  const { register } = useAuth();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    await register({
      username,
      email,
      password,
    });
  };

  return (
    <div className="loginPageContainer">
      <div className="loginCard">
        <header className="loginHeader">
          <img className="brandLogo" src="/logo.png" alt="Minevale logo" />
          <h1>Register</h1>
        </header>

        <form onSubmit={handleSubmit} className="loginForm">
          <label className="field">
            <span className="fieldLabel">Email</span>
            <div className="inputContainer">
              <IoPersonCircleOutline className="inputIcon" />
              <input
                type="text"
                placeholder="Enter Your Username"
                name="username"
                onChange={(e) => setUsername(e.target.value)}
                autoComplete="username"
                required
              />
            </div>
          </label>

          <label className="field">
            <span className="fieldLabel">Email</span>
            <div className="inputContainer">
              <MdOutlineMailOutline className="inputIcon" />
              <input
                type="email"
                placeholder="Enter Your Email"
                name="email"
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="email"
                required
              />
            </div>
          </label>

          <label className="field">
            <span className="fieldLabel">Password</span>
            <div className="inputContainer">
              <GoKey className="inputIcon" />
              <input
                type="password"
                placeholder="Enter Your Password"
                name="password"
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </div>
          </label>

          <button className="primaryBtn" type="submit">
            Sign In
          </button>
        </form>

        <div className="divider" role="separator">
          OR
        </div>

        {/* If you wire Google later, wrap in a button; this is just styling */}
        <button className="googleBtn" type="button">
          <span className="googleInitial">E</span>
          <span className="googleText">Sign in as Ethan</span>
          <img
            className="googleIcon"
            src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
            alt=""
          />
        </button>

        <a className="registerLink" href="/register">
          Don’t have an account? <strong>Register now!</strong>
        </a>
      </div>
    </div>
  );
}
