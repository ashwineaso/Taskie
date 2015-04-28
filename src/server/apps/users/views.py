import os
from bottle import request, static_file, template
from settings.altEngine import Collection, RESPONSE_SUCCESS, RESPONSE_FAILED, _decode_list, _decode_dict
from settings.constants import PROJECT_ROOT
import bll
import requests
import json

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
			userObj.authMethod = obj["authMethod"]
		except KeyError as e:
			userObj.authMethod = "taskieAuth"
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


def setServerPushId():
	"""
	Set the serverPushId for the particular user
	"""
	response = {}
	data = {}
	userObj = Collection()

	obj = request.json
	print obj
	try:
		userObj.access_token = obj["access_token"]
		userObj.id = obj["id"]
		userObj.serverPushId = obj["serverPushId"]
		if bll.checkAccessTokenValid(userObj):
			user = bll.setServerPushId(userObj)
		response["data"] = bll.convertUserToDict(user)
		response["status"] = RESPONSE_SUCCESS
	except Exception, e:
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	print response
	return response


def verifyEmail(email, key):
	"""
	Verify email of the user to confirm account

	route(/user/verifyEmail/<email>/<key>)
	"""
	response = {}
	data = {}
	userObj = Collection()
	userObj.email = email
	userObj.key = key
	flag = bll.verifyEmail(userObj)
	if flag is True:
		return "Account has been verified"
	else:
		return "Email and Key mismatch occured"


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
		data["user"] = bll.convertUserToDict(user)
		response["status"] = RESPONSE_SUCCESS
		response["data"] = data
	except Exception as e:
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
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
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
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
		try:
			userObj.authMethod = obj["authMethod"]
		except Exception, e:
			userObj.authMethod = "taskieAuth"
		try:
			userObj.password = obj["password"]
		except KeyError as e:
			userObj.password = ''
		try:
			userObj.name = obj["name"]
		except KeyError as e:
			userObj.name = ''
		cred_valid_flag = bll.authorize_user(userObj)
		if cred_valid_flag:
			userObj.user = bll.getUserByEmail(userObj)
			token = bll.getTokenByUser(userObj)
			data["id"] = str(token.user.id)
			data["name"] = userObj.user.name
			data["email"] = userObj.user.email
			data["key"] = token.key
			data["access_token"] = token.access_token
			data["refresh_token"] = token.refresh_token
			response["status"] = RESPONSE_SUCCESS
			response["data"] = data
		else:
			response["message"] = "Authorization Failed"
			response["status"] = RESPONSE_FAILED
	except Exception as e:
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
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
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
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
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
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
		response['status'] = RESPONSE_FAILED
		response['message'] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response


def syncUserInfo():
	"""
	Suny the basic information of the user like:
	id, name, email and addProfilePic
	"""
	userObj = Collection()
	clientObj = Collection()
	tokenObj = Collection()
	jsonResponse = {}
	
	try:
		jsonObj = request.json
		jsonResponse["status"] = RESPONSE_SUCCESS
	except Exception as e:
		jsonResponse["status"] = RESPONSE_FAILED
	jsonResponse["data"] = []
	userObj.access_token = jsonObj["access_token"]
	for obj in jsonObj["data"]:
		response = {}
		data = {}
		try:
			userObj.email = obj["email"]
			if bll.checkAccessTokenValid(userObj):
				user = bll.syncUserInfo(userObj)
			response["data"] = bll.convertUserToDict(user)
			response["status"] = RESPONSE_SUCCESS
		except Exception as e:
			response['status'] = RESPONSE_FAILED
			response['message'] = str(e)
			if hasattr(e, "code"):
				response["code"] = e.code
		jsonResponse["data"].append(response)
	return jsonResponse


def passwordReset():
	"""Request to reset password made by the user"""
	response = {}
	data = {}
	userObj = Collection()

	obj = request.json
	try:
		userObj.email = obj["email"]
		bll.passwordReset(userObj)
		response["status"] = RESPONSE_SUCCESS
	except Exception as e:
		response["status"] = RESPONSE_FAILED
		response["message"] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
	return response


def updatePassword(email, key):
	"""Request the password update page"""
	userObj = Collection()

	userObj.email = email;
	userObj.key = key;
	userObj.user = bll.getUserByEmail(userObj)
	token = bll.getTokenByUser(userObj)
	return template("password_reset", email = userObj.email, key = token.key)


def doUpdatePassword():
	"""perform password update"""
	userObj = Collection()

	userObj.email = request.forms.get('email')
	userObj.password = request.forms.get('password')
	userObj.key = request.forms.get('key')
	try:
		userObj.user = bll.getUserByEmail(userObj)
		token = bll.getTokenByUser(userObj)
		if (userObj.key == token.key):
			flag = bll.updatePassword(userObj)
			if flag:
				message = "Password has been updated"
			else:
				message = "Password Update Failed. Please Try again"
		else:
			message = "Email and Key mismatch. Please try again"
	except Exception as e:
		message = "Oops! Something went wrong. Please try again"
	return template("updatePasswordResult", message = message)

# Static Routes

def javascripts(filename):
    return static_file(filename, root=PROJECT_ROOT+'/apps/static/js/')

def stylesheets(filename):
	return static_file(filename, root=PROJECT_ROOT+'/apps/static/css/')

def images(filename):
    return static_file(filename, root=PROJECT_ROOT+'/apps/static/images/')
	