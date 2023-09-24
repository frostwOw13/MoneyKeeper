import config from './config/config.json';

export const waait = () =>
    new Promise((res) => setTimeout(res, Math.random() * 800));

// colors
const generateRandomColor = async () => {
    const budgets = await fetchDataDB("budgets");
    const existingBudgetLength = budgets?.length ?? 0;

    return `${existingBudgetLength * 34} 65% 50%`;
};

// Local storage
export const fetchData = (key) => {
    return JSON.parse(localStorage.getItem(key));
};

export const fetchDataDB = async (category) => {
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

// Get all items from local storage
export const getAllMatchingItems = async ({category, key, value}) => {
    const data = await fetchDataDB(category) ?? [];
    return data.filter((item) => item[key] === +value);
};

// delete item from local storage
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

    return await fetch(config.SERVER_URL + "/api/budgets", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${config.JWT_TOKEN}`
        },
        body: JSON.stringify(newItem)
    })
        .then((response) => response.json())
        .catch((error) => console.log(error));
};

// create expense
export const createExpense = ({name, amount, budgetId}) => {
    const newItem = {
        id: crypto.randomUUID(),
        name: name,
        createdAt: Date.now(),
        amount: +amount,
        budgetId: budgetId,
    };
    const existingExpenses = fetchData("expenses") ?? [];
    return localStorage.setItem(
        "expenses",
        JSON.stringify([...existingExpenses, newItem])
    );
};

// total spent by budget
export const calculateSpentByBudget = (budgetId) => {
    const expenses = fetchData("expenses") ?? [];
    const budgetSpent = expenses.reduce((acc, expense) => {
        // check if expense.id === budgetId I passed in
        if (expense.budgetId !== budgetId) return acc;

        // add the current amount to my total
        return (acc += expense.amount);
    }, 0);
    return budgetSpent;
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
