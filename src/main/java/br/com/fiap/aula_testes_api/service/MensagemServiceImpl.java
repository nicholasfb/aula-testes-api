package br.com.fiap.aula_testes_api.service;

import br.com.fiap.aula_testes_api.exception.MensagemNotFoundException;
import br.com.fiap.aula_testes_api.model.Mensagem;
import br.com.fiap.aula_testes_api.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemServiceImpl implements MensagemService{
    private final MensagemRepository mensagemRepository;

    @Override
    public Mensagem registrarMensagem(Mensagem mensagem) {
        mensagem.setId(UUID.randomUUID());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public Mensagem buscarMensagem(UUID id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("Mensagem não encontrada"));
    }

    @Override
    public Mensagem alterarMensagem(UUID id, Mensagem mensagemAtualizada) {
        var mensagem = buscarMensagem(id);
        if(!mensagem.getId().equals(mensagemAtualizada.getId())) {
            throw new MensagemNotFoundException("Mensagem atualizada não apresenta o ID correto");
        }
        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        mensagem.setUsuario(mensagemAtualizada.getUsuario());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        buscarMensagem(id);
        mensagemRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<Mensagem> listarMensagens(Pageable pageable) {
        return mensagemRepository.listarMensagens(pageable);
    }


}
