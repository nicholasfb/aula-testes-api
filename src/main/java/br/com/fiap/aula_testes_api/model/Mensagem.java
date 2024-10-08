package br.com.fiap.aula_testes_api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class Mensagem {

    @Id
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "Usuário não pode estar vazio")
    private String usuario;

    @Column(nullable = false)
    @NotEmpty(message = "conteúdo não pode estar vazio")
    private String conteudo;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    private LocalDateTime dataCriacaoMensagem = LocalDateTime.now();

    @Builder.Default
    private Integer gostei = 0;
}
