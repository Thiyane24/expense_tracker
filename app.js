/**
 * ExpenseTracker Vanilla JS
 * Architecture: State-driven DOM manipulation
 */

const CONFIG = {
    API_BASE_URL: 'https://expensetracker1-e54cc47539ed.herokuapp.com/api',
    CURRENCY: 'MYR'
};

// Application State
const state = {
    user: null,
    budgetLimit: 0,
    spentAmount: 0,
    transactions: []
};

// DOM Elements
const elements = {
    loader: document.getElementById('loader'),
    errorBanner: document.getElementById('error-banner'),
    errorMessage: document.getElementById('error-message'),
    balance: document.getElementById('balance'),
    budgetLimit: document.getElementById('budget-limit'),
    progressBar: document.getElementById('progress-bar'),
    transactionsList: document.getElementById('transactions-list'),
    emptyState: document.getElementById('empty-state'),
    expenseForm: document.getElementById('expense-form'),
    userStatus: document.getElementById('user-status'),
    submitBtn: document.getElementById('submit-btn')
};

/**
 * Utility: Error Handling
 * Ensures loading states are removed and user is notified
 */
function notifyError(msg) {
    console.error(`[System Error]: ${msg}`);
    elements.errorMessage.innerText = msg;
    elements.errorBanner.classList.remove('hidden');

    // Remove loader immediately if it's still there
    elements.loader.classList.add('hidden');

    setTimeout(closeError, 5000);
}

function closeError() {
    elements.errorBanner.classList.add('hidden');
}

/**
 * API Communication
 */
async function apiRequest(endpoint, options = {}) {
    const url = `${CONFIG.API_BASE_URL}${endpoint}`;

    // Default headers
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    try {
        const response = await fetch(url, { ...options, headers });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.error || `Erro do servidor: ${response.status}`);
        }

        return await response.json();
    } catch (err) {
        // CORS failures or Network outages land here
        const isNetworkError = err instanceof TypeError;
        const message = isNetworkError
            ? 'Não foi possível ligar ao servidor de dados (Verifique se o backend está online)'
            : err.message;

        notifyError(message);
        throw err;
    }
}

/**
 * Core Logic
 */
async function initApp() {
    try {
        // 1. Fetch User/Budget Summary
        // Assuming /api/v1/auth/me or similar exists.
        // Using /api/v1/budget as a proxy for the purpose of this demo setup.
        const budgetData = await apiRequest('/budget');
        state.budgetLimit = budgetData.limit || 0;
        state.user = budgetData.user || { name: 'Usuário' };

        // 2. Fetch Transactions
        const transactions = await apiRequest('/transactions');
        state.transactions = transactions;

        updateUI();
    } catch (err) {
        // Error handled by apiRequest, but we ensure the loader is gone
        elements.loader.classList.add('hidden');
    } finally {
        elements.loader.classList.add('hidden');
    }
}

function updateUI() {
    // Update User Status
    elements.userStatus.innerText = `Olá, ${state.user.name}`;

    // Calculate Totals
    state.spentAmount = state.transactions.reduce((sum, tx) => sum + tx.amount, 0);
    const available = state.budgetLimit - state.spentAmount;

    // Update Budget Card
    elements.balance.innerText = available.toLocaleString('en-US', { minimumFractionDigits: 2 });
    elements.budgetLimit.innerText = state.budgetLimit.toLocaleString('en-US', { minimumFractionDigits: 2 });

    const percent = state.budgetLimit > 0 ? (state.spentAmount / state.budgetLimit) * 100 : 0;
    elements.progressBar.style.width = `${Math.min(percent, 100)}%`;

    // Update Transactions List
    renderTransactions();
}

function renderTransactions() {
    if (state.transactions.length === 0) {
        elements.emptyState.classList.remove('hidden');
        elements.transactionsList.innerHTML = '';
        return;
    }

    elements.emptyState.classList.add('hidden');
    elements.transactionsList.innerHTML = state.transactions.map(tx => `
        <div class="tx-item">
            <div class="tx-info">
                <span class="tx-desc">${tx.description}</span>
                <span class="tx-meta">${tx.category} • ${tx.date || 'Hoje'}</span>
            </div>
            <div class="tx-amount">-${tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2 })} ${CONFIG.CURRENCY}</div>
        </div>
    `).join('');
}

/**
 * Event Handlers
 */
elements.expenseForm.onsubmit = async (e) => {
    e.preventDefault();

    const payload = {
        amount: parseFloat(document.getElementById('amount').value),
        description: document.getElementById('description').value,
        category: document.getElementById('category').value
    };

    try {
        elements.submitBtn.disabled = true;
        elements.submitBtn.innerText = 'Processando...';

        const newTx = await apiRequest('/transactions', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        state.transactions.unshift(newTx);
        updateUI();
        elements.expenseForm.reset();
    } catch (err) {
        // Error handled by apiRequest
    } finally {
        elements.submitBtn.disabled = false;
        elements.submitBtn.innerText = 'Adicionar Gasto';
    }
};

// Entry Point
window.addEventListener('DOMContentLoaded', initApp);
