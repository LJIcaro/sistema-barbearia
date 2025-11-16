// main.js - Funções globais e autenticação

// Gerenciamento de token
function getToken() {
    return localStorage.getItem("token");
}

function setToken(token) {
    localStorage.setItem("token", token);
}

function clearToken() {
    localStorage.removeItem("token");
    localStorage.removeItem("userType");
    localStorage.removeItem("userId");
    localStorage.removeItem("userName");
}

function getUserType() {
    return localStorage.getItem("userType");
}

function setUserData(data) {
    localStorage.setItem("userType", data.tipoUsuario);
    localStorage.setItem("userId", data.userId);
    localStorage.setItem("userName", data.nomeCompleto);
}

function getUserData() {
    return {
        userType: localStorage.getItem("userType"),
        userId: localStorage.getItem("userId"),
        userName: localStorage.getItem("userName")
    };
}

// Fetch com autenticação automática
async function apiFetch(url, options = {}) {
    const token = getToken();
    const headers = options.headers || {};

    if (token) {
        headers["Authorization"] = "Bearer " + token;
    }
    headers["Content-Type"] = headers["Content-Type"] || "application/json";

    const finalOptions = {
        ...options,
        headers
    };

    try {
        const resp = await fetch(url, finalOptions);
        
        // Se não autorizado, redireciona para login
        if (resp.status === 401 || resp.status === 403) {
            clearToken();
            window.location.href = "/";
            return null;
        }

        return resp;
    } catch (error) {
        console.error("Erro na requisição:", error);
        throw error;
    }
}

// Logout
function logout() {
    clearToken();
    window.location.href = "/";
}

// Verificar autenticação
function checkAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = "/";
        return false;
    }
    return true;
}

// Verificar se é barbeiro
function isBarbeiro() {
    return getUserType() === "BARBEIRO";
}

// Verificar se é cliente
function isCliente() {
    return getUserType() === "CLIENTE";
}

// Formatar data para exibição
function formatarData(dataStr) {
    const data = new Date(dataStr);
    return data.toLocaleDateString('pt-BR');
}

// Formatar data e hora para exibição
function formatarDataHora(dataStr) {
    const data = new Date(dataStr);
    return data.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Formatar hora
function formatarHora(dataStr) {
    const data = new Date(dataStr);
    return data.toLocaleTimeString('pt-BR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Formatar moeda
function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

// Formatar CPF
function formatarCPF(cpf) {
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "\$1.\$2.\$3-\$4");
}

// Remover formatação do CPF
function limparCPF(cpf) {
    return cpf.replace(/\D/g, '');
}

// Validar CPF
function validarCPF(cpf) {
    cpf = limparCPF(cpf);
    
    if (cpf.length !== 11) return false;
    
    // Verifica se todos os dígitos são iguais
    if (/^(\d)\1{10}$/.test(cpf)) return false;
    
    // Validação do primeiro dígito verificador
    let soma = 0;
    for (let i = 0; i < 9; i++) {
        soma += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let resto = 11 - (soma % 11);
    let digito1 = resto >= 10 ? 0 : resto;
    
    if (digito1 !== parseInt(cpf.charAt(9))) return false;
    
    // Validação do segundo dígito verificador
    soma = 0;
    for (let i = 0; i < 10; i++) {
        soma += parseInt(cpf.charAt(i)) * (11 - i);
    }
    resto = 11 - (soma % 11);
    let digito2 = resto >= 10 ? 0 : resto;
    
    return digito2 === parseInt(cpf.charAt(10));
}

// Mostrar mensagem de erro
function mostrarErro(elementId, mensagem) {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = mensagem;
        el.className = "error-msg";
        el.style.display = "block";
    }
}

// Mostrar mensagem de sucesso
function mostrarSucesso(elementId, mensagem) {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = mensagem;
        el.className = "success-msg";
        el.style.display = "block";
    }
}

// Esconder mensagem
function esconderMensagem(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.style.display = "none";
    }
}

// Obter nome do dia da semana
function getNomeDiaSemana(numero) {
    const dias = ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'];
    return dias[numero];
}

// Obter status formatado
function getStatusFormatado(status) {
    const statusMap = {
        'PENDENTE': 'Pendente',
        'CONFIRMADO': 'Confirmado',
        'CANCELADO': 'Cancelado',
        'CONCLUIDO': 'Concluído'
    };
    return statusMap[status] || status;
}

// Obter cor do status
function getStatusCor(status) {
    const corMap = {
        'PENDENTE': '#ed8936',
        'CONFIRMADO': '#48bb78',
        'CANCELADO': '#e53e3e',
        'CONCLUIDO': '#667eea'
    };
    return corMap[status] || '#718096';
}