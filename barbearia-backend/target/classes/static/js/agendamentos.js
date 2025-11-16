// /js/agendamentos.js

let horarioSelecionado = null;

document.addEventListener("DOMContentLoaded", () => {
    if (!checkAuth()) return;

    const isClienteUser = isCliente();
    const isBarbeiroUser = isBarbeiro();

    if (isClienteUser) {
        inicializarClienteAgendamentos();
    }

    if (isBarbeiroUser) {
        carregarAgendamentosBarbeiro();
    }
});

// CLIENTE - fluxo de agendamento
async function inicializarClienteAgendamentos() {
    await carregarBarbeiros();
    document.getElementById("barbeiroSelect").addEventListener("change", carregarServicosBarbeiro);
    document.getElementById("buscarHorariosBtn").addEventListener("click", buscarHorarios);
    document.getElementById("confirmarAgendamentoBtn").addEventListener("click", confirmarAgendamento);
    await carregarServicosBarbeiro();
}

async function carregarBarbeiros() {
    const select = document.getElementById("barbeiroSelect");
    select.innerHTML = "<option value=\"\">Carregando barbeiros...</option>";

    try {
        const resp = await apiFetch("/api/barbeiros/listar");
        if (!resp || !resp.ok) {
            select.innerHTML = "<option value=\"\">Erro ao carregar barbeiros</option>";
            return;
        }
        const barbeiros = await resp.json();
        if (!barbeiros.length) {
            select.innerHTML = "<option value=\"\">Nenhum barbeiro disponível</option>";
            return;
        }
        let options = "";
        barbeiros.forEach(b => {
            options += `<option value="${b.id}">${b.nomeCompleto}</option>`;
        });
        select.innerHTML = options;
    } catch (e) {
        console.error(e);
        select.innerHTML = "<option value=\"\">Erro ao carregar barbeiros</option>";
    }
}

async function carregarServicosBarbeiro() {
    const barbeiroId = document.getElementById("barbeiroSelect").value;
    const select = document.getElementById("servicoSelect");
    select.innerHTML = "<option value=\"\">Carregando serviços...</option>";

    if (!barbeiroId) {
        select.innerHTML = "<option value=\"\">Selecione um barbeiro</option>";
        return;
    }

    try {
        const resp = await apiFetch(`/api/servicos/barbeiro/${barbeiroId}`);
        if (!resp || !resp.ok) {
            select.innerHTML = "<option value=\"\">Erro ao carregar serviços</option>";
            return;
        }
        const servicos = await resp.json();
        if (!servicos.length) {
            select.innerHTML = "<option value=\"\">Nenhum serviço encontrado para este barbeiro</option>";
            return;
        }
        let options = "";
        servicos.forEach(s => {
            options += `<option value="${s.id}">${s.nome} - ${formatarMoeda(s.preco)}</option>`;
        });
        select.innerHTML = options;
    } catch (e) {
        console.error(e);
        select.innerHTML = "<option value=\"\">Erro ao carregar serviços</option>";
    }
}

async function buscarHorarios() {
    const barbeiroId = document.getElementById("barbeiroSelect").value;
    const data = document.getElementById("dataAgendamento").value;
    const container = document.getElementById("horariosContainer");
    const msgEl = document.getElementById("agendamentosMsg");

    esconderMensagem("agendamentosMsg");
    horarioSelecionado = null;
    document.getElementById("confirmarAgendamentoBtn").disabled = true;

    if (!barbeiroId || !data) {
        mostrarErro("agendamentosMsg", "Selecione barbeiro e data.");
        return;
    }

    container.innerHTML = "<div class='loading'>Carregando horários disponíveis...</div>";

    try {
        const resp = await apiFetch(`/api/agendamentos/horarios-disponiveis?barbeiroId=${barbeiroId}&data=${data}`);
        if (!resp || !resp.ok) {
            container.innerHTML = "<p class='error-msg'>Erro ao carregar horários.</p>";
            return;
        }
        const horarios = await resp.json();
        if (!horarios.length) {
            container.innerHTML = "<p class='info-msg'>Nenhum horário disponível nesta data.</p>";
            return;
        }

        let html = "<div class='horarios-grid'>";
        horarios.forEach(h => {
            const horaLabel = formatarHora(h.dataHora);
            const classe = h.disponivel ? "horario-btn" : "horario-btn indisponivel";
            html += `<button class="${classe}" 
                         data-dh="${h.dataHora}"
                         ${h.disponivel ? "onclick='selecionarHorario(this)'" : "disabled"}>
                        ${horaLabel}
                     </button>`;
        });
        html += "</div>";
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p class='error-msg'>Erro ao carregar horários.</p>";
    }
}

