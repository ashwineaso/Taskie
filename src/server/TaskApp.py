import sys
import os

from settings.constants import DEBUG

from bottle import Bottle, debug, default_app
from settings import routes

TaskApp = Bottle()
routes.set(TaskApp)

debug(DEBUG)

if __name__ == '__main__':
	TaskApp.run(host='0.0.0.0',port=8080,reloader=True)
else:
	TaskApp = default_app()
routes.set(TaskApp)

__author__ = 'mahesmohan'