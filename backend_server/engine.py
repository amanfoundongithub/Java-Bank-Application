from sqlalchemy import create_engine, Engine

from sqlalchemy.ext.declarative import declarative_base

from sqlalchemy.orm import sessionmaker


USERNAME = "admin"
PASSWORD = "admin"

DATABASE = "bank_service"

URL = f"postgresql://{USERNAME}:{PASSWORD}@localhost/{DATABASE}"


def createEngine():
    return create_engine(url = URL) 

def createLocalSession(engine : Engine):
    return sessionmaker(
        bind = engine,
        autoflush = False, 
        autocommit = False
    )
    
    
# Engine
engine = createEngine()

# Session 
session = createLocalSession(engine = engine) 

Base = declarative_base()
Base.metadata.create_all(bind = engine)

def getDB():
    db = session()
    try: 
        yield db 
    except Exception as e:
        print(f"Error : {e}")
        db.close() 



