CREATE TABLE bank.account
(login CHAR(20) NOT NULL PRIMARY KEY,
password CHAR(20) NOT NULL,
first_name CHAR(20) NOT NULL,
last_name CHAR(20) NOT NULL,
city CHAR(15) NOT NULL,
balance DECIMAL(9,2) NOT NULL DEFAULT 0.00); 

CREATE TABLE bank.transaction
(login CHAR(20) NOT NULL,
description VARCHAR(50) NOT NULL,
debit DECIMAL(9,2),
credit DECIMAL(9,2),
trans_date DATE NOT NULL); 

CREATE UNIQUE INDEX AC1
ON bank.account (LOGIN);

ALTER TABLE bank.transaction
	ADD FOREIGN KEY FK_TRANSACTION_ACCOUNT (login) REFERENCES bank.account(login)
    ON DELETE CASCADE; 

CREATE TRIGGER bank.BANK_I
AFTER INSERT 
ON bank.transaction
FOR EACH ROW
UPDATE account
SET balance = balance + NEW.debit - NEW.credit
WHERE login = NEW.login;