package bantads.conta.service;

import bantads.conta.create.repository.CreateContaRepository;
import bantads.conta.create.repository.CreateMovimentacaoRepository;
import bantads.conta.model.Conta;
import bantads.conta.model.Movimentacao;
import bantads.conta.read.repository.ReadContaRepository;
import bantads.conta.read.repository.ReadMovimentacaoRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static bantads.conta.config.RabbitMQConfig.*;

@Service
@Transactional
public class ContaService {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ReadContaRepository readContaRepository;

    @Autowired
    private CreateContaRepository createContaRepository;

    @Autowired
    private ReadMovimentacaoRepository readMovimentacaoRepository;

    @Autowired
    private CreateMovimentacaoRepository createMovimentacaoRepository;

    public Optional<Conta> getByIdCliente(Long idCliente){
        Optional<Conta> conta = readContaRepository.findByIdCliente(idCliente);
        if(conta.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada!");
        return conta;
    }

    public Conta insert(Conta conta) {
        Optional<Conta> exists = readContaRepository.findByIdCliente(conta.getIdCliente());
        if(exists.isPresent()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta para esse cliente já existe!");

        if(conta.getSalario() >= 2000L) conta.setLimite(conta.getSalario() / 2L);
        else conta.setLimite(-1L);
        conta.setSaldo(0L);
        conta.setDataCriacao(LocalDate.now());

        Long id = readContaRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(1L);
        conta.setId(id);

        log.info("Salvando nova conta...");
        createContaRepository.save(conta);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_CONTA, conta);

        return conta;
    }

    public void depositar(Long idCliente, Long valor){
        Optional<Conta> contaOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada!");

        Movimentacao deposito = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.DEPOSITO.name(),
                valor,
                idCliente);


        Long id = readMovimentacaoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(0L);
        deposito.setId(id);
        createMovimentacaoRepository.save(deposito);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_MOVIMENTACAO, deposito);

        Conta conta = contaOptional.get();
        conta.setSaldo(conta.getSaldo() + valor);

        createContaRepository.save(conta);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_CONTA, conta);
    }

    public void sacar(Long idCliente, Long valor){
        Optional<Conta> contaOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada!");

        Movimentacao saque = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.SAQUE.name(),
                valor,
                idCliente);

        Long id = readMovimentacaoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(0L);
        saque.setId(id);
        createMovimentacaoRepository.save(saque);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_MOVIMENTACAO, saque);

        Conta conta = contaOptional.get();
        if(conta.getSaldo() < valor) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        conta.setSaldo(conta.getSaldo() - valor);

        createContaRepository.save(conta);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_CONTA, conta);
    }

    public void transferir(Long idCliente, Long idClienteDestino, Long valor){
        if(Objects.equals(idCliente, idClienteDestino))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conta origem e destino não podem ser iguais!");
        Optional<Conta> contaOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta origem não encontrada!");
        Optional<Conta> contaDestinoOptional = readContaRepository.findByIdCliente(idClienteDestino);
        if(contaDestinoOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta destino não encontrada!");

        Movimentacao transferencia = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.TRANSFERENCIA.name(),
                valor,
                idCliente);
        transferencia.setIdClienteDestino(idClienteDestino);

        Long id = readMovimentacaoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(0L);
        transferencia.setId(id);
        createMovimentacaoRepository.save(transferencia);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_MOVIMENTACAO, transferencia);

        Conta conta = contaOptional.get();
        if(conta.getSaldo() < valor) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
        conta.setSaldo(conta.getSaldo() - valor);

        createContaRepository.save(conta);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_CONTA, conta);

        Conta contaDestino = contaDestinoOptional.get();
        contaDestino.setSaldo(contaDestino.getSaldo() + valor);

        createContaRepository.save(contaDestino);

        rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_SALVAR_CONTA, contaDestino);
    }

    public List<Movimentacao> extrato(Long idCliente){
        return readMovimentacaoRepository.findAllByIdClienteOrigem(idCliente);
    }

    public String deleteByIdCliente(Long idCliente){
        Optional<Conta> exists = readContaRepository.findByIdCliente(idCliente);
        if(exists.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado para exclusão da conta");
        } else {
            createContaRepository.delete(exists.get());

            rabbitTemplate.convertAndSend(NOME_EXCHANGE, CHAVE_DELETAR_CONTA, exists.get());

            return "Conta deletada com sucesso!";
        }
    }

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
    public void deletarConta(final Message message, final Conta conta) {
        log.info("Deletando conta do usuário de id " + conta.getIdCliente());
        readContaRepository.delete(conta);
    }
}
