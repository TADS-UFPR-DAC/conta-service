package bantads.conta.repository;

import bantads.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByIdCliente(Long idCliente);
    Optional<Conta> deleteByIdCliente(Long idCliente);
}
