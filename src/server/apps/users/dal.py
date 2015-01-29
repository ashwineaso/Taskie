__author__ = ["ashwineaso"]
from models import *
from mongoengine import DoesNotExist
from settings.exceptions import *
import time
from settings.constants import CLIENT_KEY_LENGTH, CLIENT_SECRET_LENGTH,\
    CODE_KEY_LENGTH, ACCESS_TOKEN_LENGTH, REFRESH_TOKEN_LENGTH, ACCESS_TOKEN_EXPIRATION, \
    ACCOUNT_NOT_VERIFIED, ACCOUNT_INVITED_UNREGISTERED, ACCOUNT_ACTIVE
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

	#Check if the user already exists
	for person in User.objects:
		if person.email == userObj.email:
			#If user's email is resitered check status
			#If status = 1 - User already exists and active
			if person.status == ACCOUNT_ACTIVE:
				raise UserAlreadyExists
			#If status = 0 : User already exists, pending verification
			elif person.status == ACCOUNT_NOT_VERIFIED:
				raise UserPendingConfirmation
			#If status = -1 : User invited, but not registered
			elif person.status == ACCOUNT_INVITED_UNREGISTERED:
				user = dal.updateUser(userObj)
				return user

	#Create a new user object with the provided information
	user = User(
		email = userObj.email,
		name = userObj.name,
		createdOn = time.time(),
		password_hash = userObj.password_hash,
		status = ACCOUNT_NOT_VERIFIED,
		serverPushId = userObj.serverPushId
		)
	
	#Save new user to Database
	user.save()
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
	target_user = getUserByEmail(userObj)
	user = User.objects(id = target_user.id).update(
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
