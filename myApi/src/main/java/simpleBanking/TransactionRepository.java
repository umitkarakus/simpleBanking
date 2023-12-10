package simpleBanking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
  List<Transaction> findByAccountNumber(String accountNumber);
}
