// rrd import
import {redirect} from "react-router-dom";

// library
import {toast} from "react-toastify";

// helpers
import {deleteItem} from "../helpers";

export async function deleteBudget({params}) {
    try {
        await deleteItem({
            key: "budgets",
            id: params.id,
        });

        toast.success("Budget deleted successfully!");
    } catch (e) {
        throw new Error("There was a problem deleting your budget.");
    }
    return redirect("/");
}
