DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  balance_amount NUMERIC(20,8) NOT NULL
);

DROP TABLE IF EXISTS crypto_coin;

CREATE TABLE crypto_coin (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  symbol VARCHAR(250),
  description VARCHAR(250)
);

DROP TABLE IF EXISTS users_balance_data;

CREATE TABLE users_balance_data (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  coin_id INT NOT NULL,
  amount NUMERIC(20,8) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (coin_id) REFERENCES crypto_coin(id)
);

DROP TABLE IF EXISTS trade_market_data;

CREATE TABLE trade_market_data (
  id INT AUTO_INCREMENT PRIMARY KEY,
  coin_id INT NOT NULL UNIQUE,
  bid_trade_market VARCHAR(250) NOT NULL,
  bid_price NUMERIC(20,8) NOT NULL,
  bid_quantity NUMERIC(20,8) NOT NULL,
  ask_trade_market VARCHAR(250) NOT NULL,
  ask_price NUMERIC(20,8) NOT NULL,
  ask_quantity NUMERIC(20,8) NOT NULL,
  timestamp TIMESTAMP,
  FOREIGN KEY (coin_id) REFERENCES crypto_coin(id)
);

DROP TABLE IF EXISTS users_trade_history;

CREATE TABLE users_trade_history (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  coin_id INT NOT NULL,
  amount NUMERIC(20,8) NOT NULL,
  is_buy BOOLEAN NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  trade_market VARCHAR(250) NOT NULL,
  trade_market_price NUMERIC(20,8) NOT NULL,
  trade_market_quantity NUMERIC(20,8) NOT NULL,
  trade_market_timestamp TIMESTAMP NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (coin_id) REFERENCES crypto_coin(id)
);
