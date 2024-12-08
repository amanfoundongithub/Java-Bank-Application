from transaction.transaction_schema import TransactionBase, Transaction
from user.user_schema import User

from sqlalchemy.orm import Session

import uuid 


def addTransaction(transaction : TransactionBase, db : Session):
    # Check if sender in DB
    sender = db.query(User).filter(User.username == transaction.senderId).first()
    if sender is None:
        raise ValueError("Sender Username is Invalid")
    
    # Check if receiver in DB
    receiver = db.query(User).filter(User.username == transaction.receiverId).first()
    if receiver is None:
        raise ValueError("Receiver Username is Invalid")
    
    # Assign ID
    transaction_id = str(uuid.uuid4())
    
    status = None 
    serverMessage = ""
    
    
    try:
        # Check if sender has sufficient balance
        if sender.balance < transaction.amount:
            raise ZeroDivisionError("Sender does not have sufficient balance")
        # Update the balances 
        sender.balance -= transaction.amount
        receiver.balance += transaction.amount
        
        status = True 
        serverMessage = "SUCCESS"
    except Exception as e:
        status = False
        serverMessage = str(e) 
    
    
    
    
    # Create transaction object
    db_transaction = Transaction(
        transactionid = transaction_id,
        senderid = transaction.senderId,
        receiverid = transaction.receiverId,
        amount = transaction.amount,
        description = transaction.description,
        status = status, 
        servermessage = serverMessage
    )
    
    db.add(db_transaction)
    db.commit()
    
    db.refresh(sender)
    db.refresh(receiver)
    db.refresh(db_transaction)
    
    return transaction_id


def confirmTransaction(transactionId : str, db : Session):
    transaction = db.query(Transaction).filter(Transaction.transactionid == transactionId).first()
    
    if transaction:
        # Return json 
        return {
            "transactionId": transaction.transactionid,
            "senderId": transaction.senderid,
            "receiverId": transaction.receiverid,
            "amount": transaction.amount,
            "description": transaction.description,
            "signed": transaction.status,
            "serverMessage": transaction.servermessage,
            "transactionDate": transaction.transactiondate.isoformat()
        }
        
    else: 
        raise ValueError("Transaction not found") 
    

def findAllTransactions(username : str, db : Session):
    # 
    transactions = db.query(Transaction).filter(
        (Transaction.senderid == username) | (Transaction.receiverid == username)
    ).all()
    
    transactions_list = [
        {
            "transactionId": transaction.transactionid,
            "senderId": transaction.senderid,
            "receiverId": transaction.receiverid,
            "amount": transaction.amount,
            "description": transaction.description,
            "signed": transaction.status,
            "serverMessage": transaction.servermessage,
            "transactionDate": transaction.transactiondate.isoformat()
        }
        for transaction in transactions
    ]
    
    return transactions_list
    