from bottle import request
from settings.altEngine import Collection,RESPONSE_SUCCESS,RESPONSE_FAILED
import bll

response = {}
data = {}
userObj = Collection()
clientObj = Collection()


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


def authorize_user():
	"""
	User login verfication

	"""
	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.password = obj["password"]
		cred_valid_flag = bll.authorize_user(userObj)
		if cred_valid_flag:
			user = bll.getUserByEmail(userObj)
			data["key"] = user.key
			response["status"] = RESPONSE_SUCCESS
			response["data"] = data
		else:
			response["message"] = "Authorization Failed"
			response["status"] = RESPONSE_FAILED
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def issueToken():
	"""
	Issue acceess and refresh token
	"""
	obj = request.json
	try:
		userObj.user_key = obj["key"]
		tokenObj = bll.issueToken(userObj)
		if tokenObj.flag:
			data["acceess_token"] = tokenObj.acceess_token
			data["refresh_token"] = tokenObj.refresh_token
			response["status"] = RESPONSE_SUCCESS
			response["data"] = data
		else:
			response["status"] = RESPONSE_FAILED
			response["message"] = "Authentication Failed"
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return data


def refreshTokens():
	"""
	Using the refresh_token to generate new acceess_token

	"""

	obj = request.json
	try:
		tokenObj.refresh_token = obj["refresh_token"]
		token = bll.refreshTokens(tokenObj)
		data["refresh_token"] = token.refresh_token
		data["expiresAt"] = token.expiresAt
		response["data"] = data
		response["message"] = RESPONSE_SUCCESS
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response


def checkAccessToken(tokenObj):
	"""
	Checking whether the given access_token is valid or not

	"""

	try:
		token = bll.checkAccessTokenValid(tokenObj)
		if token.flag:
			response["status"] = RESPONSE_SUCCESS
			response["message"] = "TOKEN_VALID"
		else:
			response["status"] = RESPONSE_FAILED
			response["message"] = "TOKEN_INVALID"
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = e.message
	return response