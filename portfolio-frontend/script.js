const API = "http://localhost:8080";

let selectedTicker = null;
// SEARCH
document.getElementById("search")
.addEventListener("input", async (e) => {

    const val = e.target.value;

    if(val.length < 2) return;

    const res = await fetch(
        `${API}/stocks/search?keyword=${val}`
    );

    const data = await res.json();

    const box = document.getElementById("results");
    box.innerHTML = "";

    data.forEach(s => {
        const d = document.createElement("div");
        d.innerText = `${s.companyName} (${s.ticker})`;

        d.onclick = () => subscribe(s);

        box.appendChild(d);
    });
});


// SUBSCRIBE
async function subscribe(stock) {

    await fetch(`${API}/subscriptions`, {
        method:"POST",
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({
            ticker:stock.ticker,
            companyName:stock.companyName,
            email:email
        })
    });

    loadSubs();
}


// LOAD SUBSCRIPTIONS
async function loadSubs(){

    const res = await fetch(
        `${API}/subscriptions/${email}`
    );

    const data = await res.json();

    const box = document.getElementById("subs");
    box.innerHTML="";

    data.forEach(s=>{
        const d = document.createElement("div");
        d.className="card";
        d.innerText = s.companyName;

        d.onclick = ()=>showStock(s.ticker);

        box.appendChild(d);
    });
}


// SHOW STOCK PRICE
async function showStock(ticker){

    const res = await fetch(
        `${API}/stock-price/${ticker}`
    );

    const data = await res.json();

    alert(
        `${ticker} Price: ${data.price}`
    );
}

loadSubs();
const searchBox = document.getElementById("search");
const suggestionsBox = document.getElementById("suggestions");
const subsDiv = document.getElementById("subs");

let subscriptions = [];
let myChart = null;

/* --- API LOGIC: SEARCH --- */
searchBox.addEventListener("input", async () => {
    const q = searchBox.value;
    if (q.length < 1) {
        suggestionsBox.innerHTML = "";
        return;
    }
    try {
        const res = await fetch(`http://localhost:8080/stocks/search?q=${q}`);
        const data = await res.json();
        suggestionsBox.innerHTML = "";
        data.forEach(stock => {
            const div = document.createElement("div");
            div.className = "suggestion-item";
            div.innerText = `${stock.companyName} (${stock.ticker})`;
            div.onclick = () => addSubscription(stock);
            suggestionsBox.appendChild(div);
        });
    } catch (err) {
        console.error("Search failed", err);
    }
});


function selectStock(stock) {
    // Fill input with company name (UX)
    searchBox1.value = stock.companyName;

    // Store ticker internally (logic)
    selectedTicker = stock.ticker;

    // Optional: auto-fill sector if you want later
    // document.getElementById("sectorInput").value = stock.sector;

    // Clear suggestions
    searchResults.innerHTML = "";
}



/* --- API LOGIC: ADD --- */
async function addSubscription(stock) {
    try {
        const res = await fetch("http://localhost:8080/subscriptions", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ticker: stock.ticker, companyName: stock.companyName })
        });
        const saved = await res.json();
        subscriptions.push(saved);
        renderSubs();
        suggestionsBox.innerHTML = "";
        searchBox.value = "";
    } catch (err) {
        alert("Error adding stock");
    }
}

/* --- UI LOGIC: RENDER --- */
function renderSubs() {
    subsDiv.innerHTML = "";
    subscriptions.forEach(s => {
        const div = document.createElement("div");
        div.className = "sub-item";
        div.onclick = () => showChart(s.ticker, s.companyName);
        div.innerHTML = `
            <div>
                <b>${s.companyName}</b><br>
                <span>${s.ticker}</span>
            </div>
            <button onclick="event.stopPropagation(); removeSub('${s.ticker}')">Remove</button>
        `;
        subsDiv.appendChild(div);
    });
}

