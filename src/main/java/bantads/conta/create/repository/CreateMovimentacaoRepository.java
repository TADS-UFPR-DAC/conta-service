package bantads.conta.create.repository;

import bantads.conta.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreateMovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
}
