__author__ = ["ashwineaso"]
from models import *
from mongoengine import DoesNotExist
from settings.exceptions import *
import datetime
from settings.constants import CLIENT_KEY_LENGTH, CLIENT_SECRET_LENGTH,\
    CODE_KEY_LENGTH, ACCESS_TOKEN_LENGTH, REFRESH_TOKEN_LENGTH, ACCESS_TOKEN_EXPIRATION
from settings.altEngine import Collection

tokenObj = Collection()

def createUser(userObj):
	"""
	Create a new User
	"""

	
	for users in User.objects:
		if users.email == userObj.email:
			raise UserAlreadyExists

	user = User(
		email = userObj.email,
		name = userObj.name,
		createdOn = datetime.datetime.now(),
		password_hash = userObj.password_hash
		)
	
	user.save()
	token = Token(user = user)
	token.save()
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
		user = User.objects.get(email = userObj.email)
		print user.email
		return user 
	except DoesNotExist as e:
		raise UserNotFound


def getTokenByUser(userObj):
	"""
	Find a user's token
	"""

	try:
		token = Token.objects.get(user = userObj.user)
		return token
	except Exception as e:
		raise TokenNotFound


def issueToken(userObj):
	"""
	Issue access and refresh tokens by verifying the user_key
	"""

	try:
		token = Token.objects.get(key = userObj.key)
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
	# token.issuedAt = TimeStampGenerator(10)
	token.expiresAt = TimeStampGenerator(ACCESS_TOKEN_EXPIRATION)()
	return token


def updatetoken(tokenObj):

	token = Token.objects.get(id = tokenObj.id)
	token.refresh_token = tokenObj.refresh_token
	# token.issuedAt = tokenObj.issuedAt
	token.expiresAt = tokenObj.expiresAt
	token.save()


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
