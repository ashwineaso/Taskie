import apps
from apps.main.views import *

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
	app.route('/css/<filename:path>', 'GET', stylesheets)
	app.route('/js/<filename:path>', 'GET', javascripts)
	app.route('/images/<filename:path>', 'GET', images)
	if DEBUG:
		app.route('/version', 'GET', version)

__author__ = ['mahesmohan', 'ashwineaso']