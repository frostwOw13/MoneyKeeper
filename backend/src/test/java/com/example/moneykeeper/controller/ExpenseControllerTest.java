package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.Expense;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.record.ErrorRecord;
import com.example.moneykeeper.record.ExpenseRecord;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    BudgetRepository budgetRepository;

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    ExpenseController controller;

    private void injectTestUser() {
        UserDetailsImpl userDetails = new UserDetailsImpl(1, null, null, null);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllExpenses_ReturnsValidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser();

        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, user);
        doReturn(List.of(budget)).when(this.budgetRepository).findBudgetsByUserId(user.getId());

        List<Expense> expenses = List.of(
                new Expense("Expense 1", 100, LocalDate.now(), budget),
                new Expense("Expense 2", 200, LocalDate.now(), budget)
        );
        doReturn(expenses).when(this.expenseRepository).findExpensesByBudgetId(budget.getId());

        // when
        ResponseEntity<List<Expense>> response = this.controller.getAllExpenses();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(expenses, response.getBody());
    }

    @Test
    void getAllExpensesByBudget_ReturnsValidResponse() {
        // given
        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);

        List<Expense> expenses = List.of(
                new Expense("Expense 1", 100, LocalDate.now(), budget),
                new Expense("Expense 2", 200, LocalDate.now(), budget)
        );
        doReturn(expenses).when(this.expenseRepository).findExpensesByBudgetId(budget.getId());

        // when
        ResponseEntity<List<Expense>> response = this.controller.getAllExpensesByBudget(budget.getId());

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(expenses, response.getBody());
    }

    @Test
    void addExpense_PayloadBudgetIdIsInvalid_ReturnsValidResponse() {
        // given
        String errorMessage = "Cannot find budget by this id";
        ExpenseRecord request = new ExpenseRecord("Expense", 100, 0);

        // when
        ResponseEntity<?> response = this.controller.addExpense(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.expenseRepository);
    }

    @Test
    void addExpense_PayloadNameIsInvalid_ReturnsValidResponse() {
        // given
        String errorMessage = "Name of expense should be present";

        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);
        when((this.budgetRepository).findById(budget.getId())).thenReturn(Optional.of(budget));

        ExpenseRecord request = new ExpenseRecord("", 100, budget.getId());

        // when
        ResponseEntity<?> response = this.controller.addExpense(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.expenseRepository);
    }

    @Test
    void addExpense_PayloadAmountIsInvalid_ReturnsValidResponse() {
        // given
        String errorMessage = "Amount of expense should be present";

        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);
        when((this.budgetRepository).findById(budget.getId())).thenReturn(Optional.of(budget));

        ExpenseRecord request = new ExpenseRecord("Expense", 0, budget.getId());

        // when
        ResponseEntity<?> response = this.controller.addExpense(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.expenseRepository);
    }

    @Test
    void addExpense_PayloadIsValid_ReturnsValidResponse() {
        // given
        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);
        when((this.budgetRepository).findById(budget.getId())).thenReturn(Optional.of(budget));

        ExpenseRecord request = new ExpenseRecord("Expense", 100, budget.getId());

        // when
        ResponseEntity<?> response = this.controller.addExpense(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        if (response.getBody() instanceof Expense expense) {
            assertEquals(request.name(), expense.getName());
            assertEquals(request.amount(), expense.getAmount());
            assertEquals(request.budgetId(), expense.getBudget().getId());

            assertEquals(URI.create("http://localhost:8080/api/expenses/" + expense.getId()), response.getHeaders().getLocation());
            verify(this.expenseRepository).save(expense);
        } else {
            assertInstanceOf(Expense.class, response.getBody());
        }
        verifyNoMoreInteractions(this.expenseRepository);
    }

    @Test
    void deleteExpense_ReturnsValidResponse() {
        // given
        Expense expense = new Expense("Expense", 100, LocalDate.now(), null);

        String message = "Expense with id = " + expense.getId() + " successfully deleted!";

        // when
        ResponseEntity<?> response = this.controller.deleteExpense(expense.getId());

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), message);

        verify(this.expenseRepository).deleteById(expense.getId());
        verifyNoMoreInteractions(this.expenseRepository);
    }

    @Test
    void deleteExpense_ReturnsInvalidResponse() {
        // given
        Expense expense = new Expense("Expense", 100, LocalDate.now(), null);

        String errorMessage = "Exception";

        doThrow((new RuntimeException(errorMessage))).when(this.expenseRepository).deleteById(expense.getId());

        // when
        ResponseEntity<?> response = this.controller.deleteExpense(expense.getId());

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.expenseRepository);
    }
}