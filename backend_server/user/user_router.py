from fastapi import APIRouter, HTTPException, Depends
from fastapi.responses import JSONResponse

from user.user_schema import UserDetails, UserAuthenticate

from user.user_routes import addUser, authenticateUser, fetchUserDetails, checkUserNameInDB

from engine import getDB

from sqlalchemy.orm import Session


user_router = APIRouter()

@user_router.post("/create")
async def create_user_details(user : UserDetails, db : Session = Depends(getDB)):
    try:
        id = addUser(user, db)
        return JSONResponse(content = {
            "id" : id,
            "message" : "OK"
        }, status_code = 200) 
    
    except ValueError as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 201)
        
    except Exception as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : e
        }, status_code = 500)
        
@user_router.post("/authenticate")
async def authenticate_user_route(user : UserAuthenticate, db : Session = Depends(getDB)):
    try:
        id = authenticateUser(user,db)
        return JSONResponse(content = {
            "message" : "OK",
            "id" : id 
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


@user_router.get("/details")
async def get_user_details_route(id : str, db : Session = Depends(getDB)):
    try:
        details = fetchUserDetails(id, db) 
        return JSONResponse(content = {
            "message" : "OK",
            "details" : details
        }, status_code = 200) 
    
    except ValueError as e:
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 404)
        
    except Exception as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 500)
        
@user_router.get("/username_in_db")
async def get_username_in_db(username : str, db : Session = Depends(getDB)):
    try:
        check = checkUserNameInDB(username, db) 
        return JSONResponse(content = {
            "message" : "OK",
            "exist" : check
        }, status_code = 200) 
        
    except Exception as e:
        print(f"Error : {e}")
        return JSONResponse(content = {
            "message" : str(e)
        }, status_code = 500)
        

