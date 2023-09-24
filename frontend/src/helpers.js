import config from './config/config.json';

export const waait = () =>
    new Promise((res) => setTimeout(res, Math.random() * 800));

// colors
const generateRandomColor = async () => {
    const budgets = await fetchData("budgets");
    const existingBudgetLength = budgets?.length ?? 0;

    return `${existingBudgetLength * 34} 65% 50%`;
};

// TODO: rewrite to use MySQL
export const fetchDataTemp = (key) => {
    return JSON.parse(localStorage.getItem(key));
};

// fetch data from db
export const fetchData = async (category) => {
    return await fetch(config.SERVER_URL + "/api/" + category, {
        method: "GET",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${config.JWT_TOKEN}`
        }
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
}

// post data to db
export const postData = async (category, body) => {
    return await fetch(config.SERVER_URL + "/api/" + category, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${config.JWT_TOKEN}`
        },
        body: JSON.stringify(body)
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
}

// Get all budgets from db by budget id
export const getAllMatchingBudgets = async (value) => {
    const data = await fetchData("budgets") ?? [];
    return data.filter((item) => item["id"] === +value);
};

// Get all expenses from db by budget id
export const getAllMatchingExpenses = async (value) => {
    const data = await fetchData("expenses") ?? [];
    return data.filter((item) => item["budget"].id === +value);
};

// delete item from db
export const deleteItem = async ({key, id}) => {
    return await fetch(config.SERVER_URL + "/api/" + key + "/" + id, {
        method: "DELETE",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${config.JWT_TOKEN}`
        }
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
};

// create budget
export const createBudget = async ({name, amount}) => {
    const newItem = {
        color: await generateRandomColor(),
        name: name,
        amount: +amount,
    };

    return await postData("budgets", newItem)
};

// create expense
export const createExpense = async ({name, amount, budgetId}) => {
    const newItem = {
        amount: +amount,
        name: name,
        budgetId: budgetId,
    };

    return await postData("expenses", newItem)
};

// total spent by budget
export const calculateSpentByBudget = async (budgetId) => {
    const expenses = await fetchData("expenses") ?? [];

    return expenses.reduce((acc, expense) => {
        // check if expense.id === budgetId I passed in

        if (expense.budget.id !== budgetId) return acc;
        // add the current amount to my total
        return (acc += expense.amount);
    }, 0);
};

// FORMATTING
export const formatDateToLocaleString = (epoch) =>
    new Date(epoch).toLocaleDateString();

// Formating percentages
export const formatPercentage = (amt) => {
    return amt.toLocaleString(undefined, {
        style: "percent",
        minimumFractionDigits: 0,
    });
};

// Format currency
export const formatCurrency = (amt) => {
    return amt.toLocaleString(undefined, {
        style: "currency",
        currency: "USD",
    });
};
