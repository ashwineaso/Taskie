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


#Current version of the API
CURRENT_VERSION = 0.1

#Debig status of the API
DEBUG = True

__author__ = ['mahesmohan','ashwineaso']