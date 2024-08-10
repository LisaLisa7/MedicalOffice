import futures3
import grpc
from flask import Flask, request, jsonify
from flask_cors import CORS
import jwt
from datetime import datetime, timedelta
import uuid
import user_pb2_grpc
import user_pb2
from idm_bd import User,RevokedToken
from bd import init_db, SessionLocal


init_db()

SECRET_KEY = "keyyy"

app = Flask(__name__)
CORS(app)

def is_valid_user(username, password):
    return True

def check_user_credentials(username, password):
    session = SessionLocal()
    try:
        user = session.query(User).filter_by(username=username).first()

        if user and user.password == password:
            return user

    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        session.close()

    return None

def generate_jwt(user_id, username, role):
    expiration_time = datetime.utcnow() + timedelta(hours=2)
    jti = str(uuid.uuid4())
    payload = {
        'sub': str(user_id),  # ID-ul utilizatorului din baza de date
        'username': username,  # Numele de utilizator
        'role': role,  # Rolul utilizatorului
        'exp': expiration_time,
        'jti': jti
    }
    token = jwt.encode(payload, SECRET_KEY, algorithm='HS256')
    return token

revoked_tokens = set()

def revoke_jwt(token):
    try:
        decoded = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
        jti = decoded['jti']
        expiration = datetime.utcfromtimestamp(decoded['exp'])
        revoked_tokens.add(jti)
        session = SessionLocal()
        try:
            revoked_token = RevokedToken(jti=jti, expirationDate=expiration)
            session.add(revoked_token)
            session.commit()
        except Exception as e:
            session.rollback()
            print(f"An error occurred: {e}")
        finally:
            session.close()


    except jwt.InvalidTokenError:
        return "Token invalid"
    return "Success"

def validate_jwt(token):
    session = SessionLocal()

    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=['HS256'])
        jti = payload['jti']
        jti_database = session.query(RevokedToken).filter_by(jti=jti).first()


        if jti_database:
            return "Token respins"
        return payload
    except jwt.ExpiredSignatureError:
        return "Token expirat"
    except jwt.InvalidTokenError:
        return "Token invalid"

class ServiceLog(user_pb2_grpc.AuthServiceServicer):

    def AuthenticateUser(self, request, context):
        username = request.username
        password = request.password
        print("sunt in server.py")
        user = check_user_credentials(username, password)

        if user:

            token = generate_jwt(user_id=user.uid, username=user.username, role=user.role)
            print(token)
            return user_pb2.TokenResponse(token=token)

        else:
            context.set_code(grpc.StatusCode.UNAUTHENTICATED)
            context.set_details('Username sau parola gresite.')
            print("wrong creds")
            return user_pb2.TokenResponse(token="")

    def ValidateToken(self, request, context):
        token = request.token
        validation_result = validate_jwt(token)
        if isinstance(validation_result, dict):
            return user_pb2.TokenInfo(sub=validation_result['sub'], role=validation_result['role'])
        else:
            context.set_code(grpc.StatusCode.UNAUTHENTICATED)
            context.set_details(validation_result)
            return user_pb2.TokenInfo(sub="", role="")

    def RevokeToken(self, request, context):
        token = request.token
        result = revoke_jwt(token)
        if result == "Success":
            return user_pb2.TokenRevokeResponse(success=True)
        else:
            context.set_code(grpc.StatusCode.UNAUTHENTICATED)
            context.set_details(result)
            return user_pb2.TokenRevokeResponse(success=False)



def serv():
    app = Flask(__name__)
    CORS(app)

    sr = grpc.server(futures3.ThreadPoolExecutor(max_workers=10))

    user_pb2_grpc.add_AuthServiceServicer_to_server(ServiceLog(), sr)
    sr.add_insecure_port('[::]:50051')



    sr.start()
    #app.run(port=5000)
    print("Serverul a pornit ...")
    sr.wait_for_termination()




if __name__ == "__main__":
    serv()

    '''flask_thread = threading.Thread(target=run_flask)
    grpc_thread = threading.Thread(target=run_grpc)

    flask_thread.start()
    grpc_thread.start()

    flask_thread.join()
    grpc_thread.join()'''
