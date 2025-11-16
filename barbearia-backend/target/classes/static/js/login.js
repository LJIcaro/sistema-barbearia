const API_BASE_URL = '/api';

function showMessage(elementId, message, type = 'error') {
  const el = document.getElementById(elementId);
  if (!el) return;
  el.textContent = message;
  el.style.color = type === 'error' ? 'red' : 'green';
  el.style.display = message ? 'block' : 'none';
}

// LOGIN
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    showMessage('loginMessage', '', 'error');

    const cpf = document.getElementById('cpfLogin').value.trim();
    const senha = document.getElementById('senhaLogin').value.trim();

    if (!cpf || !senha) {
      showMessage('loginMessage', 'CPF e senha são obrigatórios.');
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ cpf, senha })
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        const msg = errorData?.message || 'CPF ou senha inválidos.';
        showMessage('loginMessage', msg);
        return;
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('tipoUsuario', data.tipoUsuario);
      localStorage.setItem('nomeUsuario', data.nomeCompleto);

      window.location.href = '/dashboard';
    } catch (err) {
      console.error(err);
      showMessage('loginMessage', 'Erro ao tentar fazer login. Tente novamente.');
    }
  });
}