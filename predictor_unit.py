import os
import numpy as np
import pandas as pd
import mysql.connector
import pandas_ta as ta
import tensorflow as tf
from flask import Flask, jsonify
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_absolute_percentage_error
from tensorflow.keras.models import Sequential, load_model
from tensorflow.keras.layers import LSTM, Dense, Dropout, BatchNormalization, Bidirectional, Input
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.losses import Huber

# Consistency seeds
np.random.seed(42)
tf.random.set_seed(42)

app = Flask(__name__)

# CONFIG - Matches your MySQL setup
DB_CONFIG = {
    'host': 'localhost', 
    'user': 'root', 
    'password': 'n3u3da!', 
    'database': 'portfolio'
}
MODEL_DIR = "saved_models"
LOOKBACK = 60
FORECAST = 5

if not os.path.exists(MODEL_DIR): 
    os.makedirs(MODEL_DIR)

def get_db_conn():
    return mysql.connector.connect(**DB_CONFIG)

def engineer_features(df):
    # Rename your DB column 'close_price' to 'Close' for technical analysis library
    df = df.rename(columns={'close_price': 'Close'})
    
    # Calculate Features
    df['Log_Ret'] = np.log(df['Close'] / df['Close'].shift(1))
    df['RSI'] = ta.rsi(df['Close'], length=14)
    sma20 = ta.sma(df['Close'], length=20)
    sma50 = ta.sma(df['Close'], length=50)
    df['SMA_Trend'] = (sma20 - sma50) / (sma50 + 1e-9)
    
    bbands = ta.bbands(df['Close'], length=20)
    # Finding Bollinger Band columns dynamically
    l_col = [c for c in bbands.columns if 'BBL' in c][0] if not bbands.empty else None
    u_col = [c for c in bbands.columns if 'BBU' in c][0] if not bbands.empty else None
    
    df['BB_Pct'] = (df['Close'] - bbands[l_col]) / (bbands[u_col] - bbands[l_col] + 1e-9) if l_col else 0.5
    
    cols = ['Log_Ret', 'RSI', 'SMA_Trend', 'BB_Pct']
    return df[cols].dropna(), df['Close']

def build_model():
    model = Sequential([
        Input(shape=(LOOKBACK, 4)),
        Bidirectional(LSTM(128, return_sequences=True)),
        BatchNormalization(),
        Dropout(0.2),
        Bidirectional(LSTM(64)),
        Dense(32, activation='relu'),
        Dense(1)
    ])
    model.compile(optimizer=Adam(learning_rate=0.001), loss=Huber())
    return model

def get_prediction_with_accuracy(ticker):
    model_path = os.path.join(MODEL_DIR, f"{ticker}.keras")
    conn = get_db_conn()
    
    # Updated SQL to match your exact columns: close_price, price_date, stock_id
    query = """
    SELECT sp.close_price
    FROM stock_price sp
    INNER JOIN stock s ON sp.stock_id = s.id
    WHERE s.ticker = %s
    ORDER BY sp.price_date ASC
    """
    df = pd.read_sql(query, conn, params=(ticker.upper(),))
    conn.close()

    # AI requires at least 110 days to calculate 50-day SMA and 60-day Lookback
    if len(df) < 110: 
        return None

    features, prices = engineer_features(df)
    actual_last_5 = prices.iloc[-FORECAST:].values
    
    scaler = StandardScaler()
    scaled = scaler.fit_transform(features)
    
    # Load or Train Logic
    if os.path.exists(model_path):
        model = load_model(model_path)
    else:
        X, y = [], []
        for i in range(LOOKBACK, len(scaled) - FORECAST): 
            X.append(scaled[i-LOOKBACK:i])
            y.append(features['Log_Ret'].iloc[i])
        model = build_model()
        model.fit(np.array(X), np.array(y), epochs=15, batch_size=32, verbose=0)
        model.save(model_path)

    # Forecast Logic
    current_window = scaled[-LOOKBACK-FORECAST:-FORECAST].reshape(1, LOOKBACK, 4)
    last_price = prices.iloc[-FORECAST-1]
    
    preds = []
    for _ in range(FORECAST):
        res = model.predict(current_window, verbose=0)[0,0]
        res = np.clip(res, -0.03, 0.03) # Cap daily swing at 3%
        new_p = last_price * np.exp(res)
        preds.append(new_p)
        
        # Shift window
        new_row = current_window[0, -1, :].copy()
        new_row[0] = res
        current_window = np.append(current_window[:, 1:, :], new_row.reshape(1,1,4), axis=1)
        last_price = new_p

    mape = mean_absolute_percentage_error(actual_last_5, preds)
    accuracy_percent = max(0, 100 - (mape * 100))

    return {
        "forecast": [round(float(p), 2) for p in preds],
        "accuracy": f"{round(accuracy_percent, 2)}%"
    }

@app.route('/predict/<ticker>')
def predict_one(ticker):
    try:
        result = get_prediction_with_accuracy(ticker.upper())
        if result is None:
            return jsonify({"error": "Insufficient data in stock_price table (Need 110+ days)"}), 400
        return jsonify(result)
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5001, debug=True)