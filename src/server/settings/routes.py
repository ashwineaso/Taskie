from apps.main.views import *
from settings.constants import DEBUG

def set(app):
	app.route('/version/', ['GET', 'POST'], version)
	if DEBUG:
		app.route('/', 'GET', version)

__author__ = 'mahesmohan'