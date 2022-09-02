package bantads.conta.service;

import bantads.conta.exception.ContaException;
import bantads.conta.model.Conta;
import bantads.conta.model.Movimentacao;
import bantads.conta.create.repository.CreateContaRepository;
import bantads.conta.create.repository.CreateMovimentacaoRepository;
import bantads.conta.read.repository.ReadContaRepository;
import bantads.conta.read.repository.ReadMovimentacaoRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ReadContaRepository readContaRepository;

    @Autowired
    private CreateContaRepository createContaRepository;

    @Autowired
    private ReadMovimentacaoRepository readMovimentacaoRepository;

    @Autowired
    private CreateMovimentacaoRepository createMovimentacaoRepository;

    public Optional<Conta> getByIdCliente(Long id){
        Optional<Conta> conta = readContaRepository.findById(id);
        if(conta.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);
        return conta;
    }

    public Conta insert(Conta conta) {
        Optional<Conta> exists = readContaRepository.findByIdCliente(conta.getIdCliente());
        if(exists.isPresent()) throw new ContaException("Conta para esse cliente já existe!", HttpStatus.BAD_REQUEST);

        if(conta.getSalario() >= 2000L) conta.setLimite(conta.getSalario() / 2L);
        else conta.setLimite(-1L);
        conta.setSaldo(0L);
        conta.setDataCriacao(LocalDate.now());

        Long id = readContaRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(1L);
        conta.setId(id);

        log.info("Salvando nova conta...");
        createContaRepository.save(conta);

        // TODO: usar rabbitmq pra salvar novaConta no banco de read
        readContaRepository.save(conta);

        return conta;
    }

    public void depositar(Long idCliente, Long valor){
        Optional<Conta> contaOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);

        Movimentacao deposito = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.DEPOSITO.name(),
                valor,
                idCliente);


        Long id = readMovimentacaoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(0L);
        deposito.setId(id);
        createMovimentacaoRepository.save(deposito);

        // TODO: usar rabbitmq pra salvar deposito no banco de read
        readMovimentacaoRepository.save(deposito);

        Conta conta = contaOptional.get();
        conta.setSaldo(conta.getSaldo() + valor);

        createContaRepository.save(conta);

        // TODO: usar rabbitmq pra salvar conta no banco de read
        readContaRepository.save(conta);
    }

    public void sacar(Long idCliente, Long valor){
        Optional<Conta> contaOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);

        Movimentacao saque = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.SAQUE.name(),
                valor,
                idCliente);

        Long id = readMovimentacaoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(0L);
        saque.setId(id);
        createMovimentacaoRepository.save(saque);

        // TODO: usar rabbitmq pra salvar saque no banco de read
        readMovimentacaoRepository.save(saque);

        Conta conta = contaOptional.get();
        if(conta.getSaldo() < valor) throw new ContaException("Saldo insuficiente", HttpStatus.BAD_REQUEST);
        conta.setSaldo(conta.getSaldo() - valor);

        createContaRepository.save(conta);

        // TODO: usar rabbitmq pra salvar conta no banco de read
        readContaRepository.save(conta);
    }

    public void transferir(Long idCliente, Long idClienteDestino, Long valor){
        Optional<Conta> contaOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);
        Optional<Conta> contaDestinoOptional = readContaRepository.findByIdCliente(idCliente);
        if(contaDestinoOptional.isEmpty()) throw new ContaException("Conta destino não encontrada!", HttpStatus.NOT_FOUND);

        Movimentacao transferencia = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.TRANSFERENCIA.name(),
                valor,
                idCliente);
        transferencia.setIdClienteDestino(idClienteDestino);

        Long id = readMovimentacaoRepository.findFirstByOrderByIdDesc().map(value -> value.getId() + 1).orElse(0L);
        transferencia.setId(id);
        createMovimentacaoRepository.save(transferencia);

        // TODO: usar rabbitmq pra salvar transferencia no banco de read
        readMovimentacaoRepository.save(transferencia);

        Conta conta = contaOptional.get();
        if(conta.getSaldo() < valor) throw new ContaException("Saldo insuficiente", HttpStatus.BAD_REQUEST);
        conta.setSaldo(conta.getSaldo() - valor);

        createContaRepository.save(conta);

        // TODO: usar rabbitmq pra salvar conta no banco de read
        readContaRepository.save(conta);

        Conta contaDestino = contaOptional.get();
        contaDestino.setSaldo(conta.getSaldo() + valor);

        createContaRepository.save(contaDestino);

        // TODO: usar rabbitmq pra salvar contaDestino no banco de read
        readContaRepository.save(contaDestino);
    }

    public List<Movimentacao> extrato(Long idCliente){
        return readMovimentacaoRepository.findAllByIdClienteOrigem(idCliente);
    }

    public String deleteByIdCliente(Long idCliente){
        Optional<Conta> exists = createContaRepository.deleteByIdCliente(idCliente);
        if(exists.isEmpty()) throw new ContaException("Cliente não encontrado para exclusão da conta", HttpStatus.NOT_FOUND);
        else {

            // TODO: usar rabbitmq pra excluir conta com idCliente no banco de read
            readContaRepository.deleteByIdCliente(idCliente);

            return "Conta deletada com sucesso!";
        }
    }
}
