from apps.main.views import *
from apps.users.views import *
from settings.constants import DEBUG
from apps.users.routes import users_app
from apps.task.routes import task_app
from apps.group.routes import group_app

def set(app):
	app.route('/version/', ['GET', 'POST'], version)
	app.mount('/user/', users_app)
	app.mount('/task/', task_app)
	app.mount('/group/', group_app)
	app.route('/', 'GET', index)
	if DEBUG:
		app.route('/version', 'GET', version)

__author__ = ['mahesmohan', 'ashwineaso']