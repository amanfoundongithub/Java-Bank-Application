from pydantic import BaseModel

from sqlalchemy import Column, String, Float, Boolean, DateTime
from sqlalchemy.sql import func
from engine import Base 

class Transaction(Base):
    __tablename__ = "transactions"
    transactionid = Column(String, primary_key=True, index=True)
    senderid = Column(String, nullable=False)
    receiverid = Column(String, nullable=False)
    amount = Column(Float, nullable = False)
    description = Column(String, nullable=False)
    transactiondate =  Column(DateTime, server_default=func.now())  
    status = Column(Boolean, default=False)
    servermessage = Column(String, nullable = False) 


class TransactionBase(BaseModel):
    senderId : str 
    receiverId : str 
    amount : float 
    description : str
    