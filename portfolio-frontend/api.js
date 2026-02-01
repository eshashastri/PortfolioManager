/************************************************
 MULTI STOCK FETCH SERVICE
************************************************/

export async function getStocks(tickers){

try{

const response = await fetch(
`http://127.0.0.1:5000/stocks?tickers=${tickers.join(",")}`
);

if(!response.ok){
throw new Error("API FAILED");
}

const raw = await response.json();

const formatted = [];

for(const ticker of tickers){

// VERY IMPORTANT GUARD
if(!raw[ticker]){
console.warn("No data for",ticker);
continue;
}

formatted.push({

ticker,

history: raw[ticker].map(d=>({
date:d.date,
close:d.close,
open:d.open,
high:d.high,
low:d.low,
volume:d.volume
}))

});

}

return formatted;

}catch(err){

console.error("Stock API error:",err);
return [];

}

}
