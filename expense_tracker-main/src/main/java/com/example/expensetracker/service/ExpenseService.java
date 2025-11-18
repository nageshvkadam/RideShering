package com.example.expensetracker.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public List<Expense> listExpenses(User user) {
        return expenseRepository.findByUser(user);
    }

    @Transactional
    public void addExpense(Expense expense) {
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        expenseRepository.save(expense);
    }

    @Transactional
    public void updateExpense(Expense expense) {
        expense.setUpdatedAt(LocalDateTime.now());
        expenseRepository.save(expense);
    }

    @Transactional
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public Optional<Expense> getExpense(Long id) {
        return expenseRepository.findById(id);
    }

    
    public Map<String, Double> getCategoryWiseTotals(User user) {
        List<Expense> expenses = expenseRepository.findByUser(user);
        Map<String, Double> totals = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double amount = expense.getAmount();
            totals.put(category, totals.getOrDefault(category, 0.0) + amount);
        }

        return totals;
    }

}
