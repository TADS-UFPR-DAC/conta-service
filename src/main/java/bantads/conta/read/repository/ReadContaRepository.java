package bantads.conta.read.repository;

import bantads.conta.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByIdCliente(Long idCliente);
    Optional<Conta> findFirstByOrderByIdDesc();
	Optional<Conta> findByStatus(String status);
	List<Conta> findTop5ByOrderBySaldoDesc();
}
