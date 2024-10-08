package br.com.fiap.aula_testes_api.controller;

import br.com.fiap.aula_testes_api.exception.MensagemNotFoundException;
import br.com.fiap.aula_testes_api.model.Mensagem;
import br.com.fiap.aula_testes_api.service.MensagemService;
import br.com.fiap.aula_testes_api.utils.MensagemHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem{

        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            var mensagemRequest = MensagemHelper.gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
//                    .andDo(print())
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1))
                    .registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(post("/mensagens")
                    .contentType(MediaType.APPLICATION_XML)
                    .contentType(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());

            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem{

        @Test
        void devePermitirBuscarMensagem() throws Exception {
            var id = UUID.fromString("f5bf85a6-6a54-4fe0-952f-200069bfbdbf");
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.buscarMensagem(any(UUID.class)))
                    .thenReturn(mensagem);
            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isOk());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem() throws Exception{
            var id = UUID.fromString("bb99ed66-28c8-4f7c-85b4-3c36c9f4726b");

            when(mensagemService.buscarMensagem(id))
                    .thenThrow(MensagemNotFoundException.class);

            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest());

            verify(mensagemService, times(1)).buscarMensagem(id);
        }
    }

    @Nested
    class AlterarMensagem{

        @Test
        void devePermitirAlterarMensagem() throws Exception {
            var id = UUID.fromString("0de36af3-5c51-4b03-af6a-b577da04ba75");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenReturn(mensagem);

            mockMvc.perform(put("/mensagens/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                            .andExpect(status().isAccepted());

            verify(mensagemService, times(1)).alterarMensagem(id, mensagem);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("a921aa12-1b9c-4374-9321-3805197f61fc");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            var conteudoDaExcecao = "Mensagem atualizada não apresenta o ID correto";

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mensagem)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente(){
            fail("teste não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadXML() throws Exception {
            var id = UUID.fromString("0de36af3-5c51-4b03-af6a-b577da04ba75");
            String xmlPayload =
                    "<mensagem><id> " + id.toString() + "</id><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_XML)
                            .contentType(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());

            verify(mensagemService, never()).alterarMensagem(any(UUID.class),any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem{

        @Test
        void devePermitirRemoverMensagem(){
            fail("teste não implementado");
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste(){
            fail("teste não implementado");
        }
    }

    @Nested
    class ListarMensagem{

        @Test
        void  devePermitirListarMensagem(){
            fail("teste não implementado");
        }
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
