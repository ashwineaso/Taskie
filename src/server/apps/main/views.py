from settings.constants import CURRENT_VERSION, DEBUG, PROJECT_ROOT, APP_CURRENT_VERSION, UPDATE_REQUIRED
from settings.altEngine import RESPONSE_SUCCESS, RESPONSE_FAILED
from settings.exceptions import AppVersionDepreciated
from bottle import Bottle, route, static_file, template, get, TEMPLATE_PATH
from bottle import request
import json


def version():
	"""
	Responds with the current version and debug status of the API
	"""
	response = {}
	data = {}
	try:
		# Sets data.
		data['version'] = CURRENT_VERSION
		data['debug'] = DEBUG
		# Sets response if no Exception occurs.
		response['status'] = RESPONSE_SUCCESS
		response['data'] = data
	except Exception as e:
		# Sets response if exception occurs.
		response['message'] = str(e)
		response['status'] = RESPONSE_FAILED
	# Returns the response.
	return response


def appVersionCheck():
	""" Check the version of the app on the users device and suggest update"""
	response = {}

	obj = request.json
	try:
		device_version = obj["appVersionCode"]
		if UPDATE_REQUIRED == true and APP_CURRENT_VERSION > device_version:
			response["status"] = RESPONSE_FAILED
			response["code"] = 0010
			raise AppVersionDepreciated
		else:
			response["status"] = RESPONSE_SUCCESS
	except Exception as e:
		response['message'] = str(e)
		if hasattr(e, "code"):
			response["code"] = e.code
		response["status"] = RESPONSE_FAILED
	return response


def index(filename = 'index'):
	"""
	Display the landing page
	"""
	return template("index")

# Static Routes

def javascripts(filename):
    return static_file(filename, root=PROJECT_ROOT+'/apps/static/js/')

def stylesheets(filename):
	return static_file(filename, root=PROJECT_ROOT+'/apps/static/css/')

def images(filename):
    return static_file(filename, root=PROJECT_ROOT+'/apps/static/images/')
