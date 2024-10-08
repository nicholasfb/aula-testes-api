package br.com.fiap.aula_testes_api.repository;

import br.com.fiap.aula_testes_api.model.Mensagem;
import br.com.fiap.aula_testes_api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela(){
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isNotNegative();
    }

    @Test
    void devePermitirRegistrarMensagem(){
        //Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        //ACT
        var mensagemRecebida = mensagemRepository.save(mensagem);

        //Assert
        assertThat(mensagemRecebida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRecebida.getId()).isEqualTo(id);
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());
    }

    @Test
    void devePermitirBuscarMensagem(){
        //Arrange
        var id = UUID.fromString("5f789b39-4295-42c1-a65b-cfca5b987db2");

        //Act
        var mensagemRecebidaOptional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOptional).isPresent();

        mensagemRecebidaOptional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(id);
        });
    }

    @Test
    void devePermitirRemoverMensagem(){
        // Arrange
        var id = UUID.fromString("65b1bbee-c784-4457-be6d-d00b0be5c9e0");

        //Act
        mensagemRepository.deleteById(id);
        var mensagemRecebidaOptional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOptional).isEmpty();
    }

    @Test
    void devePermitirListarMensagem(){
        // Act
        var resultadosObtidos = mensagemRepository.findAll();
        //Assert
        assertThat(resultadosObtidos).hasSizeGreaterThan(0);
    }
}
