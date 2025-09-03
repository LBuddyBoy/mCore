import './App.css'
import {Route, Routes, useLocation} from "react-router";
import Layout from "./layout/Layout.jsx";
import HomePage from "./page/HomePage.jsx";
import LoginPage from "./features/account/LoginPage.jsx";
import RegisterPage from "./features/account/RegisterPage.jsx";
import SyncPage from './features/sync/SyncPage.jsx';
import AccountPage from './features/account/AccountPage.jsx';
import { useEffect } from 'react';
import { capitalizeFirstLetter } from './util/util.js';
import SingleAccountPage from './features/account/SingleAccountPage.jsx';
import CategoriesPage from './features/forums/CategoriesPage.jsx';
import SingleCategoryPage from './features/forums/SingleCategoryPage.jsx';
import SinglePostPage from './features/forums/SinglePostPage.jsx';
import CreatePostPage from './features/forums/CreatePostPage.jsx';
import AccountRepliesPage from './features/account/AccountRepliesPage.jsx';
import AccountPostsPage from './features/account/AccountPostsPage.jsx';

function App() {
  const location = useLocation();

  useEffect(() => {
    const routes = location.pathname === "/" ? ["Home"] : location.pathname.split("/").splice(1, 2);

    document.title = "MineVale • " + routes.map(capitalizeFirstLetter).join(" ");
  });

  return (
    <Routes>
      <Route element={<Layout/>}>
          <Route index path={"/"} element={<HomePage/>}></Route>
          <Route path={"/login"} element={<LoginPage/>}></Route>
          <Route path={"/register"} element={<RegisterPage/>}></Route>
          <Route path={"/sync"} element={<SyncPage/>}></Route>
          <Route path={"/account"} element={<AccountPage/>}></Route>
          <Route path={"/forums"} element={<CategoriesPage/>}></Route>
          <Route path={"/forums/categories/:id"} element={<SingleCategoryPage/>}></Route>
          <Route path={"/forums/posts/:id"} element={<SinglePostPage/>}></Route>
          <Route path={"/forums/create-post"} element={<CreatePostPage/>}></Route>
          <Route path={"/accounts/:id"} element={<SingleAccountPage/>}></Route>
          <Route path={"/accounts/:id/messages"} element={<AccountRepliesPage/>}></Route>
          <Route path={"/accounts/:id/posts"} element={<AccountPostsPage/>}></Route>
      </Route>
    </Routes>
  )
}

export default App
