package com.pm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PortfolioTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_stock_id", nullable = false)
    private PortfolioStock portfolioStock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price; // price per share

    private LocalDateTime transactionTime = LocalDateTime.now();

    public PortfolioTransaction() {}

    public PortfolioTransaction(
            PortfolioStock portfolioStock,
            TransactionType type,
            int quantity,
            double price
    ) {
        this.portfolioStock = portfolioStock;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    // getters & setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PortfolioStock getPortfolioStock() {
        return portfolioStock;
    }

    public void setPortfolioStock(PortfolioStock portfolioStock) {
        this.portfolioStock = portfolioStock;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
