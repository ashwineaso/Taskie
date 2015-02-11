import os
from bottle import request, static_file
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED
import bll
import requests


def register():
	"""
	View for user registration

	route(/user/register)

	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()

	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.name = obj["name"]
		userObj.password = obj["password"]
		try:
			userObj.serverPushId = obj["serverPushId"]
		except KeyError as e:
			userObj.serverPushId = ''
		user = bll.createUser(userObj)
		response['status'] = RESPONSE_SUCCESS
		response['data']  = bll.convertUserToDict(user)
	except Exception as e:
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response



def verifyEmail(email, key):
	"""
	Verify email of the user to confirm account

	route(/user/verifyEmail/<email>/<key>)
	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()
	userObj.email = email
	userObj.key = key
	bll.verifyEmail(userObj)


def updateUser():
	"""
	Updating user information

	route(/user/updateUser)
	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()
	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.name = obj["name"]
		userObj.key = obj["key"]
		try:
			userObj.serverPushId = obj["serverPushId"]
		except KeyError as e:
			userObj.serverPushId = ''
		user = bll.updateUser(userObj)
		data["user"] = user.to_dict()
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		response["code"] = e.code
	return response


def modifyProfilePic():
	"""
	Add a profile pic to the user account
	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()

	photoObj = Collection()
	img = request.files.get('image')
	print img
	try:
		photoObj.filename, photoObj.extension = os.path.splitext(img.filename)
		photoObj.image = img
		photoObj.contentType = request.forms.get('contentType')
		photoObj.acceess_token = requests.headers.get('Authorization')
		photoObj.id = requests.headers.get('User-ID')
		bll.addProfilePic(photoObj)
		response["status"] = RESPONSE_SUCCESS
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		response["code"] = e.code
	return response


def authorize_user():
	"""
	User Login using username and password is verified

	route(/user/authorize)

	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()

	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.password = obj["password"]
		cred_valid_flag = bll.authorize_user(userObj)
		if cred_valid_flag:
			userObj.user = bll.getUserByEmail(userObj)
			token = bll.getTokenByUser(userObj)
			data["key"] = token.key
			data["access_token"] = token.access_token
			data["refresh_token"] = token.refresh_token
			response["status"] = RESPONSE_SUCCESS
			response["data"] = data
		else:
			response["message"] = "Authorization Failed"
			response["status"] = RESPONSE_FAILED
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
	return response


# def issueToken():
# 	"""
# 	Issue acceess and refresh token
# 	"""
# 	obj = request.json
# 	try:
# 		userObj.email = obj["email"]
# 		userObj.key = obj["key"]
# 		tokenObj = bll.issueToken(userObj)
# 		if tokenObj.flag:
# 			data["access_token"] = tokenObj.access_token
# 			data["refresh_token"] = tokenObj.refresh_token
# 			response["status"] = RESPONSE_SUCCESS
# 			response["data"] = data
# 		else:
# 			response["status"] = RESPONSE_FAILED
# 			response["message"] = "Authentication Failed"
# 	except Exception as e:
# 		response["status"] = RESPONSE_FAILED
# 		response["message"] = str(e)
# 	return response



def refreshTokens():
	"""
	Using the refresh_token to generate new acceess_token

	route(/user/refreshTokens)

	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()

	obj = request.json
	try:
		tokenObj.refresh_token = obj["refresh_token"]
		new_token = bll.refreshTokens(tokenObj)
		token = bll.updatetoken(new_token)
		data["refresh_token"] = token.refresh_token
		data["access_token"] = token.access_token
		data["expiresAt"] = token.expiresAt
		response["data"] = data
		response["message"] = RESPONSE_SUCCESS
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		response["code"] = e.code
	return response


def checkAccessToken(tokenObj):
	"""
	Checking whether the given access_token is valid or not

	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()

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
		response["message"] = str(e)
		response["code"] = e.code
	return response


def verifyUser():
	"""
	REDUNDANT
	Verifying the user from the confirmation mail sent using POST method

	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()

	obj = request.json
	try:
		userObj.email = obj["email"]
		userObj.key = obj["key"]
		flag = bll.verifyUser(userObj)
		if flag:
			response["status"] = RESPONSE_SUCCESS
			response["message"] = "Verification Sucessful"
		else:
			response["status"] = RESPONSE_FAILED
			response["message"] = "Verification Unsucessful"
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
	return response


def syncUserInfo():
	"""
	Suny the basic information of the user like:
	id, name, email and addProfilePic
	"""
	response = {}
	data = {}
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()
	
	obj = request.json
	try:
		userObj.access_token = ["acceess_token"]
		userObj.id = obj["id"]
		if dal.checkAccessTokenValid(userObj):
			user = bll.syncUserInfo(userObj)
		response["data"] = user.to_dict
		response["status"] = RESPONSE_SUCCESS
	except Exception, e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
	return response