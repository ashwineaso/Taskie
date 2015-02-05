from mongoengine import *
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper
from passlib.hash import sha256_crypt
from hashlib import sha512
from uuid import uuid4
import time
from settings.constants import CLIENT_KEY_LENGTH, CLIENT_SECRET_LENGTH,\
	CODE_KEY_LENGTH, ACCESS_TOKEN_LENGTH, REFRESH_TOKEN_LENGTH, ACCESS_TOKEN_EXPIRATION, ACCOUNT_NOT_VERIFIED, ACCOUNT_INVITED_UNREGISTERED, ACCOUNT_ACTIVE

connect()

class TimeStampGenerator(object):
	"""
	Callable Timestamp Generator that returns a UNIX time integer.
	**Kwargs:**

	:: seconds : A integer indicating how many seconds in the future the
	  timestamp should be. *Default 0*
	:: Returns int
	"""
	def __init__(self, seconds = 600):
		self.seconds = seconds

	def __call__(self):
		return int(time.time()) + self.seconds


class KeyGenerator(object):
	"""Callable Key Generator that returns a random keystring.
	**Args:**
	:: length :  A integer indicating how long the key should be.
	:: Returns str
	"""
	def __init__(self, length):
		self.length = length

	def __call__(self):
		return sha512(uuid4().hex).hexdigest()[0:self.length]


class User(Document):
	"""
	User Document
	Holds all the essential information about the user
	"""
	email = StringField(unique = True)
	name = StringField(required = False)
	password_hash = StringField(required = False)
	profilepic = StringField(required = False)
	serverPushId = StringField(required = False)
	status = IntField(default = 0) ## 1 - Active ## 0 - Not Verified ## -1 - Pending registration 
	createdOn = LongField(default = time.time())

	def to_dict(self):
		return mongo_to_dict_helper(self)

	def __unicode__(self):
		return self.email



class Token(Document):
	"""
	Token Document
	Holds all the information about the token of a particular user
	"""
	user = ReferenceField(User, unique = True)
	key = StringField(default = KeyGenerator(CODE_KEY_LENGTH))
	issuedAt = IntField(default = TimeStampGenerator())
	expiresAt = IntField(default = TimeStampGenerator(ACCESS_TOKEN_EXPIRATION))
	access_token = StringField(default = KeyGenerator(ACCESS_TOKEN_LENGTH))
	refresh_token = StringField(default = KeyGenerator(REFRESH_TOKEN_LENGTH))

	def to_dict(self):
		return mongo_to_dict_helper(self)


__author__ = ["ashwineaso"]