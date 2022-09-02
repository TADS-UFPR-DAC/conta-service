package bantads.conta.create.repository;

import bantads.conta.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreateMovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
}
