import config from './config/config.json';

export const waait = () =>
    new Promise((res) => setTimeout(res, Math.random() * 800));

const generateRandomColor = async () => {
    const budgets = await fetchData("budgets");
    const existingBudgetLength = budgets?.length ?? 0;

    return `${existingBudgetLength * 34} 65% 50%`;
};

export const signin = async (userData) => {
    return await fetch(config.SERVER_URL + "/auth/signin", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
        .then((response) => response)
        .catch((error) => error);
}

export const signup = async (userData) => {
    return await fetch(config.SERVER_URL + "/auth/signup", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
        .then((response) => response)
        .catch((error) => error);
}

export const fetchUserData = async () => {
    return await fetch(config.SERVER_URL + "/user/", {
        method: "GET",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem("jwt")}`
        }
    })
        .then((response) => response.text())
        .catch((error) => console.log(error));
}

export const fetchData = async (category, id) => {
    return await fetch(config.SERVER_URL + "/api/" + category + (id ? `/${id}` : ''), {
        method: "GET",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem("jwt")}`
        }
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
}

export const postData = async (category, body) => {
    return await fetch(config.SERVER_URL + "/api/" + category, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem("jwt")}`
        },
        body: JSON.stringify(body)
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
}

export const deleteItem = async ({key, id}) => {
    return await fetch(config.SERVER_URL + "/api/" + key + "/" + id, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem("jwt")}`
        }
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
};

export const getBudgetById = async (id) => {
    return await fetchData("budgets", id);
}

export const getExpensesByBudgetId = async (id) => {
    return await fetchData("expenses", id);
}

export const createBudget = async ({name, amount}) => {
    const newItem = {
        color: await generateRandomColor(),
        name: name,
        amount: +amount,
    };

    return await postData("budgets", newItem)
};

export const createExpense = async ({name, amount, budgetId}) => {
    const newItem = {
        amount: +amount,
        name: name,
        budgetId: budgetId,
    };

    return await postData("expenses", newItem)
};

export const calculateSpentByBudget = async (budgetId) => {
    const expenses = await fetchData("expenses") ?? [];

    return expenses.reduce((acc, expense) => {
        if (expense.budget.id !== budgetId) return acc;

        return (acc += expense.amount);
    }, 0);
};

export const formatDateToLocaleString = (epoch) =>
    new Date(epoch).toLocaleDateString();

export const formatPercentage = (amt) => {
    return amt.toLocaleString(undefined, {
        style: "percent",
        minimumFractionDigits: 0,
    });
};

export const formatCurrency = (amt) => {
    return amt.toLocaleString(undefined, {
        style: "currency",
        currency: "USD",
    });
};