/* --- API LOGIC: DELETE --- */
async function removeSub(ticker) {
    try {
        await fetch(`http://localhost:8080/subscriptions/${ticker}`, { method: "DELETE" });
        subscriptions = subscriptions.filter(s => s.ticker !== ticker);
        renderSubs();
    } catch (err) {
        console.error("Delete failed", err);
    }
}

/* --- TRADING CHART LOGIC --- */
async function showChart(ticker, companyName) {
    document.getElementById("stockModal").style.display = "flex";
    document.getElementById("modalTitle").innerText = `${companyName} (${ticker})`;

    try {
        const res = await fetch(`http://localhost:8080/prices/${ticker}/all`);
        const data = await res.json();

        if (!data || data.length === 0) {
            alert("No database records found for " + ticker);
            return;
        }

        // Sort data chronologically
        data.sort((a, b) => new Date(a.priceDate) - new Date(b.priceDate));

        const labels = data.map(p => p.priceDate);
        const prices = data.map(p => p.closePrice || p.price || 0);

        if (myChart) {
            myChart.destroy();
        }

        const ctx = document.getElementById('stockChart').getContext('2d');

        // TradingView Style Gradient
        const gradient = ctx.createLinearGradient(0, 0, 0, 400);
        gradient.addColorStop(0, 'rgba(59, 130, 246, 0.4)');
        gradient.addColorStop(1, 'rgba(59, 130, 246, 0)');

        myChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Price',
                    data: prices,
                    borderColor: '#2563eb',
                    borderWidth: 2,
                    fill: true,
                    backgroundColor: gradient,
                    tension: 0.15,
                    pointRadius: 0,
                    pointHoverRadius: 6,
                    pointHoverBackgroundColor: '#2563eb',
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: '#1e293b',
                        padding: 12,
                        titleFont: { size: 14, weight: '600' },
                        callbacks: { label: (ctx) => ` $${ctx.raw.toFixed(2)}` }
                    }
                },
                scales: {
                    x: { 
                        grid: { display: false }, 
                        ticks: { color: '#94a3b8' } 
                    },
                    y: { 
                        position: 'right', 
                        grid: { color: '#f1f5f9' },
                        ticks: { 
                            color: '#94a3b8',
                            callback: (val) => '$' + val 
                        }
                    }
                }
            }
        });
    } catch (err) {
        console.error("Chart load failed", err);
    }
}



/* --- INITIAL LOAD --- */
async function loadSubscriptions() {
    try {
        const res = await fetch("http://localhost:8080/subscriptions");
        subscriptions = await res.json();
        renderSubs();
    } catch (err) {
        console.error("Initial load failed", err);
    }
}

loadSubscriptions();
/* --- STATE --- */
let holdings = [
    {ticker: "AAPL", qty: 10, buy: 150, current: 185},
    {ticker: "MSFT", qty: 6, buy: 220, current: 260}
];

/* --- MODAL CONTROLS --- */
function openModal() {
    document.getElementById("modal").style.display = "block";
    modal.style.display = "flex";
}

function closeModal() {
    
    document.getElementById("modal").style.display = "none";
    // Clear inputs on close
    document.getElementById("companyName").value="";
    document.getElementById("tickerInput").value = "";
    document.getElementById("qtyInput").value = "";
    document.getElementById("priceInput").value = "";
}

/* --- ADD STOCK --- */
async function addStock() {
    const quantity = parseInt(document.getElementById("qtyInput").value);
    const price = parseFloat(document.getElementById("priceInput").value);
    const sector = document.getElementById("sectorInput").value;

    if (!selectedStock || !quantity || !price || !sector) {
        alert("Please select a stock and fill all fields");
        return;
    }

    // 1️⃣ BUY
    const buyRes = await fetch("http://localhost:8080/portfolio/buy", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            ticker: selectedStock.ticker,
            quantity,
            price,
            sector
        })
    });

    if (!buyRes.ok) {
        alert("Buy failed");
        return;
    }

    // 2️⃣ SUBSCRIBE (reuse SAME stock object)
    const subRes = await fetch("http://localhost:8080/subscriptions", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            ticker: selectedStock.ticker,
            companyName: selectedStock.companyName
        })
    });

    if (!subRes.ok) {
        alert("Subscription failed");
        return;
    }

    closeModal();
    loadHoldings();

    // reset state
    selectedTicker = null;
    selectedStock = null;
    document.getElementById("companyInput").value = "";
}


