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


def user_login(userObj):
	"""
	User login by verifying the password

	:type userObj : object
	:param userObj : An instance with the following attribute
					email
					password
	:return userObj : with extra attribute
						response
	"""
	user = getUserByEmail(userObj)
	if not user.verify_password(userObj.password):
		raise PasswordMismatch
	else:
		userObj.response = "LogIn Successful"
	return userObj


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