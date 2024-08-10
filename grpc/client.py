import grpc
import user_pb2
import user_pb2_grpc

def run():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = user_pb2_grpc.AuthServiceStub(channel)

        username = input("username: ")
        password = input("password: ")

        request = user_pb2.UserCredentials(username=username, password=password)
        try:
            #print("OK")
            response = stub.AuthenticateUser(request)
            #print("IOK32")
            if response.token:
                print(f"Autentificare reusita. Token:\n {response.token}")

                token_req = user_pb2.TokenRequest(token=response.token)
                validate_response = stub.ValidateToken(token_req)
                print(f"Rezultate: User ID = {validate_response.sub}, Role = {validate_response.role}")

                #invalidate_response = stub.RevokeToken(token_req)
                #print(f"Token invalidated: {invalidate_response.success}")
            else:
                print("Autentificare esuata.")

        except grpc.RpcError as e:
            print(f"RPC error: {e}")


if __name__ == '__main__':
    run()