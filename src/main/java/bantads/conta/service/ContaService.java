package bantads.conta.service;

import bantads.conta.exception.ApiRequestException;
import bantads.conta.exception.ContaException;
import bantads.conta.model.Cliente;
import bantads.conta.model.Conta;
import bantads.conta.model.Movimentacao;
import bantads.conta.model.TipoMovimentacao;
import bantads.conta.repository.ContaRepository;
import bantads.conta.repository.MovimentacaoRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ContaService {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    public Optional<Conta> getByIdCliente(Long id){
        Optional<Conta> conta = contaRepository.findById(id);
        if(conta.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);
        return conta;
    }

    public Conta insert(Conta conta) {
        Optional<Conta> exists = contaRepository.findByIdCliente(conta.getIdCliente());
        if(exists.isPresent()) throw new ContaException("Conta para esse cliente já existe!", HttpStatus.BAD_REQUEST);

        // TODO: pegar cliente do microsserviço de cliente pelo conta.getIdCliente para pegar o salário
        // ou pedir pra ana fazer o front mandar o salário pra esse endpoint, q é bem mais fácil
        Cliente cliente = new Cliente();
        cliente.setSalario(3000L);
        //

        if(cliente.getSalario() >= 2000L) conta.setLimite(cliente.getSalario() / 2L);
        else conta.setLimite(-1L);
        conta.setSaldo(0L);
        conta.setDataCriacao(LocalDate.now());

        log.info("Salvando nova conta...");
        return contaRepository.save(conta);
    }

    public void depositar(Long idCliente, Long valor){
        Optional<Conta> contaOptional = contaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);

        Movimentacao deposito = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.DEPOSITO,
                valor,
                idCliente);

        movimentacaoRepository.save(deposito);

        Conta conta = contaOptional.get();
        conta.setSaldo(conta.getSaldo() + valor);

        contaRepository.save(conta);
    }

    public void sacar(Long idCliente, Long valor){
        Optional<Conta> contaOptional = contaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);

        Movimentacao saque = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.SAQUE,
                valor,
                idCliente);

        movimentacaoRepository.save(saque);

        Conta conta = contaOptional.get();
        if(conta.getSaldo() < valor) throw new ContaException("Saldo insuficiente", HttpStatus.BAD_REQUEST);
        conta.setSaldo(conta.getSaldo() - valor);

        contaRepository.save(conta);
    }

    public void transferir(Long idCliente, Long idClienteDestino, Long valor){
        Optional<Conta> contaOptional = contaRepository.findByIdCliente(idCliente);
        if(contaOptional.isEmpty()) throw new ContaException("Conta não encontrada!", HttpStatus.NOT_FOUND);
        Optional<Conta> contaDestinoOptional = contaRepository.findByIdCliente(idCliente);
        if(contaDestinoOptional.isEmpty()) throw new ContaException("Conta destino não encontrada!", HttpStatus.NOT_FOUND);

        Movimentacao transferencia = new Movimentacao(LocalDateTime.now(),
                TipoMovimentacao.TRANSFERENCIA,
                valor,
                idCliente);
        transferencia.setIdClienteDestino(idClienteDestino);

        movimentacaoRepository.save(transferencia);

        Conta conta = contaOptional.get();
        if(conta.getSaldo() < valor) throw new ContaException("Saldo insuficiente", HttpStatus.BAD_REQUEST);
        conta.setSaldo(conta.getSaldo() - valor);

        contaRepository.save(conta);

        Conta contaDestino = contaOptional.get();
        contaDestino.setSaldo(conta.getSaldo() + valor);

        contaRepository.save(contaDestino);
    }

    public List<Movimentacao> extrato(Long idCliente){
        return movimentacaoRepository.findAllByIdCliente(idCliente);
    }

    public String deleteByIdCliente(Long idCliente){
        Optional<Conta> exists = contaRepository.deleteByIdCliente(idCliente);
        if(exists.isEmpty()) throw new ContaException("Cliente não encontrado para exclusão da conta", HttpStatus.NOT_FOUND);
        else return "Conta deletada com sucesso!";
    }
}
