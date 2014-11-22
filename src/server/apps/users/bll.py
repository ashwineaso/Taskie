__author__ = ["ashwineaso"]
from . import dal

def createUser(userObj):
	"""
	creating a new userObj
	"""

	user = dal.createUser(userObj)
	return user