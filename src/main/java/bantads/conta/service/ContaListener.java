package bantads.conta.service;

import bantads.conta.model.Conta;
import bantads.conta.model.Movimentacao;
import bantads.conta.read.repository.ReadContaRepository;
import bantads.conta.read.repository.ReadMovimentacaoRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static bantads.conta.config.RabbitMQConfig.*;

@Service
public class ContaListener {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ReadContaRepository readContaRepository;

    @Autowired
    private ReadMovimentacaoRepository readMovimentacaoRepository;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(FILA_SALVAR_CONTA),
            exchange = @Exchange(name = NOME_EXCHANGE),
            key = CHAVE_SALVAR_CONTA))
    public void salvarConta(final Message message, final Conta conta) {
        log.info("Salvando conta do usuário de id "+conta.getIdCliente());
        readContaRepository.save(conta);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(FILA_SALVAR_MOVIMENTACAO),
            exchange = @Exchange(name = NOME_EXCHANGE),
            key = CHAVE_SALVAR_MOVIMENTACAO))
    public void salvarMovimentacao(final Message message, final Movimentacao movimentacao) {
        log.info("Salvando movimentação do usuário de id " + movimentacao.getIdClienteOrigem() +
                " de tipo "+movimentacao.getTipoMovimentacao());
        readMovimentacaoRepository.save(movimentacao);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(FILA_DELETAR_CONTA),
            exchange = @Exchange(name = NOME_EXCHANGE),
            key = CHAVE_DELETAR_CONTA))
    public void deletarConta(final Message message, final Long idCliente) {
        log.info("Deletando conta do usuário de id " + idCliente);
        readContaRepository.deleteByIdCliente(idCliente);
    }
}
