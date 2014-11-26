__author__ = ["ashwineaso"]

from bottle import Bottle 
from apps.task.views import *

task_app = Bottle()

task_app.route('/addNewTask', 'POST', addNewTask)
task_app.route('/editTask', 'POST', editTask)