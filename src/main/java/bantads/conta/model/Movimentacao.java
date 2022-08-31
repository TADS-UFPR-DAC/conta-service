package bantads.conta.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, unique = false)
    private String tipoMovimentacao;

    @Column(nullable = false, unique = false)
    private Long valor;

    @Column(nullable = true, unique = false)
    private Long idClienteOrigem;

    @Column(nullable = true, unique = false)
    private Long idClienteDestino;

    public Movimentacao() {
    }

    public Movimentacao(LocalDateTime dataHora, String tipoMovimentacao, Long valor, Long idClienteOrigem) {
        this.dataHora = dataHora;
        this.tipoMovimentacao = tipoMovimentacao;
        this.valor = valor;
        this.idClienteOrigem = idClienteOrigem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(String tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public Long getValor() {
        return valor;
    }

    public void setValor(Long valor) {
        this.valor = valor;
    }

    public Long getIdClienteOrigem() {
        return idClienteOrigem;
    }

    public void setIdClienteOrigem(Long idClienteOrigem) {
        this.idClienteOrigem = idClienteOrigem;
    }

    public Long getIdClienteDestino() {
        return idClienteDestino;
    }

    public void setIdClienteDestino(Long idClienteDestino) {
        this.idClienteDestino = idClienteDestino;
    }
}
