from bottle import request
from settings.altEngine import Collection,RESPONSE_SUCCESS,RESPONSE_FAILED
import bll

def register():
	"""
	View for user registration

	"""
	response = {}
	data = {}

	obj = request.json
	userObj = Collection()

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