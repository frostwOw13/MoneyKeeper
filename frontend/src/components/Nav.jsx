// rrd imports
import {Form, NavLink} from "react-router-dom"

// assets
import logomark from "../assets/logomark.svg"
import {UserMinusIcon} from "@heroicons/react/20/solid/index.js";

const Nav = ({username}) => {
    return (
        <nav>
            <NavLink
                to="/"
                aria-label="Go to home"
            >
                <img src={logomark} alt="" height={30}/>
                <span>MoneyKeeper</span>
            </NavLink>
            {
                username && (
                    <Form
                        method="post"
                        action="logout"
                    >
                        <button type="submit" className="btn btn--warning">
                            <span>Logout</span>
                            <UserMinusIcon width={20}/>
                        </button>
                    </Form>
                )
            }
        </nav>
    )
}
export default Nav