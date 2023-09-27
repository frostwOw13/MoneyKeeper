package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.error.ErrorPresentation;
import com.example.moneykeeper.record.BudgetRecord;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.UserRepository;
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
class BudgetRecordControllerTest {

    @Mock
    BudgetRepository budgetRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BudgetController controller;

    private void injectTestUser(User user) {
        UserDetailsImpl userDetails = new UserDetailsImpl(1, null, null, null);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllBudgets_ReturnsValidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);

        List<Budget> budgets = List.of(
                new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, user),
                new Budget("1 65% 50%", LocalDate.now(), "Car", 1000, user)
        );
        doReturn(budgets).when(this.budgetRepository).findBudgetsByUserId(user.getId());

        // when
        ResponseEntity<List<Budget>> response = this.controller.getAllBudgets();

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(budgets, response.getBody());
    }

    @Test
    void getBudgetById_ReturnsValidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);

        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, user);
        budget.setId(1);

        doReturn(Optional.of(budget)).when(this.budgetRepository).findBudgetsByIdAndUserId(budget.getId(), user.getId());

        // when
        ResponseEntity<Budget> response = this.controller.getBudgetById(budget.getId());

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(budget, response.getBody());
    }

    @Test
    void addBudget_PayloadIsValid_ReturnsValidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);
        when((this.userRepository).findUserById(user.getId())).thenReturn(Optional.of(user));

        BudgetRecord request = new BudgetRecord("0 65% 50%", "Groceries", 500);

        // when
        ResponseEntity<?> response = this.controller.addBudget(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        if (response.getBody() instanceof Budget budget) {
            assertEquals(request.name(), budget.getName());
            assertEquals(request.color(), budget.getColor());
            assertEquals(request.amount(), budget.getAmount());

            assertEquals(URI.create("http://localhost:8080/api/budgets/" + budget.getId()), response.getHeaders().getLocation());
            verify(this.budgetRepository).save(budget);
        } else {
            assertInstanceOf(Budget.class, response.getBody());
        }
        verifyNoMoreInteractions(this.budgetRepository);
    }

    @Test
    void addBudget_PayloadIsValid_ReturnsInvalidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);

        String errorMessage = "Session expired";
        BudgetRecord request = new BudgetRecord("0 65% 50%", "Groceries", 500);

        // when
        ResponseEntity<?> response = this.controller.addBudget(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorPresentation(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.budgetRepository);
    }

    @Test
    void addBudget_PayloadIsInvalid_ReturnsValidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);
        when((this.userRepository).findUserById(user.getId())).thenReturn(Optional.of(user));

        String errorMessage = "Name of budget should be present";
        BudgetRecord request = new BudgetRecord("0 65% 50%", "", 500);

        // when
        ResponseEntity<?> response = this.controller.addBudget(request, UriComponentsBuilder.fromUriString("http://localhost:8080"));

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorPresentation(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.budgetRepository);
    }

    @Test
    void deleteBudget_ReturnsValidResponse() {
        // given
        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);
        budget.setId(1);

        String message = "Budget with id = " + budget.getId() + " successfully deleted!";

        // when
        ResponseEntity<?> response = this.controller.deleteBudget(budget.getId());

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), message);

        verify(this.budgetRepository).deleteById(budget.getId());
        verifyNoMoreInteractions(this.budgetRepository);
    }

    @Test
    void deleteBudget_ReturnsInvalidResponse() {
        // given
        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);
        budget.setId(1);

        String errorMessage = "Exception";

        doThrow((new RuntimeException(errorMessage))).when(this.budgetRepository).deleteById(budget.getId());

        // when
        ResponseEntity<?> response = this.controller.deleteBudget(budget.getId());

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorPresentation(List.of(errorMessage)), response.getBody());

        verifyNoMoreInteractions(this.budgetRepository);
    }
}