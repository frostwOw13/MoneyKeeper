// rrd imports
import {redirect} from "react-router-dom";

// library
import {toast} from "react-toastify";

export async function logoutAction() {
    localStorage.setItem("jwt", '')
    toast.success("You’ve successfully logout!")

    return redirect("/")
}