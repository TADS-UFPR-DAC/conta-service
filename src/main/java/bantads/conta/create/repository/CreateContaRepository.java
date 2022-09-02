package bantads.conta.create.repository;

import bantads.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreateContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> deleteByIdCliente(Long idCliente);
}
