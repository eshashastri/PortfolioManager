// api.js
const API_BASE = "http://localhost:8080";

/* -------- STOCK SEARCH -------- */
export async function searchStocks(query) {
    const res = await fetch(`${API_BASE}/stocks/search?q=${query}`);
    return res.json();
}

/* -------- SUBSCRIPTIONS -------- */
export async function getSubscriptions() {
    const res = await fetch(`${API_BASE}/subscriptions`);
    return res.json();
}

export async function addSubscription(stock) {
    const res = await fetch(`${API_BASE}/subscriptions`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            ticker: stock.ticker,
            companyName: stock.companyName
        })
    });
    return res.json();
}

export async function deleteSubscription(ticker) {
    await fetch(`${API_BASE}/subscriptions/${ticker}`, {
        method: "DELETE"
    });
}

/* -------- STOCK PRICES -------- */
export async function getStockPrices(ticker) {
    const res = await fetch(`${API_BASE}/prices/${ticker}/all`);
    return res.json();
}
