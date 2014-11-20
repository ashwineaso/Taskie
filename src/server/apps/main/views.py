from settings.constants import CURRENT_VERSION, DEBUG
from settings.altEngine import RESPONSE_SUCCESS, RESPONSE_FAILED

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