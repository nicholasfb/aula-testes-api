package br.com.fiap.aula_testes_api.service;

import br.com.fiap.aula_testes_api.exception.MensagemNotFoundException;
import br.com.fiap.aula_testes_api.model.Mensagem;
import br.com.fiap.aula_testes_api.repository.MensagemRepository;
import br.com.fiap.aula_testes_api.utils.MensagemHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception{
        mock.close();
    }
    @Test
    void devePermitirRegistrarMensagem(){
        Mensagem mensagem = MensagemHelper.gerarMensagem();
        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));

        Mensagem mensagemRegistrada = mensagemService.registrarMensagem(mensagem);

        assertThat(mensagemRegistrada).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemRegistrada.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRegistrada.getUsuario()).isEqualTo(mensagem.getUsuario());
        assertThat(mensagem.getId()).isNotNull();
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void devePermitirBuscarMensagem() {
        var id = UUID.fromString("6e4d1259-11db-4cb7-9e5d-c60750c2d90a"); //Utilizar o ID estático ao invés Random, melhora a performance
        Mensagem mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagem));

        var mensagemObtida = mensagemService.buscarMensagem(id);
        assertThat(mensagemObtida).isEqualTo(mensagem);
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste(){
        var id = UUID.fromString("897b07c7-8385-4ea1-b9f0-17bffe109307");
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    void devePermitirAlterarMensagem(){
        //Arrange
        var id = UUID.fromString("4f5eac45-299f-48fd-8c6b-d6e55bed263e");

        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = new Mensagem();
        mensagemNova.setId(mensagemAntiga.getId());
        mensagemNova.setUsuario(mensagemAntiga.getUsuario());
        mensagemNova.setConteudo("ABCD 12345");

        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagemAntiga));

        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));

        //Act
        var mensagemObtida = mensagemService.alterarMensagem(id, mensagemNova);

        // Assert
        assertThat(mensagemObtida).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemObtida.getId()).isEqualTo(mensagemNova.getId());
        assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagemNova.getUsuario());
        assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemNova.getConteudo());
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste(){
        //Arrange
        var id = UUID.fromString("ed3b274e-4c54-429f-8f1d-6bfbb72ffd20");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        //Act
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy( () -> mensagemService.alterarMensagem(id, mensagem))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");

        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }


    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente(){
        //Arrange
        var id = UUID.fromString("47ef01d7-6f7a-4092-ad7f-32faae61ecec");
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = MensagemHelper.gerarMensagem();
        mensagemNova.setId(UUID.fromString("6f2f901b-9648-4030-9113-585741b7d489"));
        mensagemNova.setConteudo("ABCD 123");


        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));

        //Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem atualizada não apresenta o ID correto");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirRemoverMensagem(){
        //Arrange
        var id = UUID.fromString("03691449-39cc-41dc-bae1-5dc1a43c7991");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagem));
        doNothing().when(mensagemRepository).deleteById(id);

        //Act
        var mensagemFoiRemovida = mensagemService.removerMensagem(id);

        //Assert
        assertThat(mensagemFoiRemovida).isTrue();
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).deleteById(any(UUID.class));

    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste(){
         //Arrange
        var id = UUID.fromString("03691449-39cc-41dc-bae1-5dc1a43c7991");
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy( () -> mensagemService.removerMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");

        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).deleteById(any(UUID.class));
    }

    /*@Test
    void devePermitirListarMensagens(){
        //Arrange
        Page<Mensagem> listaDeMensagens = new PageImpl<>(Arrays.asList(
                MensagemHelper.gerarMensagem(),
                MensagemHelper.gerarMensagem()
        ));
        when(mensagemRepository.listarMensagens(any(Pageable.class))).thenReturn(listaDeMensagens);

        //Act
        var resultadoObtido = mensagemService.listarMensagens(Pageable.unpaged());

        //Assert
        assertThat(resultadoObtido).hasSize(2);
        assertThat(resultadoObtido.getContent())
                .asList()
                .allSatisfy(mensagem -> {
                  assertThat(mensagem).isNotNull().isInstanceOf(Mensagem.class);
                });
        verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
    }

     */
}
