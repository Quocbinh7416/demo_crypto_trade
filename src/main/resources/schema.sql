DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  balance_amount INTEGER NOT NULL
);

DROP TABLE IF EXISTS crypto_coin;

CREATE TABLE crypto_coin (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  name VARCHAR(250) NOT NULL,
  symbol VARCHAR(250),
  description VARCHAR(250)
);

DROP TABLE IF EXISTS users_balance_data;

CREATE TABLE users_balance_data (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  user_id INT NOT NULL,
  coin_id INT NOT NULL,
  amount NUMERIC(20,8) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (coin_id) REFERENCES crypto_coin(id)
);
