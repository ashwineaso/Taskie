from bottle import request
from settings.altEngine import Collection,RESPONSE_SUCCESS,RESPONSE_FAILED
import bll

response = {}
data = {}
userObj = Collection()


def register():
	"""
	View for user registration

	"""
	

	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.name = obj["name"]
		userObj.password = obj["password"]
		user = bll.createUser(userObj)
		data['user'] = user.to_dict()
		response['status'] = RESPONSE_SUCCESS
		response['data'] = data
	except Exception as e:
	 	response['status'] = RESPONSE_FAILED
	 	response['message'] = e.message
	return response


def user_login():
	"""
	User login verfication

	"""
	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.password = obj["password"]
		user = bll.user_login(userObj)
		data["response"] = user.response
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response