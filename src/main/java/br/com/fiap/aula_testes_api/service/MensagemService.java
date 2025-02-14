package br.com.fiap.aula_testes_api.service;

import br.com.fiap.aula_testes_api.model.Mensagem;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * TBD.
 */

public interface MensagemService {
    Mensagem registrarMensagem(Mensagem mensagem);

    Mensagem buscarMensagem(UUID id);

    Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada);

    boolean removerMensagem(UUID id);

    Page<Mensagem> listarMensagens(Pageable pageable);
}
