import os
import numpy as np
import pandas as pd
import yfinance as yf
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

# Ensure results are consistent across runs
np.random.seed(42)
tf.random.set_seed(42)

app = Flask(__name__)

# CONFIG 
DB_CONFIG = {'host': 'localhost', 'user': 'root', 'password': 'n3u3da!', 'database': 'stock_project'}
MODEL_DIR = "saved_models"
LOOKBACK = 60
FORECAST = 5

if not os.path.exists(MODEL_DIR): os.makedirs(MODEL_DIR)

def get_db_conn():
    return mysql.connector.connect(**DB_CONFIG)

def safe_get_col(df, key):
    matches = [c for c in df.columns if key.upper() in c.upper()]
    return matches[0] if matches else None

def engineer_features(df):
    df = df.rename(columns={'close_price': 'Close'})
    # log Returns 
    df['Log_Ret'] = np.log(df['Close'] / df['Close'].shift(1))
    # Momentum & Trend
    df['RSI'] = ta.rsi(df['Close'], length=14)
    sma20 = ta.sma(df['Close'], length=20)
    sma50 = ta.sma(df['Close'], length=50)
    df['SMA_Trend'] = (sma20 - sma50) / (sma50 + 1e-9)
    # Volatility
    bbands = ta.bbands(df['Close'], length=20)
    l_col, u_col = safe_get_col(bbands, 'BBL'), safe_get_col(bbands, 'BBU')
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
    df = pd.read_sql(f"SELECT close_price FROM stock_price WHERE ticker='{ticker}' ORDER BY date ASC", conn)
    conn.close()

    if len(df) < 110: return None

    features, prices = engineer_features(df)
    
    # We use the last 5 ACTUAL days to measure accuracy
    actual_last_5 = prices.iloc[-FORECAST:].values
    
    scaler = StandardScaler()
    scaled = scaler.fit_transform(features)
    
    # Load or Train
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

    current_window = scaled[-LOOKBACK-FORECAST:-FORECAST].reshape(1, LOOKBACK, 4)
    last_price = prices.iloc[-FORECAST-1]
    
    preds = []
    for _ in range(FORECAST):
        res = model.predict(current_window, verbose=0)[0,0]
        
        res = np.clip(res, -0.03, 0.03)
        
        new_p = last_price * np.exp(res)
        preds.append(new_p)
        
        new_row = current_window[0, -1, :].copy()
        new_row[0] = res
        current_window = np.append(current_window[:, 1:, :], new_row.reshape(1,1,4), axis=1)
        last_price = new_p

    # MAPE = Mean Absolute Percentage Error
    mape = mean_absolute_percentage_error(actual_last_5, preds)
    accuracy_percent = max(0, 100 - (mape * 100))

    return {
        "forecast": [round(float(p), 2) for p in preds],
        "accuracy": f"{round(accuracy_percent, 2)}%"
    }

@app.route('/predict-all')
def api():
    conn = get_db_conn()
    cursor = conn.cursor()
    cursor.execute("SELECT ticker FROM subscription")
    tickers = [row[0] for row in cursor.fetchall()]
    conn.close()

    results = {}
    for t in tickers:
        print(f"[*] Analyzing {t}...")
        results[t] = get_prediction_with_accuracy(t)

    return jsonify({"status": "success", "results": results})

if __name__ == '__main__':
    app.run(port=5000, debug=True)