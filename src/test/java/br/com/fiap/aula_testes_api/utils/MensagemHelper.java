package br.com.fiap.aula_testes_api.utils;

import br.com.fiap.aula_testes_api.model.Mensagem;

public abstract class MensagemHelper {

    public static Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("Silva Sauro")
                .conteudo("conteúdo da mensagem")
                .build();
    }
}
