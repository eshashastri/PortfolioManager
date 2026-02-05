


let pieChart, lineChart, profitChart, comparisonChart;

document.addEventListener("DOMContentLoaded", async () => {
    const holdings = await StockAPI.getHoldings();
    const transactions = await StockAPI.getTransactions();
    console.log(holdings);
    if (!holdings || holdings.length === 0) return;

    renderSummary(holdings);
    renderSectorAllocation(holdings);
    renderPortfolioGrowth(holdings);
    renderStockPL(holdings);
    renderComparison(holdings);
    renderHoldingsTable(holdings);
    renderLogs(transactions);
});


const API = "http://localhost:8080";

// chart instances (so we can destroy if needed)
async function loadDashboard() {
    const holdings = await StockAPI.getHoldings(); // already used elsewhere
    if (!holdings || holdings.length === 0) return;

    renderSummary(holdings);
    renderSectorAllocation(holdings);
    renderPortfolioGrowth(holdings);
    renderStockPL(holdings);
    renderComparison(holdings);
    renderHoldingsTable(holdings);
}

document.addEventListener("DOMContentLoaded", loadDashboard);


async function renderSummary(holdings) {
    let invested = 0;
    let current = 0;

    for (const h of holdings) {
        const latestPrice = await getLatestPrice(h.ticker);

        invested += h.quantity * h.avgBuyPrice;
        current += h.quantity * latestPrice;
    }

    const profit = current - invested;
    const returnPct = invested > 0 ? (profit / invested) * 100 : 0;

    document.getElementById("portfolioValue").innerText = `$${current.toFixed(2)}`;
    document.getElementById("invested").innerText = `$${invested.toFixed(2)}`;
    document.getElementById("profit").innerText =
        `${profit >= 0 ? "+" : ""}$${profit.toFixed(2)}`;
    document.getElementById("returnPercent").innerText =
        `${returnPct.toFixed(2)}%`;
    document.getElementById("assetCount").innerText = holdings.length;
}

let pieChartInstance = null;

async function renderSectorAllocation(holdings) {
    const sectorMap = {};

    for (const h of holdings) {
        const latestPrice = await getLatestPrice(h.ticker);
        const value = h.quantity * latestPrice;

        sectorMap[h.sector] = (sectorMap[h.sector] || 0) + value;
    }

    const labels = Object.keys(sectorMap);
    const values = Object.values(sectorMap);

    const ctx = document.getElementById("pieChart");

    // ✅ SAFELY destroy old chart
    if (pieChartInstance instanceof Chart) {
        pieChartInstance.destroy();
    }

    // ✅ Create new chart
    pieChartInstance = new Chart(ctx, {
        type: "pie",
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    "#2563eb",
                    "#10b981",
                    "#f59e0b",
                    "#ef4444",
                    "#8b5cf6",
                    "#14b8a6"
                ]
            }]
        },
        options: {
            plugins: {
                legend: {
                    position: "bottom"
                }
            }
        }
    });
}


let lineChartInstance = null;

async function renderPortfolioGrowth() {
    const dateValueMap = {};  // To track portfolio value over time
    let cumulativeValue = 0;  // To keep track of cumulative portfolio value

    // Fetch all transactions from the portfolio (from your backend)
    const transactions = await fetch("http://localhost:8080/portfolio/transactions").then(res => res.json());
    console.log("Transactions fetched: ", transactions);  // Debugging: Log the transactions

    // Fetch market prices for each stock (use the prices API)
    const priceData = {};

    for (const tx of transactions) {
        if (!priceData[tx.ticker]) {
            priceData[tx.ticker] = await fetch(`http://localhost:8080/prices/${tx.ticker}/all`)
                .then(r => r.json());
        }
    }

    // Track the holdings and calculate portfolio value
    const stockHoldings = {};  // {ticker: quantityOwned}
    
    transactions.forEach(tx => {
        const priceDataForStock = priceData[tx.ticker];

        // Get market prices for the specific stock and transaction date
        const transactionDate = tx.transactionTime;
        const marketPrice = priceDataForStock.find(p => p.priceDate === transactionDate)?.closePrice;

        // Handle the case where marketPrice is undefined
        if (!marketPrice) {
            console.error(`Market price not found for ${tx.ticker} on ${transactionDate}`);
            return;  // Skip if the price is missing for that transaction date
        }

        // Handle buy transaction
        if (tx.type === "BUY") {
            // Add quantity to the stock holdings
            if (!stockHoldings[tx.ticker]) {
                stockHoldings[tx.ticker] = 0;
            }
            stockHoldings[tx.ticker] += tx.quantity;

            console.log(`Bought ${tx.quantity} of ${tx.ticker} at ${marketPrice} on ${transactionDate}`);
        }

        // Handle sell transaction
        if (tx.type === "SELL") {
            if (!stockHoldings[tx.ticker] || stockHoldings[tx.ticker] <= 0) {
                console.error(`Trying to sell ${tx.quantity} of ${tx.ticker} but no stock owned`);
                return; // If no stock to sell, skip this transaction
            }

            // Calculate the market value of the sold quantity
            const soldValue = tx.quantity * marketPrice;
            stockHoldings[tx.ticker] -= tx.quantity;  // Decrease the stock quantity

            console.log(`Sold ${tx.quantity} of ${tx.ticker} at ${marketPrice} on ${transactionDate}, Value: $${soldValue}`);

            // Add this sell value to the cumulative portfolio value
            cumulativeValue += soldValue;
        }

        // Calculate portfolio value after each transaction (buy + sell)
        let portfolioValue = cumulativeValue;

        // Add the value of remaining holdings to the portfolio value
        for (const ticker in stockHoldings) {
            if (stockHoldings[ticker] > 0) {
                const remainingStockQuantity = stockHoldings[ticker];
                const remainingStockMarketPrice = priceDataForStock.find(p => p.priceDate === transactionDate)?.closePrice || 0;
                portfolioValue += remainingStockQuantity * remainingStockMarketPrice;
            }
        }

        // Track portfolio value for the given date
        dateValueMap[transactionDate] = portfolioValue;

        console.log(`Portfolio value on ${transactionDate}: $${portfolioValue}`);
    });

    // Calculate cumulative portfolio values for each date
    const sortedDates = Object.keys(dateValueMap).sort((a, b) => new Date(a) - new Date(b));

    let cumulativeValues = [];
    let totalCumulativeValue = 0;

    sortedDates.forEach(date => {
        totalCumulativeValue += dateValueMap[date];  // Add value for the current date to the cumulative value
        cumulativeValues.push(totalCumulativeValue);  // Store cumulative value at each date
    });

    console.log("Sorted dates: ", sortedDates);  // Debugging: Log sorted dates
    console.log("Cumulative values: ", cumulativeValues);  // Debugging: Log cumulative values

    // Create chart context
    const ctx = document.getElementById("lineChart");

    if (!ctx) {
        console.error("Chart canvas element not found!");
        return;  // Exit if the element is not found
    }

    // Destroy the previous chart if exists
    if (lineChartInstance && lineChartInstance instanceof Chart) {
        lineChartInstance.destroy();
    }

    // Create a new chart with cumulative portfolio value
    lineChartInstance = new Chart(ctx, {
        type: "line",
        data: {
            labels: sortedDates,
            datasets: [{
                label: "Cumulative Portfolio Value",
                data: cumulativeValues,
                borderColor: "#2563eb",
                backgroundColor: "rgba(37,99,235,0.15)",
                tension: 0.3,
                fill: true,
                pointRadius: 0
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    ticks: { maxTicksLimit: 8 }
                },
                y: {
                    ticks: {
                        callback: v => `$${v.toFixed(0)}`
                    }
                }
            }
        }
    });
}


