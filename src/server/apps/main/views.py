from settings.constants import CURRENT_VERSION, DEBUG, PROJECT_ROOT
from settings.altEngine import RESPONSE_SUCCESS, RESPONSE_FAILED
from bottle import Bottle, route, static_file, template, get, TEMPLATE_PATH


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
