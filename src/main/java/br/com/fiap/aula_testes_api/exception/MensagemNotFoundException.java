package br.com.fiap.aula_testes_api.exception;

public class MensagemNotFoundException extends RuntimeException{

    public MensagemNotFoundException(String mensagem){
        super(mensagem);
    }
}
