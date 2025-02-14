package br.com.fiap.aula_testes_api.controller;

import br.com.fiap.aula_testes_api.exception.MensagemNotFoundException;
import br.com.fiap.aula_testes_api.model.Mensagem;
import br.com.fiap.aula_testes_api.service.MensagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mensagem> registrarMensagem(@RequestBody Mensagem mensagem) {
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        return new ResponseEntity<Mensagem>(mensagemRegistrada, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> buscarMensagem(@PathVariable String id) {
        var uuid = UUID.fromString(id);
        try {
            var mensagemEncontrada = mensagemService.buscarMensagem(uuid);
            return new ResponseEntity<>(mensagemEncontrada, HttpStatus.OK);
        } catch(MensagemNotFoundException mensagemNotFoundException){
            return new ResponseEntity<>("ID Inválido", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mensagem> alterarMensagem(@PathVariable String id, @RequestBody Mensagem mensagem) {
        var uuid = UUID.fromString(id);
        try {
            var mensagemAtualizada = mensagemService.alterarMensagem(uuid, mensagem);

            return new ResponseEntity<>(mensagemAtualizada, HttpStatus.ACCEPTED);
        } catch (MensagemNotFoundException mensagemNotFoundException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mensagemNotFoundException.getMessage());
        }
    }
}
