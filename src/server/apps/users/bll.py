__author__ = ["ashwineaso"]
from passlib.apps import custom_app_context as pwd_context
from . import dal

def createUser(userObj):
	"""
	creating a new userObj
	"""
	userObj.password_hash = hash_password(userObj.password)
	user = dal.createUser(userObj)
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
	if not user.verify_password(userObj.password):
		match_flag = False
	return match_flag


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


def hash_password(password):
	self.password_hash = pwd_context.encrypt(password)
	return password_hash


def verify_password(password):
	return pwd_context.verify(password, self.password_hash)


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


def checkAccessTokenValid(tokenObj):
	"""
	Check whether the access_token is valid or not
	"""
	token = dal.checkAccessTokenValid(tokenObj)
	return token