function selecionarHorario(btn) {
    document.querySelectorAll(".horario-btn").forEach(b => b.classList.remove("selected"));
    btn.classList.add("selected");
    horarioSelecionado = btn.getAttribute("data-dh");
    document.getElementById("confirmarAgendamentoBtn").disabled = !horarioSelecionado;
}

async function confirmarAgendamento() {
    const barbeiroId = parseInt(document.getElementById("barbeiroSelect").value, 10);
    const servicoId = parseInt(document.getElementById("servicoSelect").value, 10);
    const observacoes = document.getElementById("observacoesAgendamento").value;
    const msgEl = document.getElementById("agendamentosMsg");

    esconderMensagem("agendamentosMsg");

    if (!barbeiroId || !servicoId || !horarioSelecionado) {
        mostrarErro("agendamentosMsg", "Selecione barbeiro, serviço e horário.");
        return;
    }

    const dto = {
        barbeiroId: barbeiroId,
        servicoId: servicoId,
        dataHora: horarioSelecionado,
        observacoes: observacoes
    };

    try {
        const resp = await apiFetch("/api/agendamentos", {
            method: "POST",
            body: JSON.stringify(dto)
        });

        if (!resp || !resp.ok) {
            const txt = await resp.text();
            mostrarErro("agendamentosMsg", txt || "Erro ao criar agendamento.");
            return;
        }

        mostrarSucesso("agendamentosMsg", "Agendamento criado com sucesso!");
        document.getElementById("confirmarAgendamentoBtn").disabled = true;
    } catch (e) {
        console.error(e);
        mostrarErro("agendamentosMsg", "Erro ao criar agendamento.");
    }
}

// BARBEIRO - listar agendamentos e mudar status
async function carregarAgendamentosBarbeiro() {
    const container = document.getElementById("barbeiroAgendamentosList");

    try {
        const resp = await apiFetch("/api/agendamentos/barbeiro");
        if (!resp || !resp.ok) {
            container.innerHTML = "<p class='error-msg'>Erro ao carregar agendamentos.</p>";
            return;
        }
        const agendamentos = await resp.json();
        if (!agendamentos.length) {
            container.innerHTML = "<p class='info-msg'>Nenhum agendamento encontrado.</p>";
            return;
        }

        let html = "<table><thead><tr>" +
            "<th>Data/Hora</th><th>Cliente</th><th>Serviço</th><th>Status</th><th>Ações</th>" +
            "</tr></thead><tbody>";

        agendamentos.forEach(a => {
            const status = getStatusFormatado(a.status);
            const cor = getStatusCor(a.status);
            html += `<tr>
                        <td>${formatarDataHora(a.dataHora)}</td>
                        <td>${a.clienteNome || ""}</td>
                        <td>${a.servicoNome || ""}</td>
                        <td><span style="color:${cor};font-weight:bold;">${status}</span></td>
                        <td>
                            <button class="btn-secondary btn-small" onclick="alterarStatusAgendamento(${a.id}, 'CONFIRMADO')">Confirmar</button>
                            <button class="btn-warning btn-small" onclick="alterarStatusAgendamento(${a.id}, 'CONCLUIDO')">Concluir</button>
                            <button class="btn-danger btn-small" onclick="alterarStatusAgendamento(${a.id}, 'CANCELADO')">Cancelar</button>
                        </td>
                    </tr>`;
        });

        html += "</tbody></table>";
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p class='error-msg'>Erro ao carregar agendamentos.</p>";
    }
}

async function alterarStatusAgendamento(id, novoStatus) {
    if (!confirm(`Deseja alterar o status para ${getStatusFormatado(novoStatus)}?`)) return;

    try {
        const resp = await apiFetch(`/api/agendamentos/${id}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status: novoStatus })
        });

        if (!resp || !resp.ok) {
            mostrarErro("barbeiroAgendamentosMsg", "Erro ao alterar status.");
            return;
        }

        mostrarSucesso("barbeiroAgendamentosMsg", "Status atualizado com sucesso.");
        carregarAgendamentosBarbeiro();
    } catch (e) {
        console.error(e);
        mostrarErro("barbeiroAgendamentosMsg", "Erro ao alterar status.");
    }
}