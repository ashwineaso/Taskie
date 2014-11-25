__author__ = ["ashwineaso"]
from . import dal

def createUser(userObj):
	"""
	creating a new userObj
	"""

	user = dal.createUser(userObj)
	return user

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