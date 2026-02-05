


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

async function renderPortfolioGrowth(holdings) {
    const dateValueMap = {};

    for (const h of holdings) {
        const prices = await fetch(
            `http://localhost:8080/prices/${h.ticker}/all`
        ).then(r => r.json());

        prices.forEach(p => {
            const date = p.priceDate;
            const price = p.closePrice || p.price || 0;

            if (!dateValueMap[date]) {
                dateValueMap[date] = 0;
            }

            dateValueMap[date] += h.quantity * price;
        });
    }

    // Sort by date
    const sortedDates = Object.keys(dateValueMap).sort(
        (a, b) => new Date(a) - new Date(b)
    );

    const values = sortedDates.map(d => dateValueMap[d]);

    const ctx = document.getElementById("lineChart");

    if (lineChartInstance instanceof Chart) {
        lineChartInstance.destroy();
    }

    lineChartInstance = new Chart(ctx, {
        type: "line",
        data: {
            labels: sortedDates,
            datasets: [{
                label: "Portfolio Value",
                data: values,
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

