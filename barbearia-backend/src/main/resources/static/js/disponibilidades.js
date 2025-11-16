// /js/disponibilidades.js

document.addEventListener("DOMContentLoaded", () => {
    if (!checkAuth() || !isBarbeiro()) return;

    carregarDisponibilidades();

    document.getElementById("disponibilidadeForm").addEventListener("submit", salvarDisponibilidade);
    document.getElementById("cancelarDispEdicao").addEventListener("click", limparFormularioDisp);
});

async function carregarDisponibilidades() {
    const container = document.getElementById("disponibilidadesList");
    container.innerHTML = "Carregando disponibilidades...";

    try {
        const resp = await apiFetch("/api/disponibilidades/minhas");
        if (!resp || !resp.ok) {
            container.innerHTML = "<p class='error-msg'>Erro ao carregar disponibilidades.</p>";
            return;
        }

        const disponibilidades = await resp.json();
        if (!disponibilidades.length) {
            container.innerHTML = "<p class='info-msg'>Você ainda não cadastrou disponibilidades.</p>";
            return;
        }

        let html = "<table><thead><tr>" +
            "<th>Dia da Semana</th><th>Início</th><th>Fim</th><th>Ativo</th><th>Ações</th>" +
            "</tr></thead><tbody>";

        disponibilidades.forEach(d => {
            html += `<tr>
                        <td>${getNomeDiaSemana(d.diaSemana)}</td>
                        <td>${d.horaInicio}</td>
                        <td>${d.horaFim}</td>
                        <td>${d.ativo ? "Sim" : "Não"}</td>
                        <td>
                            <button class="btn-secondary btn-small" onclick="editarDisponibilidade(${d.id})">Editar</button>
                            <button class="btn-danger btn-small" onclick="deletarDisponibilidade(${d.id})">Excluir</button>
                        </td>
                    </tr>`;
        });

        html += "</tbody></table>";
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p class='error-msg'>Erro ao carregar disponibilidades.</p>";
    }
}

async function salvarDisponibilidade(e) {
    e.preventDefault();

    const id = document.getElementById("disponibilidadeId").value;
    const diaSemana = parseInt(document.getElementById("diaSemana").value, 10);
    const horaInicio = document.getElementById("horaInicio").value;
    const horaFim = document.getElementById("horaFim").value;
    const ativo = document.getElementById("ativoDisp").value === "true";
    const msgEl = document.getElementById("disponibilidadesMsg");

    esconderMensagem("disponibilidadesMsg");

    const dto = {
        diaSemana: diaSemana,
        horaInicio: horaInicio,
        horaFim: horaFim,
        ativo: ativo
    };

    try {
        let resp;
        if (id) {
            resp = await apiFetch(`/api/disponibilidades/${id}`, {
                method: "PUT",
                body: JSON.stringify(dto)
            });
        } else {
            resp = await apiFetch("/api/disponibilidades", {
                method: "POST",
                body: JSON.stringify(dto)
            });
        }

        if (!resp || !resp.ok) {
            const txt = await resp.text();
            mostrarErro("disponibilidadesMsg", txt || "Erro ao salvar disponibilidade.");
            return;
        }

        mostrarSucesso("disponibilidadesMsg", "Disponibilidade salva com sucesso!");
        limparFormularioDisp();
        carregarDisponibilidades();
    } catch (e) {
        console.error(e);
        mostrarErro("disponibilidadesMsg", "Erro ao salvar disponibilidade.");
    }
}

function limparFormularioDisp() {
    document.getElementById("disponibilidadeId").value = "";
    document.getElementById("diaSemana").value = "";
    document.getElementById("horaInicio").value = "";
    document.getElementById("horaFim").value = "";
    document.getElementById("ativoDisp").value = "true";
}

async function editarDisponibilidade(id) {
    try {
        const resp = await apiFetch(`/api/disponibilidades/barbeiro/${getUserData().userId}`);
        if (!resp || !resp.ok) return;
        const lista = await resp.json();
        const d = lista.find(x => x.id === id);
        if (!d) return;

        document.getElementById("disponibilidadeId").value = d.id;
        document.getElementById("diaSemana").value = d.diaSemana;
        document.getElementById("horaInicio").value = d.horaInicio;
        document.getElementById("horaFim").value = d.horaFim;
        document.getElementById("ativoDisp").value = d.ativo ? "true" : "false";
    } catch (e) {
        console.error(e);
    }
}

async function deletarDisponibilidade(id) {
    if (!confirm("Deseja realmente excluir esta disponibilidade?")) return;

    try {
        const resp = await apiFetch(`/api/disponibilidades/${id}`, { method: "DELETE" });
        if (!resp || (resp.status !== 204 && !resp.ok)) {
            mostrarErro("disponibilidadesMsg", "Erro ao excluir disponibilidade.");
            return;
        }
        mostrarSucesso("disponibilidadesMsg", "Disponibilidade excluída com sucesso.");
        carregarDisponibilidades();
    } catch (e) {
        console.error(e);
        mostrarErro("disponibilidadesMsg", "Erro ao excluir disponibilidade.");
    }
}