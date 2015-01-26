#Database name
def getDatabase():
	if DEBUG :
		return "taskappdebug" 
	else:
		return "taskapp"

#Variable required for OAuth2 2-Legged Verification
CLIENT_KEY_LENGTH = 30
CLIENT_SECRET_LENGTH = 30
CODE_KEY_LENGTH = 30
ACCESS_TOKEN_EXPIRATION = 3600
ACCESS_TOKEN_LENGTH = 10
REFRESH_TOKEN_LENGTH = 10

#GCM API Keys
GCM_KEY = 'AIzaSyBhHrBolPT-AMAuIpEs6dm8VUbonQKyItA'
TOKEN_GCM_REGISTRATION_IDS = 'registration_ids'

#GCM 
class GCMPost(object):
	"""docstring for GCMPost"""
	def __init__(
				self,
				payload = {}
				url = 'https://android.googleapis.com/gcm/send'
				contentType = "application/json"
				authorization = GCM_KEY
				):
		self.url = url
		self.headers = {}
		self.headers['content-type'] = contentType
		self.headers['authorization'] = authorization
		self.payload = payload
			

#Current version of the API
CURRENT_VERSION = 0.1

#Debig status of the API
DEBUG = True

__author__ = ['mahesmohan','ashwineaso']