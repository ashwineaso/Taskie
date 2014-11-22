__author__ = ["ashwineaso"]
from models import User

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
	# except Exception as e:
	# 	raise e