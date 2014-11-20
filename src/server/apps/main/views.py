from settings.constants import CURRENT_VERSION, DEBUG
from settings.altEngine import RESPONSE_SUCCESS, RESPONSE_FAILED

def version():
	"""
	Responds with the current version and debug status of the API
	"""
	response = {}
	data = {}
	try:
		data['version'] = CURRENT_VERSION
		data['debug'] = DEBUG
		response['status'] = RESPONSE_SUCCESS
		response['data'] = data
	except Exception as e:
		response['message'] = str(e)
		response['status'] = RESPONSE_FAILED
	return response