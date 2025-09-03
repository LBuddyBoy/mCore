import NavBar from "./NavBar.jsx";
import {Outlet} from "react-router";

export default function Layout() {
    return (
        <>
            <NavBar/>
            <div className="main-content">
                <Outlet/>
            </div>
        </>
    );
}