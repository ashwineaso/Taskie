__author__ = ['mahesmohan','ashwineaso']

import os
import threading
import json
from mongoengine import *
import requests
from requests.adapters import HTTPAdapter
from requests.packages.urllib3.poolmanager import PoolManager
import ssl

#Database name
def getDatabase():
	if DEBUG :
		return "taskappdebug" 
	else:
		return "taskapp"


#Project Information
PROJECT_ROOT = os.path.realpath(os.path.dirname(os.path.dirname(__file__)))
PHOTOS_DEBUG_DIRECTORY = 'storage/photos_debug/'
PHOTOS_DIRECTORY = 'storage/photos/'
ATTACHMENT_DIRECTORY = 'storage/attachment/'

#User Account status flags
ACCOUNT_NOT_VERIFIED = 0
ACCOUNT_INVITED_UNREGISTERED = -1
ACCOUNT_ACTIVE = 1

#Priority Flags
LOW_PRIORITY = 0
NORMAL_PRIORITY = 1
HIGH_PRIORITY = 2

#Variable required for OAuth2 2-Legged Verification
CLIENT_KEY_LENGTH = 30
CLIENT_SECRET_LENGTH = 30
CODE_KEY_LENGTH = 30
ACCESS_TOKEN_EXPIRATION = 3600000
ACCESS_TOKEN_LENGTH = 10
REFRESH_TOKEN_LENGTH = 10

#GCM API Keys
GCM_KEY = 'AIzaSyCNoXZ78VPIMmiGJ63N2j78zbtbPxB51w4'
TOKEN_GCM_REGISTRATION_IDS = 'registration_ids'

#GCM 
class GCMPost(object):
	"""docstring for GCMPost"""
	def __init__(
				self,
				payload = {},
				url = 'https://android.googleapis.com/gcm/send',
				contentType = "application/json",
				authorization = "key="+GCM_KEY
				):
		self.url = url
		self.headers = {}
		self.headers['content-type'] = contentType
		self.headers['authorization'] = authorization
		self.payload = payload


class MyAdapter(HTTPAdapter):
    def init_poolmanager(self, connections, maxsize, block=False):
        self.poolmanager = PoolManager(num_pools=connections,
                                       maxsize=maxsize,
                                       block=block,
                                       ssl_version=ssl.PROTOCOL_TLSv1)

##################################
## Defining a class and functions
## to initiate a thread
##################################

class UrlPostThread(threading.Thread):
	def __init__(self, threadID, name, postObj):
		threading.Thread.__init__(self)
		self.threadID = threadID
		self.name = name
		self.postObj = postObj

	def run(self):
		# Make a postRequest from the postObj
		s = requests.Session()
		s.mount('https://android.googleapis.com/', MyAdapter())

		response = requests.post(
			self.postObj.url,
			data=json.dumps(self.postObj.payload)
			#headers=self.postObj.headers
		)
		self.response = response
		print(response.text)
		if response.ok:
			print 'request:', self.postObj.payload
			print self.name+': POST is success.'
			print 'content:', response.content
		else:
			print self.name+': POST failed.'

#Current version of the API
CURRENT_VERSION = 0.1

#Debig status of the API
DEBUG = True
