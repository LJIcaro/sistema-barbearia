// /js/meus-agendamentos.js

document.addEventListener("DOMContentLoaded", () => {
    if (!checkAuth() || !isCliente()) return;
    carregarMeusAgendamentos();
});

async function carregarMeusAgendamentos() {
    const container = document.getElementById("meusAgendamentosList");

    try {
        const resp = await apiFetch("/api/agendamentos/meus");
        if (!resp || !resp.ok) {
            container.innerHTML = "<p class='error-msg'>Erro ao carregar agendamentos.</p>";
            return;
        }
        const agendamentos = await resp.json();
        if (!agendamentos.length) {
            container.innerHTML = "<p class='info-msg'>Você ainda não possui agendamentos.</p>";
            return;
        }

        let html = "<table><thead><tr>" +
            "<th>Data/Hora</th><th>Barbeiro</th><th>Serviço</th><th>Status</th>" +
            "</tr></thead><tbody>";

        agendamentos.forEach(a => {
            const status = getStatusFormatado(a.status);
            const cor = getStatusCor(a.status);
            html += `<tr>
                        <td>${formatarDataHora(a.dataHora)}</td>
                        <td>${a.barbeiroNome || ""}</td>
                        <td>${a.servicoNome || ""}</td>
                        <td><span style="color:${cor};font-weight:bold;">${status}</span></td>
                    </tr>`;
        });

        html += "</tbody></table>";
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p class='error-msg'>Erro ao carregar agendamentos.</p>";
    }
}