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
	if DEBUG:
		app.route('/version', 'GET', version)

	app.route('/<filename:re:.*\.css>', 'GET', stylesheets)
	app.route('/<filename:re:.*\.js>', 'GET', javascripts)
	app.route('/<filename:re:.*\.(jpg|png|gif|ico)>', 'GET', images)
	app.route('/<filename:re:.*\.(eot|ttf|woff|svg)>', 'GET', fonts)

__author__ = ['mahesmohan', 'ashwineaso']