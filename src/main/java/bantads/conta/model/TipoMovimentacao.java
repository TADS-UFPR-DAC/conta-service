package bantads.conta.model;

public enum TipoMovimentacao {
    DEPOSITO("deposito"),
    SAQUE("saque"),
    TRANSFERENCIA("transferencia");

    TipoMovimentacao(String nome) {}
}
