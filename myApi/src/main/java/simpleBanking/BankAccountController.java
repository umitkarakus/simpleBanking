package simpleBanking;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/account/v1")
public class BankAccountController {

  private String accountNumber=null;


  private BankAccountRepository bankAccountRepository;
  private TransactionRepository transactionRepository;

  public BankAccountController(BankAccountRepository bankAccountRepository,TransactionRepository transactionRepository){
    this.bankAccountRepository=bankAccountRepository;
    this.transactionRepository=transactionRepository;
  }

@GetMapping()
public List<BankAccount> getBankAccounts(){
  return bankAccountRepository.findAll();

}

@PostMapping()
public ResponseEntity<?> postBankAccounts(@RequestBody
Map<String,String> body) {
  try {
    BankAccount bankAccount =new BankAccount();
    bankAccount.setOwner(body.get("owner"));
    bankAccount.setAccountNumber(body.get("accountNumber"));
    bankAccount.setCreateDate(new Date());
    bankAccountRepository.save(bankAccount);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }catch (Exception e){
    return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
  }
  }

  @PostMapping("/{transactionType}/{accountNumber}")
  public ResponseEntity<?> postTransactions(
      @PathVariable(value="transactionType") String transactionType,
      @PathVariable(value="accountNumber") String accountNumber,
      @RequestBody
      Transaction transaction){

    this.accountNumber=accountNumber;
    String approvalCode=null;
    try {
      if(transactionType.equals("debit")){
         approvalCode=debit(transaction.getAmount());
      }else {
        approvalCode=credit(transaction.getAmount());
      }
      transaction.setApprovalCode(approvalCode);
      return new ResponseEntity<>(transaction.getApprovalCode(),HttpStatus.OK);
    }catch (Exception e){
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/{accountNumber}")
  public BankAccountResponse getTransactions(@PathVariable(value="accountNumber") String accountNumber){
    BankAccountResponse bankAccount=new BankAccountResponse();
    List<BankAccount> bankAccountEntity= bankAccountRepository.findByAccountNumber(accountNumber);
    List<Transaction> transactionList = transactionRepository.findByAccountNumber(accountNumber);
    bankAccount.setAccountNumber(accountNumber);
    bankAccount.setOwner(bankAccountEntity.get(0).getOwner());
    bankAccount.setBalance(bankAccountEntity.get(0).getBalance());
    bankAccount.setCreateDate(bankAccountEntity.get(0).getCreateDate());
    bankAccount.setTransactions(transactionList);
    return bankAccount;
  }

private String debit(double amount){
    TransactionService transactionService=new DepositTransaction(amount);
    return postTransaction(transactionService);
}

private String credit(double amount) {
  TransactionService transactionService = new WithdrawalTransaction(amount);
  return postTransaction(transactionService);
}

private String postTransaction(TransactionService transactionService){
  Transaction transaction =new Transaction();
  transaction.setAmount(transactionService.amount <=0 ? (-1*transactionService.amount) : transactionService.amount);
  transaction.setType(transactionService.type);
  transaction.setDate(new Date());
  transaction.setAccountNumber(accountNumber);
  transaction.setApprovalCode(UUID.randomUUID().toString());
  List<BankAccount> bankAccount = bankAccountRepository.findByAccountNumber(accountNumber);
  bankAccount.get(0).setBalance(bankAccount.get(0).getBalance()+transactionService.amount);
  transactionRepository.save(transaction);
  bankAccountRepository.saveAll(bankAccount);
  return transaction.getApprovalCode();
}
}
