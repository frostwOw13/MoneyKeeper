// rrd imports
import {Outlet, useLoaderData} from "react-router-dom";

// assets
import wave from "../assets/wave.svg";

// components
import Nav from "../components/Nav";

//  helper functions
import {fetchUserData} from "../helpers"

// loader
export async function mainLoader() {
    let username;

    if (localStorage.getItem("jwt")?.trim().length > 0) {
        username = await fetchUserData();
    }

    return {username};
}

const Main = () => {
    const {username} = useLoaderData()

    return (
        <div className="layout">
            <Nav username={username}/>
            <main>
                <Outlet/>
            </main>
            <img src={wave} alt=""/>
        </div>
    )
}
export default Main