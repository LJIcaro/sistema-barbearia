document.addEventListener("DOMContentLoaded", () => {
    if (!checkAuth() || !isBarbeiro()) return;

    carregarBloqueios();

    document.getElementById("bloqueioForm").addEventListener("submit", salvarBloqueio);
    document.getElementById("cancelarBloqEdicao").addEventListener("click", limparFormularioBloq);
});

async function carregarBloqueios() {
    const container = document.getElementById("bloqueiosList");
    container.innerHTML = "Carregando bloqueios...";

    try {
        const resp = await apiFetch("/api/bloqueios/meus");
        if (!resp || !resp.ok) {
            container.innerHTML = "<p class='error-msg'>Erro ao carregar bloqueios.</p>";
            return;
        }

        const bloqueios = await resp.json();
        if (!bloqueios.length) {
            container.innerHTML = "<p class='info-msg'>Você ainda não cadastrou bloqueios.</p>";
            return;
        }

        let html = "<table><thead><tr>" +
            "<th>Início</th><th>Fim</th><th>Motivo</th><th>Ações</th>" +
            "</tr></thead><tbody>";

        bloqueios.forEach(b => {
            html += `<tr>
                        <td>${formatarDataHora(b.dataInicio)}</td>
                        <td>${formatarDataHora(b.dataFim)}</td>
                        <td>${b.motivo || ""}</td>
                        <td>
                            <button class="btn-secondary btn-small" onclick="editarBloqueio(${b.id})">Editar</button>
                            <button class="btn-danger btn-small" onclick="deletarBloqueio(${b.id})">Excluir</button>
                        </td>
                    </tr>`;
        });

        html += "</tbody></table>";
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p class='error-msg'>Erro ao carregar bloqueios.</p>";
    }
}

function toLocalDateTimeInput(isoString) {
    // espera algo como "2025-11-14T12:30:00"
    if (!isoString) return "";
    const d = new Date(isoString);
    const pad = n => n.toString().padStart(2, "0");
    const year = d.getFullYear();
    const month = pad(d.getMonth() + 1);
    const day = pad(d.getDate());
    const hours = pad(d.getHours());
    const minutes = pad(d.getMinutes());
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

async function salvarBloqueio(e) {
    e.preventDefault();

    const id = document.getElementById("bloqueioId").value;
    const dataInicio = document.getElementById("dataInicio").value;
    const dataFim = document.getElementById("dataFim").value;
    const motivo = document.getElementById("motivoBloqueio").value;
    const msgEl = document.getElementById("bloqueiosMsg");

    esconderMensagem("bloqueiosMsg");

    if (!dataInicio || !dataFim) {
        mostrarErro("bloqueiosMsg", "Informe data/hora de início e fim.");
        return;
    }

    if (new Date(dataFim) <= new Date(dataInicio)) {
        mostrarErro("bloqueiosMsg", "Data/hora de fim deve ser maior que a de início.");
        return;
    }

    const dto = {
        dataInicio: dataInicio,
        dataFim: dataFim,
        motivo: motivo
    };

    try {
        let resp;
        if (id) {
            resp = await apiFetch(`/api/bloqueios/${id}`, {
                method: "PUT",
                body: JSON.stringify(dto)
            });
        } else {
            resp = await apiFetch("/api/bloqueios", {
                method: "POST",
                body: JSON.stringify(dto)
            });
        }

        if (!resp || !resp.ok) {
            const txt = await resp.text();
            mostrarErro("bloqueiosMsg", txt || "Erro ao salvar bloqueio.");
            return;
        }

        mostrarSucesso("bloqueiosMsg", "Bloqueio salvo com sucesso!");
        limparFormularioBloq();
        carregarBloqueios();
    } catch (e) {
        console.error(e);
        mostrarErro("bloqueiosMsg", "Erro ao salvar bloqueio.");
    }
}

function limparFormularioBloq() {
    document.getElementById("bloqueioId").value = "";
    document.getElementById("dataInicio").value = "";
    document.getElementById("dataFim").value = "";
    document.getElementById("motivoBloqueio").value = "";
}

async function editarBloqueio(id) {
    try {
        const resp = await apiFetch("/api/bloqueios/meus");
        if (!resp || !resp.ok) return;
        const lista = await resp.json();
        const b = lista.find(x => x.id === id);
        if (!b) return;

        document.getElementById("bloqueioId").value = b.id;
        document.getElementById("dataInicio").value = toLocalDateTimeInput(b.dataInicio);
        document.getElementById("dataFim").value = toLocalDateTimeInput(b.dataFim);
        document.getElementById("motivoBloqueio").value = b.motivo || "";
    } catch (e) {
        console.error(e);
    }
}

async function deletarBloqueio(id) {
    if (!confirm("Deseja realmente excluir este bloqueio?")) return;

    try {
        const resp = await apiFetch(`/api/bloqueios/${id}`, { method: "DELETE" });
        if (!resp || (resp.status !== 204 && !resp.ok)) {
            mostrarErro("bloqueiosMsg", "Erro ao excluir bloqueio.");
            return;
        }
        mostrarSucesso("bloqueiosMsg", "Bloqueio excluído com sucesso.");
        carregarBloqueios();
    } catch (e) {
        console.error(e);
        mostrarErro("bloqueiosMsg", "Erro ao excluir bloqueio.");
    }
}