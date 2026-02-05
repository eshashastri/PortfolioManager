async function initPredictionPage() {
    const params = new URLSearchParams(window.location.search);
    const ticker = params.get("ticker");
    const name = params.get("name");

    if (!ticker) {
        document.getElementById("predictionTitle").innerText = "No stock selected";
        document.getElementById("predictionSubtitle").innerText =
            "Return to the Subscriptions page and choose a stock to view predictions.";
        return;
    }

    const displayName = name || ticker;
    document.getElementById("predictionTitle").innerText = `${displayName} (${ticker})`;

    try {
        // 1) Fetch full price history to get latest close
        const prices = await StockAPI.getStockPrices(ticker);
        if (!prices || prices.length === 0) {
            document.getElementById("predictionSubtitle").innerText =
                "No historical price data available for this stock.";
            return;
        }

        // Ensure sorted by date ascending
        prices.sort((a, b) => new Date(a.priceDate) - new Date(b.priceDate));
        const latest = prices[prices.length - 1];
        const currentPrice = latest.closePrice ?? latest.price ?? 0;
        const lastDate = latest.priceDate;

        document.getElementById("currentPriceDisplay").innerText =
            currentPrice ? `$${currentPrice.toFixed(2)}` : "—";
        document.getElementById("lastPriceDate").innerText =
            lastDate ? `Last close: ${lastDate}` : "Last close: —";

        // 2) Fetch prediction from backend
        const prediction = await StockAPI.getPrediction(ticker);
        const forecast = Array.isArray(prediction.forecast)
            ? prediction.forecast.slice(0, 5)
            : [];

        const accuracyEl = document.getElementById("modelAccuracy");
        if (prediction.accuracy) {
            accuracyEl.innerText = `Model accuracy: ${prediction.accuracy}`;
        } else {
            accuracyEl.innerText = "Model accuracy: —";
        }

        if (forecast.length === 0) {
            document.getElementById("predictionSubtitle").innerText =
                "No forecast values returned by the model.";
            return;
        }

        const historyValues = prices.map(p => p.closePrice ?? p.price ?? 0);
        const today = new Date();
        const forecastLabels = forecast.map((_, idx) => {
            const d = new Date(today);
            d.setDate(d.getDate() + idx + 1);
            return d.toISOString().split("T")[0];
        });

        // 3) Build 5-day forecast table vs current price (centered card)
        const tbody = document.getElementById("predictionTableBody");
        tbody.innerHTML = "";

        forecast.forEach((value, idx) => {
            const rowDate = new Date(today);
            rowDate.setDate(rowDate.getDate() + idx + 1);
            const dateStr = rowDate.toISOString().split("T")[0];

            const diff = value - currentPrice;
            const trend =
                Math.abs(diff) < 1e-6
                    ? "Same"
                    : diff > 0
                        ? "Higher"
                        : "Lower";

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>Day ${idx + 1}</td>
                <td>${dateStr}</td>
                <td>$${value.toFixed(2)}</td>
                <td>
                    <span class="trend-badge ${trend === "Higher" ? "trend-up" : trend === "Lower" ? "trend-down" : "trend-flat"}">
                        ${trend}
                    </span>
                </td>
                <td>${diff >= 0 ? "+" : ""}$${diff.toFixed(2)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error("Prediction page load failed", err);
        document.getElementById("predictionSubtitle").innerText =
            "Unable to load prediction data. Please ensure the backend and ML service are running.";
    }
}

document.addEventListener("DOMContentLoaded", initPredictionPage);

