__author__ = ['mahesmohan', 'ashwineaso']

import sys
import os

from settings.constants import DEBUG, PROJECT_ROOT, PHOTOS_DIRECTORY, PHOTOS_DEBUG_DIRECTORY, ATTACHMENT_DIRECTORY

print 'ROOT:', PROJECT_ROOT

from bottle import Bottle, debug, default_app
from settings import routes

# In case of WSGI execution the following gets executed
TaskApp = Bottle()
routes.set(TaskApp)

debug(DEBUG)

# In case of execution from command line the following gets executed.
if __name__ == '__main__':
	TaskApp.run(host='0.0.0.0', port=8080, reloader=True)
else:
	TaskApp = default_app()
routes.set(TaskApp)
