fetch("http://localhost:8080/stocks")
const API = "http://localhost:8080";

const email = "test@gmail.com"; // later make dynamic

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
