package simpleBanking;

public class DepositTransaction extends TransactionService{
  public DepositTransaction(double amount) {
    super(amount);
    super.type="DepositTransaction";
  }
}
