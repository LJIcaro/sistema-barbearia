// /js/servicos.js

document.addEventListener("DOMContentLoaded", () => {
    if (!checkAuth() || !isBarbeiro()) return;

    carregarServicos();

    document.getElementById("servicoForm").addEventListener("submit", salvarServico);
    document.getElementById("cancelarEdicao").addEventListener("click", limparFormulario);
});

async function carregarServicos() {
    const container = document.getElementById("servicosList");
    container.innerHTML = "Carregando serviços...";

    try {
        // barbeiro logado -> /api/servicos/meus
        const resp = await apiFetch("/api/servicos/meus");
        if (!resp || !resp.ok) {
            container.innerHTML = "<p class='error-msg'>Erro ao carregar serviços.</p>";
            return;
        }

        const servicos = await resp.json();
        if (!servicos.length) {
            container.innerHTML = "<p class='info-msg'>Você ainda não cadastrou serviços.</p>";
            return;
        }

        let html = "<table><thead><tr>" +
            "<th>Nome</th><th>Preço</th><th>Duração</th><th>Ativo</th><th>Ações</th>" +
            "</tr></thead><tbody>";

        servicos.forEach(s => {
            html += `<tr>
                        <td>${s.nome}</td>
                        <td>${formatarMoeda(s.preco)}</td>
                        <td>${s.duracaoMinutos} min</td>
                        <td>${s.ativo ? "Sim" : "Não"}</td>
                        <td>
                            <button class="btn-secondary btn-small" onclick="editarServico(${s.id})">Editar</button>
                            <button class="btn-danger btn-small" onclick="deletarServico(${s.id})">Excluir</button>
                        </td>
                    </tr>`;
        });

        html += "</tbody></table>";
        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = "<p class='error-msg'>Erro ao carregar serviços.</p>";
    }
}

async function salvarServico(e) {
    e.preventDefault();

    const id = document.getElementById("servicoId").value;
    const nome = document.getElementById("nomeServico").value;
    const descricao = document.getElementById("descricaoServico").value;
    const preco = parseFloat(document.getElementById("precoServico").value);
    const duracao = parseInt(document.getElementById("duracaoServico").value, 10);
    const materiais = document.getElementById("materiaisServico").value;
    const ativo = document.getElementById("ativoServico").value === "true";
    const msgEl = document.getElementById("servicosMsg");

    esconderMensagem("servicosMsg");

    const dto = {
        nome: nome,
        descricao: descricao,
        preco: preco,
        duracaoMinutos: duracao,
        materiaisNecessarios: materiais,
        ativo: ativo
    };

    try {
        let resp;
        if (id) {
            resp = await apiFetch(`/api/servicos/${id}`, {
                method: "PUT",
                body: JSON.stringify(dto)
            });
        } else {
            resp = await apiFetch("/api/servicos", {
                method: "POST",
                body: JSON.stringify(dto)
            });
        }

        if (!resp || !resp.ok) {
            const txt = await resp.text();
            mostrarErro("servicosMsg", txt || "Erro ao salvar serviço.");
            return;
        }

        mostrarSucesso("servicosMsg", "Serviço salvo com sucesso!");
        limparFormulario();
        carregarServicos();
    } catch (e) {
        console.error(e);
        mostrarErro("servicosMsg", "Erro ao salvar serviço.");
    }
}

function limparFormulario() {
    document.getElementById("servicoId").value = "";
    document.getElementById("nomeServico").value = "";
    document.getElementById("descricaoServico").value = "";
    document.getElementById("precoServico").value = "";
    document.getElementById("duracaoServico").value = "";
    document.getElementById("materiaisServico").value = "";
    document.getElementById("ativoServico").value = "true";
}

async function editarServico(id) {
    try {
        const resp = await apiFetch(`/api/servicos/${id}`);
        if (!resp || !resp.ok) return;
        const s = await resp.json();

        document.getElementById("servicoId").value = s.id;
        document.getElementById("nomeServico").value = s.nome;
        document.getElementById("descricaoServico").value = s.descricao || "";
        document.getElementById("precoServico").value = s.preco;
        document.getElementById("duracaoServico").value = s.duracaoMinutos;
        document.getElementById("materiaisServico").value = s.materiaisNecessarios || "";
        document.getElementById("ativoServico").value = s.ativo ? "true" : "false";
    } catch (e) {
        console.error(e);
    }
}

async function deletarServico(id) {
    if (!confirm("Deseja realmente excluir este serviço?")) return;

    try {
        const resp = await apiFetch(`/api/servicos/${id}`, { method: "DELETE" });
        if (!resp || (resp.status !== 204 && !resp.ok)) {
            mostrarErro("servicosMsg", "Erro ao excluir serviço.");
            return;
        }
        mostrarSucesso("servicosMsg", "Serviço excluído com sucesso.");
        carregarServicos();
    } catch (e) {
        console.error(e);
        mostrarErro("servicosMsg", "Erro ao excluir serviço.");
    }
}