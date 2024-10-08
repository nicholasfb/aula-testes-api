package br.com.fiap.aula_testes_api.repository;

import br.com.fiap.aula_testes_api.model.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {
    @Query("SELECT m FROM Mensagem m ORDER BY m.dataCriacaoMensagem DESC")
    Page<Mensagem> listarMensagens(Pageable pageable);
}
