package bantads.conta.read.repository;

import bantads.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public interface ReadContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByIdCliente(Long idCliente);
    Optional<Conta> findFirstByOrderByIdDesc();
    Optional<Conta> deleteByIdCliente(Long idCliente);
}
