USERNAME="SECRET_USERNAME"
PASSWORD="SECRET_PASSWORD"

from flask import Flask
from flask import request

app = Flask("HoE_math_server")

@app.route('/calc',methods=['POST'])
def authRouteHandler():
    user=request.authorization["username"]
    pwd=request.authorization["password"]
    rawData=request.get_data()
    return rawData

app.run(host = '0.0.0.0', port = 8090)