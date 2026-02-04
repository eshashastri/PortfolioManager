
import { StockAPI } from "./script.js"; // reuse existing API logic

let pieChart, lineChart, profitChart, comparisonChart;

document.addEventListener("DOMContentLoaded", async () => {
    const holdings = await StockAPI.getHoldings();
    const transactions = await StockAPI.getTransactions();

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


function renderSummary(holdings) {
    let invested = 0;
    let current = 0;

    holdings.forEach(h => {
        invested += h.quantity * h.avgBuyPrice;
        current += h.quantity * h.currentPrice;
    });

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

function renderSectorAllocation(holdings) {
    const sectorMap = {};

    holdings.forEach(h => {
        const value = h.quantity * h.currentPrice;
        sectorMap[h.sector] = (sectorMap[h.sector] || 0) + value;
    });

    const labels = Object.keys(sectorMap);
    const values = Object.values(sectorMap);

    if (pieChart) pieChart.destroy();

    pieChart = new Chart(document.getElementById("pieChart"), {
        type: "pie",
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: [
                    "#2563eb", "#10b981", "#f59e0b",
                    "#ef4444", "#8b5cf6", "#14b8a6"
                ]
            }]
        }
    });
}
function renderPortfolioGrowth(holdings) {
    let cumulative = 0;
    const labels = [];
    const values = [];

    holdings.forEach((h, i) => {
        cumulative += h.quantity * h.currentPrice;
        labels.push(h.ticker);
        values.push(cumulative);
    });

    if (lineChart) lineChart.destroy();

    lineChart = new Chart(document.getElementById("lineChart"), {
        type: "line",
        data: {
            labels,
            datasets: [{
                label: "Portfolio Value",
                data: values,
                borderColor: "#2563eb",
                tension: 0.3,
                fill: false
            }]
        }
    });
}
function renderStockPL(holdings) {
    const labels = holdings.map(h => h.ticker);
    const values = holdings.map(
        h => (h.currentPrice - h.avgBuyPrice) * h.quantity
    );

    if (profitChart) profitChart.destroy();

    profitChart = new Chart(document.getElementById("profitChart"), {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label: "Profit / Loss",
                data: values,
                backgroundColor: values.map(v => v >= 0 ? "#10b981" : "#ef4444")
            }]
        }
    });
}
function renderComparison(holdings) {
    let invested = 0;
    let current = 0;

    holdings.forEach(h => {
        invested += h.quantity * h.avgBuyPrice;
        current += h.quantity * h.currentPrice;
    });

    if (comparisonChart) comparisonChart.destroy();

    comparisonChart = new Chart(
        document.getElementById("comparisonChart"), {
            type: "bar",
            data: {
                labels: ["Invested", "Current"],
                datasets: [{
                    data: [invested, current],
                    backgroundColor: ["#94a3b8", "#2563eb"]
                }]
            }
        }
    );
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
