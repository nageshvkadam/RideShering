package com.example.expensetracker.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.ExpenseService;

@Controller
public class ExpenseController {

    @Autowired private ExpenseService expenseService;
    @Autowired private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) return "redirect:/login";

        model.addAttribute("expenses", expenseService.listExpenses(user));
        Map<String, Double> categoryData = expenseService.getCategoryWiseTotals(user);
        model.addAttribute("categoryData", categoryData);
        
        model.addAttribute("username", user.getUsername()); 

        return "dashboard";
    }


    @GetMapping("/expense/add")
    public String addExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "expenses";
    }

    @PostMapping("/expense/add")
    public String addExpense(@ModelAttribute Expense expense, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) return "redirect:/login";

        expense.setUser(user);
        expenseService.addExpense(expense);
        return "redirect:/dashboard";
    }

    @GetMapping("/expense/edit/{id}")
    public String editExpense(@PathVariable Long id, Model model, Principal principal) {
        Expense expense = expenseService.getExpense(id).orElse(null);
        if (expense == null) return "redirect:/dashboard";

        
        User currentUser = userRepository.findByUsername(principal.getName());
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("expense", expense);
        return "expenses";
    }

    @PostMapping("/expense/edit/{id}")
    public String updateExpense(@ModelAttribute Expense expense, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null) return "redirect:/login";

        expense.setUser(user); 
        expenseService.updateExpense(expense);
        return "redirect:/dashboard";
    }

    @GetMapping("/expense/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Principal principal) {
        Expense expense = expenseService.getExpense(id).orElse(null);
        if (expense == null) return "redirect:/dashboard";

        User currentUser = userRepository.findByUsername(principal.getName());
        if (!expense.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/dashboard"; 
        }

        expenseService.deleteExpense(id);
        return "redirect:/dashboard";
    }
}
