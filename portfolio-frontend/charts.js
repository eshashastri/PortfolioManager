import {getStocks} from "./api.js";

async function loadDashboard(){

const tickers = ["AAPL","MSFT","GOOG"];

console.log("Fetching stocks...");

const stocks = await getStocks(tickers);

console.log("Stocks:",stocks);


// 🔴 VERY IMPORTANT SAFETY CHECK
if(!stocks || stocks.length === 0){

document.getElementById("portfolioValue").innerText="Loading...";
return;
}


/************************************************
BUILD PORTFOLIO
************************************************/

let portfolio=[];

stocks.forEach(stock=>{

const prices = stock.history.map(h=>h.close);

if(prices.length===0) return;

const current = prices[prices.length-1];

// TEMP INVESTED VALUE
const invested = current * 0.85;

portfolio.push({
ticker:stock.ticker,
current,
invested,
history:stock.history
});

});


/************************************************
CALCULATE TOTALS
************************************************/

let totalInvested=0;
let totalCurrent=0;

portfolio.forEach(s=>{
totalInvested+=s.invested;
totalCurrent+=s.current;
});

const profit = totalCurrent-totalInvested;

const returnPercent =
((profit/totalInvested)*100).toFixed(2);



/************************************************
UPDATE UI
************************************************/

document.getElementById("portfolioValue").innerText=
"$"+totalCurrent.toFixed(2);

document.getElementById("invested").innerText=
"$"+totalInvested.toFixed(2);

document.getElementById("profit").innerText=
"$"+profit.toFixed(2);

document.getElementById("returnPercent").innerText=
returnPercent+"%";



/************************************************
DESTROY OLD CHARTS (IMPORTANT)
Prevents ghost charts
************************************************/

Chart.helpers.each(Chart.instances, function(instance){
instance.destroy();
});



/************************************************
PIE — Allocation
************************************************/

new Chart(document.getElementById("pieChart"),{

type:'doughnut',

data:{
labels:portfolio.map(s=>s.ticker),
datasets:[{
data:portfolio.map(s=>s.current),
backgroundColor:["#4f46e5","#22c55e","#f59e0b"]
}]
}

});



/************************************************
LINE — Portfolio Growth
************************************************/

const dates = portfolio[0].history.map(h=>h.date);

const growth = dates.map((_,i)=>{

let sum=0;

portfolio.forEach(s=>{
sum+=s.history[i].close;
});

return sum;

});

new Chart(document.getElementById("lineChart"),{

type:'line',

data:{
labels:dates,
datasets:[{
label:"Portfolio Growth",
data:growth,
borderColor:"#4f46e5",
tension:0.4
}]
}

});



/************************************************
BAR — Profit Loss
************************************************/

new Chart(document.getElementById("profitChart"),{

type:'bar',

data:{
labels:portfolio.map(s=>s.ticker),
datasets:[{
data:portfolio.map(s=>s.current-s.invested),
backgroundColor:portfolio.map(
s=>(s.current-s.invested)>=0?
"#16a34a":"#dc2626"
)
}]
}

});



/************************************************
INVESTED vs CURRENT
************************************************/

new Chart(document.getElementById("comparisonChart"),{

type:'bar',

data:{
labels:portfolio.map(s=>s.ticker),
datasets:[
{
label:"Invested",
data:portfolio.map(s=>s.invested),
backgroundColor:"#94a3b8"
},
{
label:"Current",
data:portfolio.map(s=>s.current),
backgroundColor:"#4f46e5"
}
]
}

});

}

loadDashboard();
