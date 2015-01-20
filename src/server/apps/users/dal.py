__author__ = ["ashwineaso"]
from models import *
from mongoengine import DoesNotExist
from settings.exceptions import UserNotFound
import datetime

tokenObj = Collection()

def createUser(userObj):
	"""
	Create a new User
	"""

	if User.objects.get(email = userObj.email) is not None:
		raise UserAlreadyExists

	user = User(
		email = userObj.email,
		name = userObj.name,
		createdOn = datetime.date(),
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


def issueToken(userObj):
	"""
	Issue access and refresh tokens by verifying the user_key
	"""

	try:
		token = Token.objects.get(key = userObj.user_key).first()
		tokenObj.flag = True
		tokenObj.access_token = token.access_token
		tokenObj.refresh_token = token.refresh_token
	except DoesNotExist as e:
		tokenObj.flag = False
	return tokenObj


def refreshTokens(tokenObj):
	"""
	Generate new refresh token and expiration time for access_token
	"""

	token = Token.objects.get(refresh_token = tokenObj.refresh_token)
	token.refresh_token = KeyGenerator(REFRESH_TOKEN_LENGTH)()
	token.issuedAt = TimeStampGenerator()
	token.expiresAt = TimeStampGenerator(ACCESS_TOKEN_EXPIRATION)()
	token.save()
	return token


def checkAccessTokenValid(tokenObj):
	"""
	Check whether the access_token is valid or not
	"""

	tokenObj.flag = True
	token = Token.objects.get(access_token = tokenObj.access_token).first()
	time_now = TimeStampGenerator()
	if time_now > tokenObj.expiresAt:
		tokenObj.flag = False
	return tokenObj
