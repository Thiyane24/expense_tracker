/**
 * ExpenseTracker Vanilla JS - PRO Version
 * Architecture: State-driven DOM manipulation with JWT Auth and UX enhancements
 */

const CONFIG = {
    API_BASE_URL: 'https://expensetracker1-e54cc47539ed.herokuapp.com/api',
    CURRENCY: 'MYR'
};

// Application State
const state = {
    user: null,
    token: localStorage.getItem('token') || null,
    budgetLimit: 0,
    spentAmount: 0,
    transactions: [],
    filteredTransactions: [],
    isLoginMode: true
};

// DOM Elements
const elements = {
    loader: document.getElementById('loader'),
    errorBanner: document.getElementById('error-banner'),
    errorMessage: document.getElementById('error-message'),
    balance: document.getElementById('balance'),
    budgetLimit: document.getElementById('budget-limit'),
    budgetPercent: document.getElementById('budget-percent'),
    progressBar: document.getElementById('progress-bar'),
    transactionsList: document.getElementById('transactions-list'),
    emptyState: document.getElementById('empty-state'),
    expenseForm: document.getElementById('expense-form'),
    userStatus: document.getElementById('user-status'),
    submitBtn: document.getElementById('submit-btn'),
    searchTx: document.getElementById('search-tx'),

    // Auth elements
    authScreen: document.getElementById('auth-screen'),
    appScreen: document.getElementById('app-screen'),
    authForm: document.getElementById('auth-form'),
    authTitle: document.getElementById('auth-title'),
    authSubtitle: document.getElementById('auth-subtitle'),
    authSubmitBtn: document.getElementById('auth-submit-btn'),
    toggleAuthBtn: document.getElementById('toggle-auth'),
    nameGroup: document.getElementById('name-group'),
    logoutBtn: document.getElementById('logout-btn')
};

/**
 * Utility: Error Handling
 */
function notifyError(msg) {
    console.error(`[System Error]: ${msg}`);
    elements.errorMessage.innerText = msg;
    elements.errorBanner.classList.remove('hidden');
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
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (state.token) {
        headers['Authorization'] = `Bearer ${state.token}`;
    }

    try {
        const response = await fetch(url, { ...options, headers });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            if (response.status === 401) {
                handleLogout();
                throw new Error('Sessão expirada. Faça login novamente.');
            }
            throw new Error(errorData.error || `Erro do servidor: ${response.status}`);
        }
        return await response.json();
    } catch (err) {
        const isNetworkError = err instanceof TypeError;
        const message = isNetworkError
            ? 'Erro de conexão com o servidor.'
            : err.message;
        notifyError(message);
        throw err;
    }
}

/**
 * Auth Logic
 */
function toggleAuthMode() {
    state.isLoginMode = !state.isLoginMode;
    elements.authTitle.innerText = state.isLoginMode ? 'Entrar' : 'Criar Conta';
    elements.authSubtitle.innerText = state.isLoginMode ? 'Gestão financeira minimalista' : 'Comece a gerir as suas despesas';
    elements.authSubmitBtn.innerText = state.isLoginMode ? 'Entrar' : 'Registar';
    elements.nameGroup.classList.toggle('hidden');
    elements.toggleAuthBtn.innerText = state.isLoginMode ? 'Não tem conta? Crie uma agora' : 'Já tem conta? Faça login';
}

