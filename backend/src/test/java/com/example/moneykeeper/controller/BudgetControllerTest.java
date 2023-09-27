package com.example.moneykeeper.controller;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.dto.BudgetRequest;
import com.example.moneykeeper.entity.Budget;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.repository.BudgetRepository;
import com.example.moneykeeper.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    BudgetRepository budgetRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    HttpServletResponse httpServletResponse;

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
        List<Budget> response = this.controller.getAllBudgets();

        // then
        assertNotNull(response);
        assertEquals(budgets, response);
    }

    @Test
    void getBudgetById_ReturnsValidResponse() {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);

        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, user);
        budget.setId(1);

        doReturn(budget).when(this.budgetRepository).findBudgetsByIdAndUserId(budget.getId(), user.getId());

        // when
        Budget response = this.controller.getBudgetById(budget.getId());

        // then
        assertNotNull(response);
        assertEquals(budget, response);
    }

    @Test
    void addBudget_ReturnsValidResponse() throws IOException {
        // given
        User user = new User();
        user.setId(1);

        injectTestUser(user);

        BudgetRequest request = new BudgetRequest("0 65% 50%", "Groceries", 500);

        // when
        Budget budget = this.controller.addBudget(request, this.httpServletResponse);

        // then
        assertNotNull(budget);
        assertEquals(request.getName(), budget.getName());
        assertEquals(request.getColor(), budget.getColor());
        assertEquals(request.getAmount(), budget.getAmount());

        verify(this.budgetRepository).save(budget);
        verifyNoMoreInteractions(this.budgetRepository);
    }

    @Test
    void deleteBudget_ReturnsValidResponse() {
        // given
        Budget budget = new Budget("0 65% 50%", LocalDate.now(), "Groceries", 500, null);
        budget.setId(1);

        willDoNothing().given(budgetRepository).deleteById(budget.getId());

        // when
        String response = this.controller.deleteBudget(budget.getId());

        // then
        assertNotNull(response);
        assertEquals(response, "Budget with id = " + budget.getId() + " successfully deleted!");

        verify(this.budgetRepository).deleteById(budget.getId());
        verifyNoMoreInteractions(this.budgetRepository);
    }
}