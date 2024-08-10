from flask import Flask, request, jsonify
from flask_cors import CORS
import grpc
import user_pb2_grpc
import user_pb2

app = Flask(__name__)
CORS(app)

def grpc_authenticate_user(username, password):
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = user_pb2_grpc.AuthServiceStub(channel)
        response = stub.AuthenticateUser(user_pb2.UserCredentials(username=username, password=password))

        request = user_pb2.TokenRequest(token=response.token)
        #invalidate_response = stub.RevokeToken(request)
        #print(f"Token invalidated: {invalidate_response.success}")

        try:
            # Call the ValidateToken method
            valid_response = stub.ValidateToken(request)
            print(f"Token is valid for user: {valid_response.sub}, role: {valid_response.role}")
        except grpc.RpcError as e:
            # Handle the case where the token is not valid
            print(f"Validation failed: {e.details()}")
            return {e.details()}


        return response.token

def grpc_validate_user(token):
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = user_pb2_grpc.AuthServiceStub(channel)
        request = user_pb2.TokenRequest(token=token)
        try:
            valid_response = stub.ValidateToken(request)
            print(f"Token is valid for user: {valid_response.sub}, role: {valid_response.role}")
            return True
        except grpc.RpcError as e:
            print(f"Validation failed: {e.details()}")
            return False


@app.route('/login', methods=['POST'])
def login():
    data = request.json
    username = data.get('username')
    password = data.get('password')
    if not username or not password:
        return jsonify({'error': 'Missing username or password'}), 400

    try:
        token = grpc_authenticate_user(username, password)
        return jsonify({'token': token})
    except grpc.RpcError as e:
        return jsonify({'error': e.details()}), 401

@app.route('/validate',methods=['POST'])
def validateToken():
    data = request.json
    print(data)
    token = data.get("authToken")
    if not token:
        return jsonify({"error": "Token is required"}), 400

    is_valid = grpc_validate_user(token)

    if is_valid:
        return jsonify({"valid": True}), 200
    else:
        return jsonify({"valid": False}), 401






if __name__ == "__main__":
    app.run(port=5000)