let profitChartInstance = null;

async function renderStockPL(holdings) {
    const labels = [];
    const values = [];
    const colors = [];

    for (const h of holdings) {
        const latestPrice = await getLatestPrice(h.ticker);
        const pl = (latestPrice - h.avgBuyPrice) * h.quantity;

        labels.push(h.ticker);
        values.push(pl);
        colors.push(pl >= 0 ? "#10b981" : "#ef4444");
    }

    const ctx = document.getElementById("profitChart");

    if (profitChartInstance instanceof Chart) {
        profitChartInstance.destroy();
    }

    profitChartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label: "Profit / Loss",
                data: values,
                backgroundColor: colors
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx =>
                            `$${ctx.raw.toFixed(2)}`
                    }
                }
            },
            scales: {
                y: {
                    ticks: {
                        callback: v => `$${v}`
                    }
                }
            }
        }
    });
}

let comparisonChartInstance = null;

async function renderComparison(holdings) {
    let invested = 0;
    let current = 0;

    for (const h of holdings) {
        const latestPrice = await getLatestPrice(h.ticker);

        invested += h.quantity * h.avgBuyPrice;
        current += h.quantity * latestPrice;
    }

    const ctx = document.getElementById("comparisonChart");

    if (comparisonChartInstance instanceof Chart) {
        comparisonChartInstance.destroy();
    }

    comparisonChartInstance = new Chart(ctx, {
        type: "bar",
        data: {
            labels: ["Invested", "Current"],
            datasets: [{
                data: [invested, current],
                backgroundColor: ["#94a3b8", "#2563eb"]
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => `$${ctx.raw.toFixed(2)}`
                    }
                }
            },
            scales: {
                y: {
                    ticks: {
                        callback: v => `$${v}`
                    }
                }
            }
        }
    });
}

function renderHoldingsTable(holdings) {
    const tbody = document.querySelector("#holdingsTable tbody");
    tbody.innerHTML = "";

    holdings.forEach(h => {
        const invested = h.quantity * h.avgBuyPrice;
        const current = h.quantity * h.currentPrice;
        const pl = current - invested;

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${h.ticker}</td>
            <td>$${invested.toFixed(2)}</td>
            <td>$${current.toFixed(2)}</td>
            <td style="color:${pl >= 0 ? '#10b981' : '#ef4444'}">
                ${pl >= 0 ? "+" : ""}$${pl.toFixed(2)}
            </td>
        `;
        tbody.appendChild(row);
    });
}
function renderLogs(transactions) {
    const tbody = document.querySelector("#logsTable tbody");
    if (!tbody) return;

    tbody.innerHTML = "";

    transactions.forEach(tx => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${tx.transactionTime.split("T")[0]}</td>
            <td>${tx.type}</td>
            <td>${tx.ticker}</td>
            <td>${tx.quantity}</td>
            <td>$${tx.price}</td>
        `;
        tbody.appendChild(tr);
    });
}

async function getLatestPrice(ticker) {
    const prices = await fetch(`http://localhost:8080/prices/${ticker}/all`)
        .then(r => r.json());

    return prices.at(-1)?.closePrice || prices.at(-1)?.price || 0;
}

