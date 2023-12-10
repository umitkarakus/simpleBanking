package simpleBanking;

public abstract class TransactionService {

  double amount;
  String type;

  public TransactionService(double amount){
    this.amount=amount;
  }

}
