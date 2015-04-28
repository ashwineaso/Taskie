__author__ = ['mahesmohan', 'ashwineaso']

import sys
import os

from settings.constants import DEBUG, PROJECT_ROOT, PHOTOS_DIRECTORY, PHOTOS_DEBUG_DIRECTORY, ATTACHMENT_DIRECTORY

from bottle import Bottle, debug, default_app, TEMPLATE_PATH

print 'ROOT:', PROJECT_ROOT
TEMPLATE_PATH.insert(0, PROJECT_ROOT+'/apps/main/views/')

from settings import routes

# In case of WSGI execution the following gets executed
TaskApp = Bottle()
routes.set(TaskApp)

debug(DEBUG)

# In case of execution from command line the following gets executed.
if __name__ == '__main__':
	TaskApp.run(host='0.0.0.0', port=8080, reloader=True)
else:
	application = default_app()
	routes.set(application)
