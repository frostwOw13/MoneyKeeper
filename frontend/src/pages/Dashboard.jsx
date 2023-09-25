// rrd imports
import {Link, useLoaderData} from "react-router-dom";

// library imports
import {toast} from "react-toastify";

// components
import Intro from "../components/Intro";
import AddBudgetForm from "../components/AddBudgetForm";
import AddExpenseForm from "../components/AddExpenseForm";
import BudgetItem from "../components/BudgetItem";
import Table from "../components/Table";

//  helper functions
import {
    createBudget,
    createExpense,
    deleteItem,
    fetchData,
    fetchUserData,
    signin,
    signup,
    waait,
} from "../helpers";

// loader
export async function dashboardLoader() {
    let username;
    let budgets;
    let expenses;

    if (localStorage.getItem("jwt").trim().length > 0) {
        username = await fetchUserData();
    }

    if (username) {
        budgets = await fetchData("budgets");
        expenses = await fetchData("expenses");
    }

    return {username, budgets, expenses};
}

// action
export async function dashboardAction({request}) {
    await waait();

    const data = await request.formData();
    const {_action, ...values} = Object.fromEntries(data);

    // new user submission
    if (_action === "signin") {
        try {
            const userData = {
                username: values.username,
                password: values.password
            }

            const jwt = await signin(userData)
                .then((response) => {
                    if (response.ok) {
                        return response.text()
                    }
                })

            if (jwt) {
                localStorage.setItem("jwt", jwt);
                return toast.success(`Welcome, ${values.username}`);
            }
            return toast.error("Invalid username or password!")
        } catch (e) {
            throw new Error("There was a problem creating your account.");
        }
    }

    if (_action === "signup") {
        try {
            const userData = {
                username: values.username,
                email: values.email,
                password: values.password
            }

            const jwt = await signup(userData)
                .then((response) => {
                    if (response.ok) {
                        return response.text()
                    }
                })

            if (jwt) {
                localStorage.setItem("jwt", jwt);
                return toast.success(`Welcome, ${values.username}`);
            }
            return toast.error("Invalid username or password!")
        } catch (e) {
            throw new Error("There was a problem creating your account.");
        }
    }

    if (_action === "createBudget") {
        try {
            await createBudget({
                name: values.newBudget,
                amount: values.newBudgetAmount,
            });

            return toast.success("Budget created!");
        } catch (e) {
            throw new Error("There was a problem creating your budget.");
        }
    }

    if (_action === "createExpense") {
        try {
            await createExpense({
                name: values.newExpense,
                amount: values.newExpenseAmount,
                budgetId: values.newExpenseBudget,
            });

            return toast.success(`Expense ${values.newExpense} created!`);
        } catch (e) {
            throw new Error("There was a problem creating your expense.");
        }
    }

    if (_action === "deleteExpense") {
        try {
            await deleteItem({
                key: "expenses",
                id: values.expenseId,
            });
            return toast.success("Expense deleted!");
        } catch (e) {
            throw new Error("There was a problem deleting your expense.");
        }
    }
}

const Dashboard = () => {
    let {username, budgets, expenses} = useLoaderData();

    if (localStorage.getItem("jwt").trim().length === 0) {
        username = null;
    }

    return (
        <>
            {username ? (
                <div className="dashboard">
                    <h1>
                        Welcome back, <span className="accent">{username}</span>
                    </h1>
                    <div className="grid-sm">
                        {budgets && budgets.length > 0 ? (
                            <div className="grid-lg">
                                <div className="flex-lg">
                                    <AddBudgetForm/>
                                    <AddExpenseForm budgets={budgets}/>
                                </div>
                                <h2>Existing Budgets</h2>
                                <div className="budgets">
                                    {budgets.map((budget) => {
                                        return <BudgetItem key={budget.id} budget={budget}/>
                                    })}
                                </div>
                                {expenses && expenses.length > 0 && (
                                    <div className="grid-md">
                                        <h2>Recent Expenses</h2>
                                        <Table
                                            expenses={expenses
                                                .sort((a, b) => b.date - a.date)
                                                .slice(0, 8)}
                                        />
                                        {expenses.length > 8 && (
                                            <Link to="expenses" className="btn btn--dark">
                                                View all expenses
                                            </Link>
                                        )}
                                    </div>
                                )}
                            </div>
                        ) : (
                            <div className="grid-sm">
                                <p>Personal budgeting is the secret to financial freedom.</p>
                                <p>Create a budget to get started!</p>
                                <AddBudgetForm/>
                            </div>
                        )}
                    </div>
                </div>
            ) : (
                <Intro/>
            )}
        </>
    );
};
export default Dashboard;
