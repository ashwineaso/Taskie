__author__ = ["ashwineaso"]
from passlib.hash import sha256_crypt as pwd_context
from . import dal
from settings.exceptions import AuthenticationError
from settings.constants import PROJECT_ROOT

def createUser(userObj):
	"""
	Creating a new User

	::type userObj : object
	::param userObj : An instance of Collection with the following attributes
					email,
					name,
					serverPushId,
					password_hash,
					createdOn
	::return user : An instance of user class
	"""

	userObj.password_hash = hash_password(userObj.password)
	user = dal.createUser(userObj)
	return user


def createAndInvite(userObj):
	"""
	User creation by invite during task creation
	::type userObj : object
	::param userObj : An instance of Collection with the following attributes
						email
	::return user : An intance of the User class
	"""

	user = dal.createMinimalUser(userObj)
	## Send a personalise invite to the user's email
	sendInvite(user)
	return user


def verifyUser(userObj):
	"""
	Verify the user account using the user id and key

	::type userObj : object
	::param userObj : An instance of Collection with the following attributes
						email,
						key,
	::return flag : True if verified and False if not
	"""
	verified = dal.verifyUser(userObj)
	return verified


def modifyProfilePic(photoObj):
	"""
	Adds a profile pic to the user's account

	::type photoObj: object
	::param photoObj: An object with the following attributes
					filename,
					extension,
					image,
					uuid,
	::return : An object of the photo class
	"""
	
	# Get the user associated with 
	photoObj.user = userBll.getUserById(photoObj)
	# Save the photo to folder and add path to user'd db
	dal.addProfilePic(photoObj)


def updateUser(userObj):
	"""
	Updating User information with new values

	type userObj : objects
	::param userObj : An instance of Collection with the following attributes
					email,
					name,
					serverPushId,
	::return user : An instance of user class
	"""
	if (authenticate(userObj)):
		user = dal.updateUser(userObj)
	return user


def authorize_user(userObj):
	"""
	User authorization by verifying the password

	:type userObj : object
	:param userObj : An instance with the following attribute
					email
					password
	:return userObj : with extra attribute
						response
	"""
	match_flag = True
	user = getUserByEmail(userObj)
	if not verify_password(userObj.password, user.password_hash):
		match_flag = False
	return match_flag


def authenticate(userObj):
	"""
	Authenticate user by email and key matching
	"""
	user = getUserByEmail(userObj)
	userObj.user = user
	token = getTokenByUser(userObj)
	if token.key == userObj.key:
		return True
	raise AuthenticationError


def syncUserInfo(userObj):
	"""
	Sync minimal user information such as id, name, email and profile pic
	"""

	user = dal.syncUserInfo(userObj)
	return user


def issueToken(userObj):
	"""
	Issue access and refresh tokens by confirming user key
	"""
	token = dal.issueToken(userObj)
	return token


def refreshTokens(tokenObj):
	"""
	Generate new refresh token and expiration time for access tokenObj
	"""
	token = dal.refreshTokens(tokenObj)
	return token


def updatetoken(tokenObj):
	token = dal.updatetoken(tokenObj)
	return token


def hash_password(password):
	password_hash = pwd_context.encrypt(password)
	return password_hash


def verify_password(password, password_hash):
	return pwd_context.verify(password, password_hash)


def getUserByEmail(userObj):
	"""
	Find a user by email id
	
	:type userObj: object
	:pram userObj: An instance with the following attribute(s)
		email
	:return: An object of User class
	"""

	user = dal.getUserByEmail(userObj)
	return user


def getUserById(userObj):
	"""
	Find a user by id
	
	::type userObj: object
	::parm userObj: An instance with the following attributes
					id
	::return : An object of User class
	"""
	user = dal.getUserById(userObj)
	return user


def getTokenByUser(userObj):
	"""
	Find the user's token
	"""
	token = dal.getTokenByUser(userObj)
	return token


def checkAccessTokenValid(tokenObj):
	"""
	Check whether the access_token is valid or not
	"""
	token = dal.checkAccessTokenValid(tokenObj)
	return token