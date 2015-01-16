__author__ = ["ashwineaso"]
from models import User
from mongoengine import DoesNotExist
from settings.exceptions import UserNotFound
import datetime

def createUser(userObj):
	"""
	Create a new userObj
	"""

	if User.objects.get(email = userObj.email) is not None:
		raise UserAlreadyExists

	user = User(
		email = userObj.email,
		name = userObj.name,
		joinDate = datetime.date(),
		password_hash = userObj.password_hash
		)
	
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