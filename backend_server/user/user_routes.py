from user.user_schema import User, UserDetails, UserAuthenticate

import uuid 
import bcrypt


from sqlalchemy.orm import Session

salt = bcrypt.gensalt()

def encrypt(password : str) -> str:
    return bcrypt.hashpw(password.encode("utf-8"), salt).decode("utf-8")

def verify(candidate : str, actual : str) -> bool:
    return bcrypt.checkpw(candidate.encode("utf-8"), actual.encode("utf-8"))

def addUser(user : UserDetails, db : Session):
    # Email
    exisiting_email = db.query(User).filter(
        (User.email == user.email)
    ).first()
    if exisiting_email:
        raise ValueError(
            "Email already exists, choose another one"
        )
    
    # Username
    existing_username = db.query(User).filter(
        (User.username == user.username)
    ).first() 
    if existing_username:
        raise ValueError(
            "Username already exists, choose another one"
        )
    
    # Create User
    if not user.id:
        user.id = str(uuid.uuid4())
    
    user.password = encrypt(user.password)
    
    db_user = User(
        id=user.id,
        email=user.email,
        password=user.password,
        username=user.username,
        name=user.name,
        balance=user.balance,
        phone=user.phone,
        premium=user.premium
    )
    
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    
    return user.id 
    

def authenticateUser(user : UserAuthenticate, db : Session):
    # Query user
    db_user = db.query(User).filter(User.email == user.email).first()
    
    if db_user is None: 
        raise ValueError(
            "Email does not exist!"
        )
        
    if verify(user.password, db_user.password):
        return db_user.id 
    else: 
        raise ValueError(
            "Incorrect Password!"
        )
    
def checkUserNameInDB(username : str, db : Session):
    db_user = db.query(User).filter(User.username == username).first()
    
    if db_user:
        return True 
    else: 
        return False 
    

def fetchUserDetails(id : str, db : Session):
    db_user = db.query(User).filter(User.id == id).first()
    
    if db_user is None:
        raise ValueError(
            "User ID not found in database"
        )
    
    return {
        "id": db_user.id,
        "email": db_user.email,
        "username": db_user.username,
        "name": db_user.name,
        "balance": db_user.balance,
        "phone": db_user.phone,
        "premium": db_user.premium,
        "created_at": db_user.created_at.isoformat() 
    }
    
    