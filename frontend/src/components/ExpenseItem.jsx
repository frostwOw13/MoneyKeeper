// rrd imports
import { Link, useFetcher } from "react-router-dom";

// library import
import { TrashIcon } from "@heroicons/react/24/solid";

// helper imports
import {
  formatCurrency,
  formatDateToLocaleString
} from "../helpers";

const ExpenseItem = ({expense, showBudget}) => {
  const fetcher = useFetcher();

  return (
      <>
        <td>{expense.name}</td>
        <td>{formatCurrency(expense.amount)}</td>
        <td>{formatDateToLocaleString(expense.date)}</td>
        {showBudget && (
            <td>
              <Link
                  to={`/budget/${expense.budget.id}`}
                  style={{
                    "--accent": expense.budget.color,
                  }}
              >
                {expense.budget.name}
              </Link>
            </td>
        )}
        <td>
          <fetcher.Form method="post">
            <input type="hidden" name="_action" value="deleteExpense"/>
            <input type="hidden" name="expenseId" value={expense.id}/>
            <button
                type="submit"
                className="btn btn--warning"
                aria-label={`Delete ${expense.name} expense`}
            >
              <TrashIcon width={20}/>
            </button>
          </fetcher.Form>
        </td>
      </>
  );
};
export default ExpenseItem;