/* --- DELETE STOCK --- */
function deleteStock(index) {
    if(confirm(`Remove ${holdings[index].ticker} from holdings?`)) {
        holdings.splice(index, 1);
        renderTable();
    }
}

/* --- RENDER TABLE & SUMMARY --- */
function renderTable() {
    const tbody = document.querySelector("#holdingsTable tbody");
    tbody.innerHTML = "";

    let totalInvested = 0;
    let totalCurrent = 0;

    holdings.forEach((s, index) => {
        const invested = s.qty * s.buy;
        const value = s.qty * s.current;
        const pl = value - invested;

        totalInvested += invested;
        totalCurrent += value;

        const row = document.createElement("tr");
        row.innerHTML = `
            <td><strong>${s.ticker}</strong></td>
            <td>${s.qty}</td>
            <td>$${s.buy.toLocaleString()}</td>
            <td>$${s.current.toLocaleString()}</td>
            <td>$${invested.toLocaleString()}</td>
            <td>$${value.toLocaleString()}</td>
            <td style="color: ${pl >= 0 ? '#10b981' : '#ef4444'}; font-weight: 600;">
                ${pl >= 0 ? '+' : ''}$${pl.toLocaleString()}
            </td>
            <td>
                <button class="delete-btn" onclick="deleteStock(${index})">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Update Summary Cards
    document.getElementById("totalInvested").innerText = "$" + totalInvested.toLocaleString();
    document.getElementById("currentValue").innerText = "$" + totalCurrent.toLocaleString();
    
    const totalPL = totalCurrent - totalInvested;
    const plElement = document.getElementById("totalPL");
    plElement.innerText = (totalPL >= 0 ? '+' : '') + "$" + totalPL.toLocaleString();
    plElement.style.color = totalPL >= 0 ? '#10b981' : '#ef4444';
}
function closeModal() {
    const stockModal = document.getElementById("stockModal");
    const addModal = document.getElementById("modal");
    if (stockModal) stockModal.style.display = "none";
    if (addModal) addModal.style.display = "none";
    
    // Clear inputs
    if(document.getElementById("tickerInput")) {
        document.getElementById("tickerInput").value = "";
        document.getElementById("qtyInput").value = "";
        document.getElementById("priceInput").value = "";
    }
}
// Initial Render
renderTable();

//transactions page loading

async function loadTransactions() {
    const filter = document.getElementById("filter").value;

    const transactions = await StockAPI.getTransactions();

    let totalBuys = 0;
    let totalSells = 0;

    const tbody = document.querySelector("#transactionsTable tbody");
    tbody.innerHTML = "";

    transactions
        .filter(tx => filter === "ALL" || tx.type === filter)
        .forEach(tx => {
            if (tx.type === "BUY") totalBuys++;
            if (tx.type === "SELL") totalSells++;
            const date = new Date(tx.transactionTime).toISOString().split('T')[0];
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${date}</td>
                <td>${tx.type}</td>
                <td>${tx.ticker}</td>
                <td>${tx.companyName}</td>
                <td>${tx.quantity}</td>
                <td>$${tx.price}</td>
                <td>$${(tx.quantity * tx.price).toFixed(2)}</td>
            `;
            tbody.appendChild(row);
        });

    document.getElementById("totalTrades").innerText =
        `${transactions.length} Trades`;
    document.getElementById("totalBuys").innerText = totalBuys;
    document.getElementById("totalSells").innerText = totalSells;
}
