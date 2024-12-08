from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from user.user_router import user_router
from transaction.transaction_router import transaction_router

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allow all origins 
    allow_credentials=True,
    allow_methods=["*"],  # Allow all HTTP methods
    allow_headers=["*"],  # Allow all headers
)

app.include_router(user_router, prefix = '/user', tags = ["user"])
app.include_router(transaction_router, prefix = "/transaction", tags = ["transaction"])

