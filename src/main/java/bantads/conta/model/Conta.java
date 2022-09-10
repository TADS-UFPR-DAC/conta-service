package bantads.conta.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table
public class Conta {

    @Id
    private Long id;
    
    @Column(nullable = true, unique = false)
    private String status;

    @Column(nullable = false, unique = true)
    private Long idCliente;
    
    @Column(nullable = true, unique = false)
    private Long idGerente;

    @Column(nullable = false, unique = true)
    private Long numero;

    @Column(nullable = false, unique = false)
    private Long saldo;

    @Column(nullable = false, unique = false)
    private LocalDate dataCriacao;

    @Column(nullable = false, unique = false)
    private Long limite;

    @Column(nullable = false, unique = false)
    private Long salario;

    public Conta() {
    }

    public Conta(Long id, String status, Long idCliente, Long idGerente, Long numero, Long saldo, LocalDate dataCriacao, Long limite, Long salario) {
        this.id = id;
        this.status = status;
        this.idCliente = idCliente;
        this.idGerente = idGerente;
        this.numero = numero;
        this.saldo = saldo;
        this.dataCriacao = dataCriacao;
        this.limite = limite;
        this.salario = salario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
    
    public Long getIdGerente() {
        return idGerente;
    }

    public void setIdGerente(Long idGerente) {
        this.idGerente = idGerente;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Long getSaldo() {
        return saldo;
    }

    public void setSaldo(Long saldo) {
        this.saldo = saldo;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Long getLimite() {
        return limite;
    }

    public void setLimite(Long limite) {
        this.limite = limite;
    }

    public Long getSalario() {
        return salario;
    }

    public void setSalario(Long salario) {
        this.salario = salario;
    }
}
