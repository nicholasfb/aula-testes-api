package br.com.fiap.aula_testes_api.service;

import br.com.fiap.aula_testes_api.exception.MensagemNotFoundException;
import br.com.fiap.aula_testes_api.model.Mensagem;
import br.com.fiap.aula_testes_api.repository.MensagemRepository;
import br.com.fiap.aula_testes_api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem{
        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = MensagemHelper.gerarMensagem();

            var resultadoObtido = mensagemService.registrarMensagem(mensagem);

            assertThat(resultadoObtido)
                    .isNotNull()
                    .isInstanceOf(Mensagem.class);

            assertThat(resultadoObtido.getId())
                    .isNotNull();

            assertThat(resultadoObtido.getDataCriacaoMensagem())
                    .isNotNull();

            assertThat(resultadoObtido.getGostei())
                    .isNotNull();
            }
    }

    @Nested
    class BuscarMensagem{
        @Test
        void devePermitirBuscarMensagem(){
            var id = UUID.fromString("5f789b39-4295-42c1-a65b-cfca5b987db2");
            var resultadoObtido = mensagemService.buscarMensagem(id);

            assertThat(resultadoObtido)
                    .isNotNull()
                    .isInstanceOf(Mensagem.class);

            assertThat(resultadoObtido.getId())
                    .isNotNull()
                    .isEqualTo(id);

            assertThat(resultadoObtido.getUsuario())
                    .isNotNull()
                    .isEqualTo("Adam");

            assertThat(resultadoObtido.getConteudo())
                    .isNotNull()
                    .isEqualTo("abcd 1234");

            assertThat(resultadoObtido.getDataCriacaoMensagem())
                    .isNotNull();

            assertThat(resultadoObtido.getGostei())
                    .isEqualTo(0);
        }

        @Test
        void deveGerarExececao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = UUID.fromString("239dc23f-3d48-41da-b644-0655235c1e91");
            assertThatThrownBy(() ->mensagemService.buscarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class AlterarMensagem{
        @Test
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("65b1bbee-c784-4457-be6d-d00b0be5c9e0");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            var resultadoObtido = mensagemService.alterarMensagem(id, mensagemAtualizada);

            assertThat(resultadoObtido.getId()).isEqualTo(id);
            assertThat(resultadoObtido.getUsuario()).isEqualTo(mensagemAtualizada.getUsuario());
            assertThat(resultadoObtido.getConteudo()).isEqualTo(mensagemAtualizada.getConteudo());

        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("f0a92ea4-e56e-40de-a5fc-cdd0880e0905");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("85d16404-0af9-46ed-bdf4-5c2eadedab94");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(UUID.fromString("9993443e-727c-436e-90de-c37458c7c308"));

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem atualizada não apresenta o ID correto");
        }
    }

    @Nested
    class RemoverMensagem{
        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("a02bc76a-9e20-4557-be2b-ee4d5b6fa636");
            var resultadoObtido = mensagemService.removerMensagem(id);

            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.fromString("71a0a2d2-3732-4b01-bcea-5764eadbd55e");

            assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");

        }
    }

    @Nested
        class ListarMensagem{
        @Test
        void devePermitirListarMensagens() {
            Page<Mensagem> listaDeMensagensObtida = mensagemService.listarMensagens(Pageable.unpaged());

            assertThat(listaDeMensagensObtida).hasSize(3);
            assertThat(listaDeMensagensObtida.getContent())
                    .asList()
                    .allSatisfy(mensagemObtida -> {
                        assertThat(mensagemObtida).isNotNull();
                    });
        }
    }
}
