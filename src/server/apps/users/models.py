from mongoengine import *
from settings.methods import connect
from settings.altEngine import mongo_to_dict_helper
from passlib.hash import sha512
from uuid import uuid4
import time
from settings.constants import CLIENT_KEY_LENGTH, CLIENT_SECRET_LENGTH,\
    CODE_KEY_LENGTH, ACCESS_TOKEN_LENGTH, REFRESH_TOKEN_LENGTH, ACCESS_TOKEN_EXPIRATION

connect()

class TimestampGenerator(object):
    """
    Callable Timestamp Generator that returns a UNIX time integer.
    **Kwargs:**

    :: seconds : A integer indicating how many seconds in the future the
      timestamp should be. *Default 0*
    :: Returns int
    """
    def __init__(self, seconds=10):
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
	email = EmailField()
	name = StringField()
	createdOn = DateField()
	key = StringField(default = KeyGenerator(CODE_KEY_LENGTH))
	issuedAt = IntField(default = TimeStampGenerator())
	expiresAt = IntField(default = TimeStampGenerator(ACCESS_TOKEN_EXPIRATION))
	access_token = StringField(default = KeyGenerator(ACCESS_TOKEN_LENGTH))
	refresh_token = StringField(default = KeyGenerator(REFRESH_TOKEN_LENGTH))


	def to_dict(self):
		return mongo_to_dict_helper(self)


class Client(Document):
	"""
	Stores the client authentication data

	**Args**
	::name : Stores the name of the client
	::user : References the user linked
	"""
	name = StringField(required = True)
	key = StringField(default=KeyGenerator(CLIENT_KEY_LENGTH))
	secret = StringField(default=KeyGenerator(CLIENT_SECRET_LENGTH))

	def to_dict(self):
		return mongo_to_dict_helper(self)


__author__ = ["ashwineaso"]