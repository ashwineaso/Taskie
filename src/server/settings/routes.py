from apps.main.views import *
from settings.constants import DEBUG
from apps.users.routes import users_app
from apps.task.routes import task_app

def set(app):
	app.route('/version/', ['GET', 'POST'], version)
	app.mount('/user/', users_app)
	app.mount('/task/', task_app)
	if DEBUG:
		app.route('/', 'GET', version)

__author__ = ['mahesmohan', 'ashwineaso']