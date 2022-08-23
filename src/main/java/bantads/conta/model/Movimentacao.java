package bantads.conta.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = false)
    private LocalDateTime dataHora;

    @Column(nullable = false, unique = false)
    private TipoMovimentacao tipoMovimentacao;

    @Column(nullable = false, unique = false)
    private Long valor;

    @Column(nullable = true, unique = false)
    private Long idClienteOrigem;

    @Column(nullable = true, unique = false)
    private Long idClienteDestino;

    public Movimentacao(LocalDateTime dataHora, TipoMovimentacao tipoMovimentacao, Long valor, Long idClienteOrigem) {
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

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
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