async function handleAuth(e) {
    e.preventDefault();
    const email = document.getElementById('auth-email').value;
    const password = document.getElementById('auth-password').value;
    const name = document.getElementById('auth-name').value;
    const endpoint = state.isLoginMode ? '/auth/login' : '/auth/signup';
    const payload = state.isLoginMode ? { email, password } : { email, password, name };

    try {
        elements.authSubmitBtn.disabled = true;
        elements.authSubmitBtn.innerText = 'A processar...';

        const data = await apiRequest(endpoint, {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        if (state.isLoginMode) {
            state.token = data.token;
            localStorage.setItem('token', data.token);
            state.user = { id: data.id, email: data.email };
            showApp();
        } else {
            alert('Conta criada com sucesso! Por favor, faça login.');
            toggleAuthMode();
        }
    } catch (err) {
        // handled by apiRequest
    } finally {
        elements.authSubmitBtn.disabled = false;
        elements.authSubmitBtn.innerText = state.isLoginMode ? 'Entrar' : 'Registar';
    }
}

function handleLogout() {
    localStorage.removeItem('token');
    state.token = null;
    state.user = null;
    elements.appScreen.classList.add('hidden');
    elements.authScreen.classList.remove('hidden');
}

/**
 * Core App Logic
 */
function showApp() {
    elements.authScreen.classList.add('hidden');
    elements.appScreen.classList.remove('hidden');
    initApp();
}

async function initApp() {
    try {
        const budgetData = await apiRequest('/auth/budget');
        state.budgetLimit = budgetData.newBudget || budgetData.limit || 0;
        state.user = budgetData.user || { name: 'Usuário' };

        const transactions = await apiRequest('/transactions');
        state.transactions = transactions;
        state.filteredTransactions = [...state.transactions];

        updateUI();
    } catch (err) {
        // handled
    } finally {
        elements.loader.classList.add('hidden');
    }
}

function updateUI() {
    elements.userStatus.innerText = `Olá, ${state.user.name}`;
    state.spentAmount = state.transactions.reduce((sum, tx) => sum + tx.amount, 0);
    const available = state.budgetLimit - state.spentAmount;

    elements.balance.innerText = available.toLocaleString('en-US', { minimumFractionDigits: 2 });
    elements.budgetLimit.innerText = state.budgetLimit.toLocaleString('en-US', { minimumFractionDigits: 2 });

    const percent = state.budgetLimit > 0 ? (state.spentAmount / state.budgetLimit) * 100 : 0;
    elements.progressBar.style.width = `${Math.min(percent, 100)}%`;
    elements.budgetPercent.innerText = `${Math.round(percent)}%`;

    renderTransactions();
}

function renderTransactions() {
    const list = state.filteredTransactions;
    if (list.length === 0) {
        elements.emptyState.classList.remove('hidden');
        elements.transactionsList.innerHTML = '';
        return;
    }

    elements.emptyState.classList.add('hidden');
    elements.transactionsList.innerHTML = list.map((tx, index) => `
        <div class="tx-item" style="animation-delay: ${index * 0.05}s">
            <div class="tx-info">
                <span class="tx-desc">${tx.description}</span>
                <span class="tx-meta">${tx.category} • ${tx.date || 'Hoje'}</span>
            </div>
            <div class="tx-right">
                <span class="tx-amount">-${tx.amount.toLocaleString('en-US', { minimumFractionDigits: 2 })} ${CONFIG.CURRENCY}</span>
                <button onclick="deleteTransaction('${tx.id}')" class="delete-tx-btn" title="Remover">×</button>
            </div>
        </div>
    `).join('');
}

async function deleteTransaction(id) {
    if (!confirm('Deseja realmente remover este gasto?')) return;

    try {
        await apiRequest(`/transactions/${id}`, { method: 'DELETE' });
        state.transactions = state.transactions.filter(t => t.id !== id);
        state.filteredTransactions = [...state.transactions];
        updateUI();
    } catch (err) {
        // handled
    }
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
        state.filteredTransactions = [...state.transactions];
        updateUI();
        elements.expenseForm.reset();
    } catch (err) {
        // handled
    } finally {
        elements.submitBtn.disabled = false;
        elements.submitBtn.innerText = 'Adicionar Gasto';
    }
};

elements.searchTx.oninput = (e) => {
    const term = e.target.value.toLowerCase();
    state.filteredTransactions = state.transactions.filter(tx =>
        tx.description.toLowerCase().includes(term) ||
        tx.category.toLowerCase().includes(term)
    );
    renderTransactions();
};

elements.toggleAuthBtn.onclick = toggleAuthMode;
elements.authForm.onsubmit = handleAuth;
elements.logoutBtn.onclick = handleLogout;

window.deleteTransaction = deleteTransaction;

window.addEventListener('DOMContentLoaded', () => {
    if (state.token) {
        showApp();
    } else {
        elements.loader.classList.add('hidden');
        elements.authScreen.classList.remove('hidden');
    }
});
