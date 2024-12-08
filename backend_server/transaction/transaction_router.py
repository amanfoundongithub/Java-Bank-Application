from fastapi import APIRouter, Depends
from fastapi.responses import JSONResponse

from transaction.transaction_schema import TransactionBase
from transaction.transaction_routes import addTransaction, confirmTransaction, findAllTransactions
from engine import getDB

from sqlalchemy.orm import Session


transaction_router = APIRouter()

@transaction_router.post("/add_new")
async def add_new_transaction(transaction  : TransactionBase, db : Session = Depends(getDB)):
    try:
        transactionId = addTransaction(transaction, db) 
        return JSONResponse(content = {
            "message" : "OK",
            "id" : transactionId 
        }, status_code = 200)
    
    except ValueError as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 201)
        
    except Exception as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 500)


@transaction_router.get("/verify")
async def verify_transaction_route(transactionId : str, db : Session = Depends(getDB)):
    try: 
        transaction = confirmTransaction(transactionId, db)
        print(transaction) 
        return JSONResponse(transaction, status_code = 200) 
    
    except ValueError as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 201)
        
    except Exception as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 500)
        
@transaction_router.get("/all")
async def get_all_transactions_route(username : str, db : Session = Depends(getDB)):
    try:
        trans = findAllTransactions(username, db) 
        print(trans) 
        return JSONResponse(content = {
            "transactions" : trans 
        }, status_code = 200)
    except Exception as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 500)
