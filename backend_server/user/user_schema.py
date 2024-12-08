from pydantic import BaseModel

from sqlalchemy import Column, String, Float, BigInteger, Boolean, DateTime
from sqlalchemy.sql import func
from engine import Base

############################ DATABASE SCHEMA ######################

class User(Base):
    __tablename__ = "users"
    id = Column(String, primary_key=True, index=True)
    email = Column(String, unique=True, nullable=False)
    password = Column(String, nullable=False)
    username = Column(String, unique=True, nullable=False)
    name = Column(String, nullable=False)
    balance = Column(Float, default=0.0)
    phone = Column(BigInteger, nullable=False)
    premium = Column(Boolean, default=False)
    created_at = Column(DateTime, server_default=func.now())  
    
############################ DATABASE SCHEMA ######################
    

class UserBase(BaseModel):
    email : str 
    password : str
    

    
class UserAuthenticate(UserBase):
    pass
    
    
class UserDetails(UserAuthenticate):
    id : str = ""
    username : str 
    name : str 
    balance : float 
    phone : int 
    premium : bool = False 