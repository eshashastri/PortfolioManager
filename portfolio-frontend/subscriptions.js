let stocks=[];

// Load stock list (simple list instead of NASDAQ fetch)
stocks = [
    {ticker:"AAPL",name:"Apple"},
    {ticker:"GOOG",name:"Google"},
    {ticker:"MSFT",name:"Microsoft"},
    {ticker:"TSLA",name:"Tesla"},
    {ticker:"AMZN",name:"Amazon"},
    {ticker:"META",name:"Meta"}
];

const search=document.getElementById("stockSearch");
const suggestions=document.getElementById("suggestions");

search.addEventListener("input",()=>{
    const val=search.value.toLowerCase();
    suggestions.innerHTML="";

    if(!val) return;

    stocks
    .filter(s=>s.name.toLowerCase().includes(val))
    .forEach(s=>{
        const div=document.createElement("div");
        div.innerText=`${s.name} (${s.ticker})`;

        div.onclick=()=>subscribe(s);

        suggestions.appendChild(div);
    });
});


// Save subscription
function subscribe(stock){
fetch("http://localhost:8080/subscriptions",{
    method:"POST",
    headers:{"Content-Type":"application/json"},
    body:JSON.stringify({
        email:"test@mail.com",
        ticker:stock.ticker,
        name:stock.name
    })
}).then(()=>loadSubs());
}


// Load subscriptions
function loadSubs(){
fetch("http://localhost:8080/subscriptions/test@mail.com")
.then(r=>r.json())
.then(data=>{
    const box=document.getElementById("mySubs");
    box.innerHTML="";

    data.forEach(s=>{
        const div=document.createElement("div");
        div.className="stockBox";
        div.innerText=`${s.name} (${s.ticker})`;

        div.onclick=()=>loadChart(s.ticker);

        box.appendChild(div);
    });
});
}

loadSubs();


// Load chart
function loadChart(ticker){

fetch(`https://query1.finance.yahoo.com/v8/finance/chart/${ticker}?range=5d&interval=1h`)
.then(r=>r.json())
.then(d=>{

const result=d.chart.result[0];

const prices=result.indicators.quote[0].close;
const labels=result.timestamp.map(t=>
    new Date(t*1000).toLocaleDateString()
);

new Chart(document.getElementById("chart"),{
    type:"line",
    data:{
        labels:labels,
        datasets:[{
            label:ticker,
            data:prices
        }]
    }
});

});
}

