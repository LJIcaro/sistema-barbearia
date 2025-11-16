// /js/dashboard.js

document.addEventListener("DOMContentLoaded", async () => {
    if (!checkAuth()) return;
    if (!isBarbeiro()) return;

    const agendamentosDiv = document.getElementById("agendamentosHoje");
    const infoEl = document.getElementById("dashboardInfo");

    try {
        const resp = await apiFetch("/api/agendamentos/barbeiro");
        if (!resp || !resp.ok) {
            agendamentosDiv.innerHTML = "<p class='error-msg'>Erro ao carregar agendamentos.</p>";
            return;
        }

        const agendamentos = await resp.json();

        // filtra agendamentos de hoje
        const hoje = new Date();
        const hojeStr = hoje.toISOString().split("T")[0];

        const agHoje = agendamentos.filter(a => {
            const data = new Date(a.dataHora);
            return data.toISOString().startsWith(hojeStr);
        });

        infoEl.textContent = `Você tem ${agHoje.length} agendamento(s) para hoje.`;

        if (agHoje.length === 0) {
            agendamentosDiv.innerHTML = "<p class='info-msg'>Nenhum agendamento para hoje.</p>";
            return;
        }

        let html = "<table><thead><tr>" +
            "<th>Horário</th><th>Cliente</th><th>Serviço</th><th>Status</th>" +
            "</tr></thead><tbody>";

        agHoje.forEach(a => {
            const dataHora = formatarDataHora(a.dataHora);
            const status = getStatusFormatado(a.status);
            const cor = getStatusCor(a.status);
            html += `<tr>
                        <td>${dataHora}</td>
                        <td>${a.clienteNome || ""}</td>
                        <td>${a.servicoNome || ""}</td>
                        <td><span style="color:${cor};font-weight:bold;">${status}</span></td>
                    </tr>`;
        });

        html += "</tbody></table>";
        agendamentosDiv.innerHTML = html;

    } catch (e) {
        console.error(e);
        agendamentosDiv.innerHTML = "<p class='error-msg'>Erro ao carregar agendamentos.</p>";
    }
});