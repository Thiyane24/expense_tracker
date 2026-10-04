-- 1. Tabela de Contas (Accounts)
CREATE TABLE accounts (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(100) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          balance DECIMAL(12, 2) NOT NULL DEFAULT 0.00
);

-- 2. Tabela de Categorias (Categories)
CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            name VARCHAR(100) NOT NULL UNIQUE,
                            monthly_limit DECIMAL(12, 2) NOT NULL DEFAULT 0.00
);

-- 3. Tabela de Transações / Gastos (Transactions)
CREATE TABLE transactions (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              account_id UUID NOT NULL,
                              category_id UUID NOT NULL,
                              amount DECIMAL(12, 2) NOT NULL,
                              transaction_date DATE NOT NULL,
                              description VARCHAR(255) NOT NULL,

    -- Configuração das Chaves Estrangeiras (Foreign Keys)
                              CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                              CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- 4. Índices para Acelerar Consultas de Orçamento e Histórico
-- Acelera o cálculo da soma de gastos por categoria dentro do mês
CREATE INDEX idx_transactions_category_date ON transactions(category_id, transaction_date);

-- Acelera a listagem do extrato ordenado por data
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);