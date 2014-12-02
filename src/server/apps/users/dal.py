__author__ = ["ashwineaso"]
from models import User
from mongoengine import DoesNotExist
from settings.exceptions import UserNotFound

def createUser(userObj):
	"""
	Create a new userObj
	"""

	user = User(
		email = userObj.email,
		name = userObj.name
		)
	# try:
	user.save()
	return user

def getUserByEmail(userObj):
	"""
	Finds a user by their email

	:type userObj: object
	:param userObj: An instance with the following attribute(s)
		email
	:return user: An instance of User class
	"""

	try:
		user = User.objects(email = userObj.email).get()
		return user 
	except DoesNotExist as e:
		raise UserNotFound