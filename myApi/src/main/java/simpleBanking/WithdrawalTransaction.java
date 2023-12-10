package simpleBanking;

public class WithdrawalTransaction extends TransactionService{
  public WithdrawalTransaction(double amount) {
    super(amount*-1);
    super.type="WithdrawalTransaction";
  }


  }
