__author__ = ["ashwineaso"]
from models import *
from mongoengine import DoesNotExist
from settings.exceptions import *
import time
from settings.constants import CLIENT_KEY_LENGTH, CLIENT_SECRET_LENGTH,\
    CODE_KEY_LENGTH, ACCESS_TOKEN_LENGTH, REFRESH_TOKEN_LENGTH, ACCESS_TOKEN_EXPIRATION, ACCOUNT_NOT_VERIFIED, ACCOUNT_INVITED_UNREGISTERED, ACCOUNT_ACTIVE
from settings.altEngine import Collection

tokenObj = Collection()

def createUser(userObj):
	"""
	Create a new User

	::type userObj : objects
	::param userObj : An instance of Collection with the following attributes
					email,
					name,
					serverPushId,
					password_hash,
					createdOn
	::return user : An instance of user class
	"""
	try:
		user = User(
			email = userObj.email,
			name = userObj.name,
			createdOn = time.time(),
			password_hash = userObj.password_hash,
			status = ACCOUNT_NOT_VERIFIED,
			serverPushId = userObj.serverPushId
			)
		user.save()
	except Exception:
		person = User.objects.get(email = userObj.email)
		#If status = 1 - User already exists and active
		if person.status == ACCOUNT_ACTIVE:
			raise UserAlreadyExists
		#If status = 0 : User already exists, pending verification
		if person.status == ACCOUNT_NOT_VERIFIED:
			raise UserPendingConfirmation
		#If status = -1 : User invited, but not registered
		if person.status == ACCOUNT_INVITED_UNREGISTERED:
			user = updateUser(userObj)
			return user

	#Save new user to Database
	#Create a new token for the user
	token = Token(user = user)
	token.save()
	return user



def createMinimalUser(userObj):
	"""
	Create a minimal user for sending invites

	::type userObj : object of Collection class
	::parama userObj : object with attributes
						email
	::return user : An instance of User class
	"""

	user = User(
				email = userObj.email,
				status = ACCOUNT_INVITED_UNREGISTERED,
				createdOn = time.time()
				)
	user.save()
	user.reload()
	return user


def updateUser(userObj):
	"""
	Updating User information with new values
	OR 
	Updating Invited user with provided values

	type userObj : objects
	::param userObj : An instance of Collection with the following attributes
					email,
					name,
					serverPushId,
	::return user : An instance of user class
	"""
	user = getUserByEmail(userObj)
	User.objects(id = user.id).update(
										set__email = userObj.email,
										set__name = userObj.name,
										set__serverPushId = userObj.serverPushId
										)
	
	###################################################
	## If User is not yet marked registered, then user was created by invitation
	## Token has to be created explicitly
	###################################################
	if user.status == ACCOUNT_INVITED_UNREGISTERED:
		user.password_hash = userObj.password_hash
		user.status = ACCOUNT_ACTIVE
		token = Token(user = user)
		token.save()

	user.save()
	user.reload()
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
		user = getUserByEmail(userObj)
		token = Token.objects.get(user = user)
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
	try:
		token = Token.objects.get(refresh_token = tokenObj.refresh_token)
	except Exception:
		raise RefreshTokenInvalid
	token.refresh_token = KeyGenerator(REFRESH_TOKEN_LENGTH)()
	token.access_token = KeyGenerator(ACCESS_TOKEN_LENGTH)()
	token.expiresAt = TimeStampGenerator(ACCESS_TOKEN_EXPIRATION)()
	return token


def updatetoken(tokenObj):

	token = Token.objects.get(id = tokenObj.id)
	token.refresh_token = tokenObj.refresh_token
	token.access_token = tokenObj.access_token
	token.expiresAt = tokenObj.expiresAt
	token.save()
	token.reload()
	return token


def checkAccessTokenValid(tokenObj):
	"""
	Check whether the access_token is valid or not
	"""

	tokenObj.flag = True
	token = Token.objects.get(access_token = tokenObj.access_token)
	time_now = time.time()
	if time_now > token.expiresAt:
		raise AccessTokenInvalid
	return tokenObj


def verifyUser(userObj):
	"""
	Verify user by key matching 
	"""
	user = getUserByEmail(userObj)
	token = Token.objects.get(user = user)
	if token.key == userObj.key:
		user.status = ACCOUNT_ACTIVE
		user.save()
		return True
	return False