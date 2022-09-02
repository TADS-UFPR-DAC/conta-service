package bantads.conta.read.repository;

import bantads.conta.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadMovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findAllByIdClienteOrigem(Long idCliente);
    Optional<Movimentacao> findFirstByOrderByIdDesc();
}
