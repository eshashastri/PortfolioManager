// api.js

const API_BASE = "http://localhost:8080";

const StockAPI = {
    /* -------- STOCK SEARCH -------- */
    searchStocks: async (query) => {
        const res = await fetch(`${API_BASE}/stocks/search?q=${query}`);
        return res.json();
    },

    /* -------- SUBSCRIPTIONS -------- */
    getSubscriptions: async () => {
        const res = await fetch(`${API_BASE}/subscriptions`);
        return res.json();
    },

    addSubscription: async (stock) => {
        const res = await fetch(`${API_BASE}/subscriptions`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                ticker: stock.ticker,
                companyName: stock.companyName
            })
        });
        return res.json();
    },

    deleteSubscription: async (ticker) => {
        await fetch(`${API_BASE}/subscriptions/${ticker}`, {
            method: "DELETE"
        });
    },
    getHoldings: async () => {
        const res = await fetch(`${API_BASE}/portfolio/holdings`);
        return res.json();
    },

    /* -------- TRANSACTIONS -------- */
    getTransactions: async () => {
        const res = await fetch(`${API_BASE}/portfolio/transactions`);
        console.log(res);
        return res.json();
    },

    /* -------- STOCK PRICES -------- */
    getStockPrices: async (ticker) => {
        const res = await fetch(`${API_BASE}/prices/${ticker}/all`);
        return res.json();
    },

    /* -------- PREDICTIONS (ML MODEL) -------- */
    getPrediction: async (ticker) => {
        const res = await fetch(`${API_BASE}/predictions/${ticker}`);
        if (!res.ok) {
            throw new Error("Prediction request failed");
        }
        return res.json();
    }
};
